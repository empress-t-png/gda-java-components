package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.ICloudClient;          // ✅ fixed import
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.DataUtil;

/**
 * CloudClientConnector implements ICloudClient and manages cloud MQTT communication.
 */
public class CloudClientConnector implements ICloudClient, IConnectionListener
{
    private static final Logger _Logger = Logger.getLogger(CloudClientConnector.class.getName());

    private String topicPrefix = "";
    private MqttClientConnector mqttClient = null;
    private IDataMessageListener dataMsgListener = null;
    private boolean isConnected = false;

    public CloudClientConnector()
    {
        ConfigUtil configUtil = ConfigUtil.getInstance();

        this.topicPrefix = configUtil.getProperty(
            ConfigConst.CLOUD_GATEWAY_SERVICE,
            ConfigConst.CLOUD_TOPIC_PREFIX_KEY,
            "");

        if (this.topicPrefix.startsWith("/")) {
            this.topicPrefix = this.topicPrefix.substring(1);
        }
        if (!this.topicPrefix.endsWith("/") && !this.topicPrefix.isEmpty()) {
            this.topicPrefix += "/";
        }

        this.mqttClient = new MqttClientConnector(true);
        this.mqttClient.setConnectionListener(this);
    }

    @Override
    public boolean connectClient()
    {
        _Logger.info("Connecting to cloud service...");
        return this.mqttClient.connectClient();
    }

    @Override
    public boolean disconnectClient()
    {
        _Logger.info("Disconnecting from cloud service...");
        return this.mqttClient.disconnectClient();
    }

    @Override
    public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data)
    {
        if (data != null && resource != null) {
            String payload = DataUtil.getInstance().sensorDataToJson(data);
            return publishMessageToCloud(resource, payload);
        }
        _Logger.warning("SensorData or resource is null. Cannot send to cloud.");
        return false;
    }

    @Override
    public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data)
    {
        if (data != null && resource != null) {
            String payload = DataUtil.getInstance().systemPerformanceDataToJson(data);
            return publishMessageToCloud(resource, payload);
        }
        _Logger.warning("SystemPerformanceData or resource is null. Cannot send to cloud.");
        return false;
    }

    @Override
    public boolean subscribeToCloudEvents(ResourceNameEnum resource)
    {
        if (resource != null) {
            String topicName = createTopicName(resource);
            _Logger.info("Subscribing to cloud topic: " + topicName);
            return this.mqttClient.subscribeToTopic(topicName, ConfigConst.DEFAULT_QOS);
        }
        _Logger.warning("Resource is null. Cannot subscribe to cloud events.");
        return false;
    }

    @Override
    public boolean unsubscribeFromCloudEvents(ResourceNameEnum resource)
    {
        if (resource != null) {
            String topicName = createTopicName(resource);
            _Logger.info("Unsubscribing from cloud topic: " + topicName);
            return this.mqttClient.unsubscribeFromTopic(topicName);
        }
        _Logger.warning("Resource is null. Cannot unsubscribe from cloud events.");
        return false;
    }

    @Override
    public boolean setDataMessageListener(IDataMessageListener listener)
    {
        if (listener != null) {
            this.dataMsgListener = listener;
            this.mqttClient.setDataMessageListener(listener);
            return true;
        }
        _Logger.warning("Data message listener is null. Ignoring.");
        return false;
    }

    @Override
    public void onConnect(boolean isReconnect)
    {
        _Logger.info("Cloud service connection established (reconnect = " + isReconnect + ")");
        this.isConnected = true;
        handleCloudSubscriptions();
    }

    @Override
    public void onDisconnect()
    {
        _Logger.warning("Cloud service connection lost");
        this.isConnected = false;
    }

    private boolean publishMessageToCloud(ResourceNameEnum resource, String payload)
    {
        if (this.isConnected) {
            String topicName = createTopicName(resource);
            _Logger.fine("Publishing message to cloud topic: " + topicName);
            return this.mqttClient.publishMessage(topicName, payload, ConfigConst.DEFAULT_QOS);
        }
        _Logger.warning("Not connected to cloud service. Cannot publish message.");
        return false;
    }

    private String createTopicName(ResourceNameEnum resource)
    {
        return this.topicPrefix + resource.getResourceName();
    }

    private String createTopicName(String deviceName, String itemName)
    {
        return this.topicPrefix + deviceName + "/" + itemName;
    }

    private void handleCloudSubscriptions()
    {
        _Logger.info("Handling cloud service subscriptions for LED actuation events...");

        LedEnablementMessageListener ledListener = new LedEnablementMessageListener(this.dataMsgListener);

        ActuatorData ad = new ActuatorData();
        ad.setAsResponse();
        ad.setName(ConfigConst.LED_ACTUATOR_NAME);
        ad.setValue(-1.0f);

        String ledTopic = createTopicName(ConfigConst.CONSTRAINED_DEVICE, ad.getName());
        String adJson = DataUtil.getInstance().actuatorDataToJson(ad);

        this.mqttClient.publishMessage(ledTopic, adJson, ConfigConst.DEFAULT_QOS);
        this.mqttClient.subscribeToTopic(ledTopic, ConfigConst.DEFAULT_QOS, ledListener);

        _Logger.info("Subscribed to LED actuation topic: " + ledTopic);
    }

    private class LedEnablementMessageListener implements IMqttMessageListener
    {
        private IDataMessageListener dataMsgListener = null;
        private ResourceNameEnum resource = ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE;
        private int typeID = ConfigConst.LED_ACTUATOR_TYPE;
        private String itemName = ConfigConst.LED_ACTUATOR_NAME;

        LedEnablementMessageListener(IDataMessageListener listener)
        {
            this.dataMsgListener = listener;
        }

        public ResourceNameEnum getResource()
        {
            return this.resource;
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception
        {
            try {
                String jsonData = new String(message.getPayload());
                _Logger.info("LED actuation message received from cloud: " + jsonData);

                ActuatorData actuatorData = DataUtil.getInstance().jsonToActuatorData(jsonData);
                actuatorData.setLocationID(ConfigConst.CONSTRAINED_DEVICE);
                actuatorData.setTypeID(this.typeID);
                actuatorData.setName(this.itemName);

                int val = (int) actuatorData.getValue();

                switch (val) {
                    case ConfigConst.ON_COMMAND:
                        _Logger.info("Received LED enablement message [ON].");
                        actuatorData.setStateData("LED switching ON");
                        actuatorData.setCommand(ConfigConst.ON_COMMAND);
                        break;

                    case ConfigConst.OFF_COMMAND:
                        _Logger.info("Received LED enablement message [OFF].");
                        actuatorData.setStateData("LED switching OFF");
                        actuatorData.setCommand(ConfigConst.OFF_COMMAND);
                        break;

                    default:
                        _Logger.warning("Invalid LED command value: " + val);
                        return;
                }

                if (this.dataMsgListener != null) {
                    jsonData = DataUtil.getInstance().actuatorDataToJson(actuatorData);
                    this.dataMsgListener.handleIncomingMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, jsonData);
                } else {
                    _Logger.warning("No data message listener set. Cannot forward LED command.");
                }
            } catch (Exception e) {
                _Logger.log(Level.WARNING, "Failed to process LED actuation message from cloud.", e);
            }
        }
    }
}
