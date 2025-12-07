package programmingtheiot.gda.app;

<<<<<<< Updated upstream
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
=======
>>>>>>> Stashed changes
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
<<<<<<< Updated upstream
import programmingtheiot.data.BaseIotData;
import programmingtheiot.data.DataUtil;
=======
>>>>>>> Stashed changes
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.DataUtil;

import programmingtheiot.gda.connection.MqttClientConnector;

<<<<<<< Updated upstream
import programmingtheiot.gda.system.SystemPerformanceManager;

/**
 * Implementation of DeviceDataManager for Lab Module 10.
 *
 */
public class DeviceDataManager implements IDataMessageListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager.class.getName());
	
	// private var's
	
	private boolean enableMqttClient = true;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	private boolean enableSystemPerf = false;
	
	private IActuatorDataListener actuatorDataListener = null;
	private MqttClientConnector mqttClient = null;
	private IPubSubClient cloudClient = null;
	private IPersistenceClient persistenceClient = null;
	private IRequestResponseClient smtpClient = null;
	private CoapServerGateway coapServer = null;
	private SystemPerformanceManager sysPerfMgr = null;
	
	// Humidity threshold crossing variables
	private ActuatorData   latestHumidifierActuatorData = null;
	private ActuatorData   latestHumidifierActuatorResponse = null;
	private SensorData     latestHumiditySensorData = null;
	private OffsetDateTime latestHumiditySensorTimeStamp = null;
	
	private boolean handleHumidityChangeOnDevice = false;
	private int     lastKnownHumidifierCommand   = ConfigConst.OFF_COMMAND;
	
	private long    humidityMaxTimePastThreshold = 300; // seconds
	private float   nominalHumiditySetting   = 40.0f;
	private float   triggerHumidifierFloor   = 30.0f;
	private float   triggerHumidifierCeiling = 50.0f;
	
	// constructors
	
	public DeviceDataManager()
	{
		super();
		
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.enableMqttClient =
			configUtil.getBoolean(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
		
		this.enableCoapServer =
			configUtil.getBoolean(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
		
		this.enableCloudClient =
			configUtil.getBoolean(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
		
		this.enableSmtpClient =
			configUtil.getBoolean(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SMTP_CLIENT_KEY);
		
		this.enablePersistenceClient =
			configUtil.getBoolean(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
		
		// Load humidity threshold configuration
		this.handleHumidityChangeOnDevice =
			configUtil.getBoolean(
				ConfigConst.GATEWAY_DEVICE, "handleHumidityChangeOnDevice");
		
		this.humidityMaxTimePastThreshold =
			configUtil.getInteger(
				ConfigConst.GATEWAY_DEVICE, "humidityMaxTimePastThreshold");
		
		this.nominalHumiditySetting =
			configUtil.getFloat(
				ConfigConst.GATEWAY_DEVICE, "nominalHumiditySetting");
		
		this.triggerHumidifierFloor =
			configUtil.getFloat(
				ConfigConst.GATEWAY_DEVICE, "triggerHumidifierFloor");
		
		this.triggerHumidifierCeiling =
			configUtil.getFloat(
				ConfigConst.GATEWAY_DEVICE, "triggerHumidifierCeiling");
		
		// Basic validation for timing
		if (this.humidityMaxTimePastThreshold < 10 || this.humidityMaxTimePastThreshold > 7200) {
			this.humidityMaxTimePastThreshold = 300;
		}
		
		initConnections();
	}
	
	public DeviceDataManager(
		boolean enableMqttClient,
		boolean enableCoapClient,
		boolean enableCloudClient,
		boolean enableSmtpClient,
		boolean enablePersistenceClient)
	{
		super();
		
		initConnections();
	}
	
	
	// public methods
	
	@Override
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
	{
		if (data != null) {
			_Logger.info("Handling actuator response: " + data.getName());
			
			if (data.hasError()) {
				_Logger.warning("Error flag set for ActuatorData instance.");
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean handleActuatorCommandRequest(ResourceNameEnum resourceName, ActuatorData data)
	{
		if (data != null) {
			_Logger.info("Handling actuator command: " + data.getName());
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
	{
		if (msg != null) {
			_Logger.info("Handling incoming generic message: " + msg);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
	{
		if (data != null) {
			_Logger.fine("Handling sensor message: " + data.getName());
			
			if (data.hasError()) {
				_Logger.warning("Error flag set for SensorData instance.");
			}
			
			String jsonData = DataUtil.getInstance().sensorDataToJson(data);
			
			_Logger.fine("JSON [SensorData] -> " + jsonData);
			
			int qos = ConfigConst.DEFAULT_QOS;
			
			if (this.enablePersistenceClient && this.persistenceClient != null) {
				this.persistenceClient.storeData(resourceName.getResourceName(), qos, data);
			}
			
			this.handleIncomingDataAnalysis(resourceName, data);
			
			this.handleUpstreamTransmission(resourceName, jsonData, qos);
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
	{
		if (data != null) {
			_Logger.info("Handling system performance message: " + data.getName());
			
			if (data.hasError()) {
				_Logger.warning("Error flag set for SystemPerformanceData instance.");
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public void setActuatorDataListener(String name, IActuatorDataListener listener)
	{
		if (listener != null) {
			this.actuatorDataListener = listener;
		}
	}
	
	public void startManager()
	{
		_Logger.info("Starting DeviceDataManager...");
		
		if (this.mqttClient != null) {
			if (this.mqttClient.connectClient()) {
				_Logger.info("Successfully connected MQTT client to broker.");
			} else {
				_Logger.severe("Failed to connect MQTT client to broker.");
			}
		}
		
		// START CoAP server
		if (this.enableCoapServer && this.coapServer != null) {
			if (this.coapServer.startServer()) {
				_Logger.info("CoAP server started.");
			} else {
				_Logger.severe("Failed to start CoAP server. Check log file for details.");
			}
		}
		
		if (this.sysPerfMgr != null) {
			this.sysPerfMgr.startManager();
		}
	}
	
	public void stopManager()
	{
		_Logger.info("Stopping DeviceDataManager...");
		
		if (this.sysPerfMgr != null) {
			this.sysPerfMgr.stopManager();
		}
		
		// STOP CoAP server
		if (this.enableCoapServer && this.coapServer != null) {
			if (this.coapServer.stopServer()) {
				_Logger.info("CoAP server stopped.");
			} else {
				_Logger.severe("Failed to stop CoAP server. Check log file for details.");
			}
		}
		
		if (this.mqttClient != null) {
			if (this.mqttClient.disconnectClient()) {
				_Logger.info("Successfully disconnected MQTT client from broker.");
			} else {
				_Logger.severe("Failed to disconnect MQTT client from broker.");
			}
		}
	}
	
	// private methods
	
	/**
	 * Initializes the enabled connections. This will NOT start them, but only create the
	 * instances that will be used in the {@link #startManager() and #stopManager()) methods.
	 * 
	 */
	private void initConnections()
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.enableSystemPerf =
			configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SYSTEM_PERF_KEY);
		
		if (this.enableSystemPerf) {
			this.sysPerfMgr = new SystemPerformanceManager();
			this.sysPerfMgr.setDataMessageListener(this);
		}
		
		if (this.enableMqttClient) {
			_Logger.info("MQTT client is ENABLED - creating MQTT client connector...");
			this.mqttClient = new MqttClientConnector();
			this.mqttClient.setDataMessageListener(this);
		}
		
		// CREATE CoAP server instance
		if (this.enableCoapServer) {
			_Logger.info("CoAP server is ENABLED - initializing CoAP server...");
			this.coapServer = new CoapServerGateway(this);
		}
	}
	
	private void handleIncomingDataAnalysis(ResourceNameEnum resource, SensorData data)
	{
		// Check if this is humidity data
		if (data.getTypeID() == ConfigConst.HUMIDITY_SENSOR_TYPE) {
			handleHumiditySensorAnalysis(resource, data);
		}
	}
	
	private void handleHumiditySensorAnalysis(ResourceNameEnum resource, SensorData data)
	{
		_Logger.fine("Analyzing humidity data from CDA: " + data.getLocationID() + ". Value: " + data.getValue());
		
		boolean isLow  = data.getValue() < this.triggerHumidifierFloor;
		boolean isHigh = data.getValue() > this.triggerHumidifierCeiling;
		
		if (isLow || isHigh) {
			_Logger.fine("Humidity data from CDA exceeds nominal range.");
			
			if (this.latestHumiditySensorData == null) {
				// Set properties then exit - nothing more to do until the next sample
				this.latestHumiditySensorData = data;
				this.latestHumiditySensorTimeStamp = getDateTimeFromData(data);
				
				_Logger.fine(
					"Starting humidity nominal exception timer. Waiting for seconds: " +
					this.humidityMaxTimePastThreshold);
				
				return;
			} else {
				OffsetDateTime curHumiditySensorTimeStamp = getDateTimeFromData(data);
				
				long diffSeconds =
					ChronoUnit.SECONDS.between(
						this.latestHumiditySensorTimeStamp, curHumiditySensorTimeStamp);
				
				_Logger.fine("Checking Humidity value exception time delta: " + diffSeconds);
				
				if (diffSeconds >= this.humidityMaxTimePastThreshold) {
					ActuatorData ad = new ActuatorData();
					ad.setName(ConfigConst.HUMIDIFIER_ACTUATOR_NAME);
					ad.setLocationID(data.getLocationID());
					ad.setTypeID(ConfigConst.HUMIDIFIER_ACTUATOR_TYPE);
					ad.setValue(this.nominalHumiditySetting);
					
					if (isLow) {
						ad.setCommand(ConfigConst.ON_COMMAND);
					} else if (isHigh) {
						ad.setCommand(ConfigConst.OFF_COMMAND);
					}
					
					_Logger.info(
						"Humidity exceptional value reached. Sending actuation event to CDA: " +
						ad);
					
					this.lastKnownHumidifierCommand = ad.getCommand();
					sendActuatorCommandToCda(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, ad);
					
					// Set ActuatorData and reset SensorData (and timestamp)
					this.latestHumidifierActuatorData = ad;
					this.latestHumiditySensorData = null;
					this.latestHumiditySensorTimeStamp = null;
				}
			}
		} else if (this.lastKnownHumidifierCommand == ConfigConst.ON_COMMAND) {
			// Check if we need to turn off the humidifier
			if (this.latestHumidifierActuatorData != null) {
				// Check the value - if the humidifier is on, but not yet at nominal, keep it on
				if (this.latestHumidifierActuatorData.getValue() >= this.nominalHumiditySetting) {
					this.latestHumidifierActuatorData.setCommand(ConfigConst.OFF_COMMAND);
					
					_Logger.info(
						"Humidity nominal value reached. Sending OFF actuation event to CDA: " +
						this.latestHumidifierActuatorData);
					
					sendActuatorCommandToCda(
						ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, this.latestHumidifierActuatorData);
					
					// Reset ActuatorData and SensorData (and timestamp)
					this.lastKnownHumidifierCommand = this.latestHumidifierActuatorData.getCommand();
					this.latestHumidifierActuatorData = null;
					this.latestHumiditySensorData = null;
					this.latestHumiditySensorTimeStamp = null;
				} else {
					_Logger.fine("Humidifier is still on. Not yet at nominal levels (OK).");
				}
			} else {
				_Logger.warning(
					"ERROR: ActuatorData for humidifier is null (shouldn't be). Can't send command.");
			}
		}
	}
	
	private void sendActuatorCommandToCda(ResourceNameEnum resource, ActuatorData data)
	{
		// NOTE: This is how an ActuatorData command will get passed to the CDA
		// when the GDA is providing the CoAP server and hosting the appropriate
		// ActuatorData resource.
		if (this.actuatorDataListener != null) {
			this.actuatorDataListener.onActuatorDataUpdate(data);
		}
		
		// NOTE: This is how an ActuatorData command will get passed to the CDA
		// when using MQTT to communicate between the GDA and CDA
		if (this.enableMqttClient && this.mqttClient != null) {
			String jsonData = DataUtil.getInstance().actuatorDataToJson(data);
			
			if (this.mqttClient.publishMessage(resource, jsonData, ConfigConst.DEFAULT_QOS)) {
				_Logger.info(
					"Published ActuatorData command from GDA to CDA: " + data.getCommand());
			} else {
				_Logger.warning(
					"Failed to publish ActuatorData command from GDA to CDA: " + data.getCommand());
			}
		}
	}
	
	private OffsetDateTime getDateTimeFromData(BaseIotData data)
	{
		OffsetDateTime odt = null;
		
		try {
			odt = OffsetDateTime.parse(data.getTimeStamp());
		} catch (Exception e) {
			_Logger.warning(
				"Failed to extract ISO 8601 timestamp from IoT data. Using local current time.");
			
			// This won't be accurate, but should be reasonably close
			odt = OffsetDateTime.now();
		}
		
		return odt;
	}
	
	private void handleUpstreamTransmission(ResourceNameEnum resource, String jsonData, int qos)
	{
		// NOTE: This will be implemented in Part 04 for cloud integration
		_Logger.info("TODO: Send JSON data to cloud service: " + resource);
	}
}
=======
public class DeviceDataManager implements IDataMessageListener
{
    private Logger _Logger = Logger.getLogger(DeviceDataManager.class.getName());

    private ConfigUtil configUtil = null;
    private MqttClientConnector mqttClient = null;

    private float triggerHvacTempFloor = 18.0f;
    private float triggerHvacTempCeiling = 22.0f;

    public DeviceDataManager()
    {
        this.configUtil = ConfigUtil.getInstance();
        this.mqttClient = new MqttClientConnector();
        this.mqttClient.setDataMessageListener(this);

        this.triggerHvacTempFloor = this.configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, ConfigConst.TRIGGER_HVAC_TEMP_FLOOR_KEY, 18.0f);
        this.triggerHvacTempCeiling = this.configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, ConfigConst.TRIGGER_HVAC_TEMP_CEILING_KEY, 22.0f);
    }

    public void startManager()
    {
        if (this.mqttClient.connectClient()) {
            _Logger.info("MQTT client connected.");
        } else {
            _Logger.severe("MQTT client failed to connect.");
        }
    }

    public void stopManager()
    {
        if (this.mqttClient.disconnectClient()) {
            _Logger.info("MQTT client disconnected.");
        } else {
            _Logger.warning("MQTT client failed to disconnect.");
        }
    }

    @Override
    public void handleIncomingMessage(ResourceNameEnum resource, String msg)
    {
        _Logger.info("Received message on resource: " + resource);

        if (resource == ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE) {
            SensorData sensorData = DataUtil.getInstance().jsonToSensorData(msg);
            handleSensorData(sensorData);
        } else if (resource == ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE) {
            SystemPerformanceData sysPerfData = DataUtil.getInstance().jsonToSystemPerformanceData(msg);
            _Logger.info("SystemPerformanceData received: CPU=" + sysPerfData.getCpuUtilization() + " Mem=" + sysPerfData.getMemoryUtilization());
        }
    }

    private void handleSensorData(SensorData data)
    {
        if (data.getTypeID() == ConfigConst.TEMP_SENSOR_TYPE) {
            float temp = data.getValue();
            ActuatorData ad = new ActuatorData();
            ad.setTypeID(ConfigConst.HVAC_ACTUATOR_TYPE);
            ad.setAsResponse();

            if (temp < this.triggerHvacTempFloor) {
                ad.setCommand(ConfigConst.COMMAND_ON);
                ad.setStateData("Heating ON");
                ad.setValue(this.triggerHvacTempFloor);
                _Logger.info("Temperature below floor. Sending Heating ON command.");
            } else if (temp > this.triggerHvacTempCeiling) {
                ad.setCommand(ConfigConst.COMMAND_ON);
                ad.setStateData("Cooling ON");
                ad.setValue(this.triggerHvacTempCeiling);
                _Logger.info("Temperature above ceiling. Sending Cooling ON command.");
            } else {
                ad.setCommand(ConfigConst.COMMAND_OFF);
                ad.setStateData("HVAC OFF");
                ad.setValue(0.0f);
                _Logger.info("Temperature normal. Sending HVAC OFF command.");
            }

            String adJson = DataUtil.getInstance().actuatorDataToJson(ad);
            this.mqttClient.publishMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, adJson, ConfigConst.DEFAULT_QOS);
        }
    }

    @Override
    public void handleActuatorCommandMessage(ActuatorData data)
    {
        _Logger.info("Actuator command received: " + data.getStateData());
    }

    @Override
    public boolean handleActuatorCommandResponse(ResourceNameEnum resource, ActuatorData data)
    {
        _Logger.info("Actuator response received: " + data.getStateData());
        return true;
    }
}
>>>>>>> Stashed changes
