package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.DataUtil;

/**
 * Cloud client connector for MQTT-based cloud services.
 * Implements ICloudClient and IConnectionListener interfaces.
 */
public class CloudClientConnector implements ICloudClient, IConnectionListener
{
    private static final Logger _Logger = Logger.getLogger(CloudClientConnector.class.getName());
    
    // Private instance variables
    private String topicPrefix = "";
    private MqttClientConnector mqttClient = null;
    private IDataMessageListener dataMsgListener = null;
    private boolean isConnected = false;
    
    // Constructors
    
    /**
     * Default constructor.
     * Initializes the cloud client connector with cloud gateway service configuration.
     */
    public CloudClientConnector()
    {
        super();
        
        ConfigUtil configUtil = ConfigUtil.getInstance();
        
        // Load the topic prefix from configuration
        this.topicPrefix = 
            configUtil.getProperty(
                ConfigConst.CLOUD_GATEWAY_SERVICE, 
                ConfigConst.CLOUD_TOPIC_PREFIX_KEY);
        
        // If no topic prefix is configured, use an empty string
        if (this.topicPrefix == null) {
            this.topicPrefix = "";
        } else {
            // Ensure topic prefix doesn't start with '/' but ends with '/'
            if (this.topicPrefix.startsWith("/")) {
                this.topicPrefix = this.topicPrefix.substring(1);
            }
            if (!this.topicPrefix.endsWith("/")) {
                this.topicPrefix = this.topicPrefix + "/";
            }
        }
        
        // Initialize the MQTT client with cloud gateway service configuration
        this.mqttClient = new MqttClientConnector(ConfigConst.CLOUD_GATEWAY_SERVICE);
        this.mqttClient.setConnectionListener(this);
    }
    
    // Public methods from ICloudClient
    
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
            
            int qos = ConfigConst.DEFAULT_QOS;
            return this.mqttClient.subscribeToTopic(topicName, qos);
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
    
    // Public methods from IConnectionListener
    
    @Override
    public void onConnect(boolean isReconnect)
    {
        _Logger.info("Cloud service connection established (reconnect = " + isReconnect + ")");
        this.isConnected = true;
    }
    
    @Override
    public void onDisconnect()
    {
        _Logger.warning("Cloud service connection lost");
        this.isConnected = false;
    }
    
    // Private methods
    
    /**
     * Publishes a message to the cloud service.
     * 
     * @param resource The resource enum
     * @param payload The message payload
     * @return True if successful, false otherwise
     */
    private boolean publishMessageToCloud(ResourceNameEnum resource, String payload)
    {
        if (this.isConnected) {
            String topicName = createTopicName(resource);
            int qos = ConfigConst.DEFAULT_QOS;
            
            _Logger.fine("Publishing message to cloud topic: " + topicName);
            return this.mqttClient.publishMessage(topicName, payload, qos);
        } else {
            _Logger.warning("Not connected to cloud service. Cannot publish message.");
            return false;
        }
    }
    
    /**
     * Creates a topic name by combining the topic prefix with the resource name.
     * 
     * @param resource The resource enum
     * @return The complete topic name
     */
    private String createTopicName(ResourceNameEnum resource)
    {
        return this.topicPrefix + resource.getResourceName();
    }
}