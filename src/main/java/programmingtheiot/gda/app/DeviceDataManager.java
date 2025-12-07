package programmingtheiot.gda.app;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import programmingtheiot.common.IActuatorDataListener;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.BaseIotData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.DataUtil;

import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;

public class DeviceDataManager implements IDataMessageListener
{
    private static final Logger _Logger = Logger.getLogger(DeviceDataManager.class.getName());

    private boolean enableMqttClient = true;
    private boolean enableSystemPerf = false;

    private MqttClientConnector mqttClient = null;
    private SystemPerformanceManager sysPerfMgr = null;
    private IActuatorDataListener actuatorDataListener = null;

    private ActuatorData latestHumidifierActuatorData = null;
    private SensorData latestHumiditySensorData = null;
    private OffsetDateTime latestHumiditySensorTimeStamp = null;

    private int lastKnownHumidifierCommand = ConfigConst.OFF_COMMAND;
    private long humidityMaxTimePastThreshold = 300;
    private float nominalHumiditySetting = 40.0f;
    private float triggerHumidifierFloor = 30.0f;
    private float triggerHumidifierCeiling = 50.0f;

    public DeviceDataManager()
    {
        ConfigUtil configUtil = ConfigUtil.getInstance();

        this.enableMqttClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
        this.enableSystemPerf = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SYSTEM_PERF_KEY);

        this.humidityMaxTimePastThreshold = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE, "humidityMaxTimePastThreshold", 300);
        this.nominalHumiditySetting = configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "nominalHumiditySetting", 40.0f);
        this.triggerHumidifierFloor = configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "triggerHumidifierFloor", 30.0f);
        this.triggerHumidifierCeiling = configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "triggerHumidifierCeiling", 50.0f);

        initConnections();
    }

    private void initConnections()
    {
        if (this.enableSystemPerf) {
            this.sysPerfMgr = new SystemPerformanceManager();
            this.sysPerfMgr.setDataMessageListener(this);
        }
        if (this.enableMqttClient) {
            this.mqttClient = new MqttClientConnector();
            this.mqttClient.setDataMessageListener(this);
        }
    }

    public void startManager()
    {
        if (this.mqttClient != null) {
            this.mqttClient.connectClient();
        }
        if (this.sysPerfMgr != null) {
            this.sysPerfMgr.startManager();
        }
    }

    public void stopManager()
    {
        if (this.sysPerfMgr != null) {
            this.sysPerfMgr.stopManager();
        }
        if (this.mqttClient != null) {
            this.mqttClient.disconnectClient();
        }
    }

    @Override
    public boolean handleSensorMessage(ResourceNameEnum resource, SensorData data)
    {
        if (data != null) {
            _Logger.info("Received SensorData: " + data.getName());
            handleIncomingDataAnalysis(resource, data);
            String jsonData = DataUtil.getInstance().sensorDataToJson(data);
            this.mqttClient.publishMessage(resource, jsonData, ConfigConst.DEFAULT_QOS);
            handleUpstreamTransmission(resource, jsonData, ConfigConst.DEFAULT_QOS);
            return true;
        }
        return false;
    }

    private void handleIncomingDataAnalysis(ResourceNameEnum resource, SensorData data)
    {
        if (data.getTypeID() == ConfigConst.HUMIDITY_SENSOR_TYPE) {
            handleHumiditySensorAnalysis(resource, data);
        }
    }

    private void handleHumiditySensorAnalysis(ResourceNameEnum resource, SensorData data)
    {
        boolean isLow = data.getValue() < this.triggerHumidifierFloor;
        boolean isHigh = data.getValue() > this.triggerHumidifierCeiling;

        if (isLow || isHigh) {
            if (this.latestHumiditySensorData == null) {
                this.latestHumiditySensorData = data;
                this.latestHumiditySensorTimeStamp = getDateTimeFromData(data);
                return;
            } else {
                OffsetDateTime curTime = getDateTimeFromData(data);
                long delta = ChronoUnit.SECONDS.between(this.latestHumiditySensorTimeStamp, curTime);

                if (delta >= this.humidityMaxTimePastThreshold) {
                    ActuatorData ad = new ActuatorData();
                    ad.setName(ConfigConst.HUMIDIFIER_ACTUATOR_NAME);
                    ad.setLocationID(data.getLocationID());
                    ad.setTypeID(ConfigConst.HUMIDIFIER_ACTUATOR_TYPE);
                    ad.setValue(this.nominalHumiditySetting);
                    ad.setCommand(isLow ? ConfigConst.ON_COMMAND : ConfigConst.OFF_COMMAND);

                    this.lastKnownHumidifierCommand = ad.getCommand();
                    sendActuatorCommandToCda(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, ad);

                    this.latestHumidifierActuatorData = ad;
                    this.latestHumiditySensorData = null;
                    this.latestHumiditySensorTimeStamp = null;
                }
            }
        }
    }

    private void sendActuatorCommandToCda(ResourceNameEnum resource, ActuatorData data)
    {
        if (this.actuatorDataListener != null) {
            this.actuatorDataListener.onActuatorDataUpdate(data);
        }
        String jsonData = DataUtil.getInstance().actuatorDataToJson(data);
        this.mqttClient.publishMessage(resource, jsonData, ConfigConst.DEFAULT_QOS);
    }

    private OffsetDateTime getDateTimeFromData(BaseIotData data)
    {
        try {
            return OffsetDateTime.parse(data.getTimeStamp());
        } catch (Exception e) {
            _Logger.warning("Failed to parse timestamp. Using current time.");
            return OffsetDateTime.now();
        }
    }

    @Override
    public boolean handleSystemPerformanceMessage(ResourceNameEnum resource, SystemPerformanceData data)
    {
        _Logger.info("Received SystemPerformanceData: " + data.getName());
        return true;
    }

    @Override
    public boolean handleActuatorCommandResponse(ResourceNameEnum resource, ActuatorData data)
    {
        _Logger.info("Received ActuatorData response: " + data.getName());
        return true;
    }

    @Override
    public boolean handleActuatorCommandRequest(ResourceNameEnum resource, ActuatorData data)
    {
        _Logger.info("Received ActuatorData command: " + data.getName());
        return true;
    }

    @Override
    public boolean handleIncomingMessage(ResourceNameEnum resource, String msg)
    {
        _Logger.info("Received generic message: " + msg);
        return true;
    }

    @Override
    public void setActuatorDataListener(String name, IActuatorDataListener listener)
    {
        this.actuatorDataListener = listener;
    }
    
    private void handleUpstreamTransmission(ResourceNameEnum resource, String jsonData, int qos)
    {
        // NOTE: This will be implemented in Part 04 (Cloud Integration)
        _Logger.info("Upstream transmission invoked. Checking cloud integration: " + resource.getResourceName());
    }
}