package programmingtheiot.gda.connection;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.DataUtil;

public class MqttClientConnector implements MqttCallbackExtended
{
    private static final Logger _Logger = Logger.getLogger(MqttClientConnector.class.getName());

    // params
    private String protocol = ConfigConst.DEFAULT_MQTT_PROTOCOL;
    private String host = ConfigConst.DEFAULT_HOST;
    private int port = ConfigConst.DEFAULT_MQTT_PORT;
    private int brokerKeepAlive = ConfigConst.DEFAULT_KEEP_ALIVE;
    private String clientID = null;
    private String brokerAddr = null;
    
    private String pemFileName = null;
    private boolean enableEncryption = false;
    private boolean useCleanSession = false;
    private boolean enableAutoReconnect = true;
    
    private MqttAsyncClient mqttClient = null;
    private MqttConnectOptions connOpts = null;
    private MemoryPersistence persistence = null;
    private IDataMessageListener dataMsgListener = null;

    // constructors
    
    public MqttClientConnector()
    {
        super();
        initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
    }

    // public methods
    
    public boolean connectClient()
    {
        try {
            if (!this.mqttClient.isConnected()) {
                _Logger.info("Connecting to MQTT broker: " + this.brokerAddr);
                this.mqttClient.connect(this.connOpts).waitForCompletion();
                return true;
            }
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Connect failed.", e);
        }
        return false;
    }

    public boolean disconnectClient()
    {
        try {
            if (this.mqttClient.isConnected()) {
                _Logger.info("Disconnecting from MQTT broker: " + this.brokerAddr);
                this.mqttClient.disconnect().waitForCompletion();
                return true;
            }
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Disconnect failed.", e);
        }
        return false;
    }

    public boolean publishMessage(ResourceNameEnum topic, String payload, int qos)
    {
        return publishMessage(topic.getResourceName(), payload, qos);
    }

    public boolean publishMessage(String topic, String payload, int qos)
    {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            this.mqttClient.publish(topic, message);
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Publish failed to topic: " + topic, e);
        }
        return false;
    }

    public boolean subscribeToTopic(ResourceNameEnum topic, int qos)
    {
        return subscribeToTopic(topic.getResourceName(), qos);
    }

    public boolean subscribeToTopic(String topic, int qos)
    {
        try {
            this.mqttClient.subscribe(topic, qos);
            _Logger.info("Subscribed to topic: " + topic);
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Subscribe failed to topic: " + topic, e);
        }
        return false;
    }

    public boolean unsubscribeFromTopic(ResourceNameEnum topic)
    {
        return unsubscribeFromTopic(topic.getResourceName());
    }

    public boolean unsubscribeFromTopic(String topic)
    {
        try {
            this.mqttClient.unsubscribe(topic);
            _Logger.info("Unsubscribed from topic: " + topic);
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Unsubscribe failed from topic: " + topic, e);
        }
        return false;
    }

    public void setDataMessageListener(IDataMessageListener listener)
    {
        this.dataMsgListener = listener;
    }

    // callbacks
    
    @Override
    public void connectComplete(boolean reconnect, String serverURI)
    {
        _Logger.info("Connected to broker: " + serverURI + " (reconnect = " + reconnect + ")");
        
        // Subscribe to CDA topics using custom message listeners
        int qos = ConfigConst.DEFAULT_QOS;
        
        try {
            _Logger.info("Subscribing to topic: " + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
            
            this.mqttClient.subscribe(
                ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
                qos,
                new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));

            _Logger.info("Subscribing to topic: " + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
            
            this.mqttClient.subscribe(
                ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
                qos,
                new SensorDataMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
            
            _Logger.info("Subscribing to topic: " + ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
            
            this.mqttClient.subscribe(
                ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
                qos,
                new SystemPerformanceDataMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));
                
        } catch (MqttException e) {
            _Logger.log(Level.WARNING, "Failed to subscribe to CDA topics.", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause)
    {
        _Logger.warning("Connection lost: " + cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        // TEMPORARILY COMMENTED OUT FOR PERFORMANCE TESTING
        // _Logger.info("Delivery complete for token: " + token.getMessageId());
    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception
    {
        String payload = new String(msg.getPayload());
        _Logger.info("Message arrived on topic: " + topic);

        if (this.dataMsgListener != null) {
            ResourceNameEnum resource = ResourceNameEnum.getEnumFromValue(topic);
            if (resource != null) {
                this.dataMsgListener.handleIncomingMessage(resource, payload);
            }
        }
    }
    
    // private methods
    
    private void initClientParameters(String configSectionName)
    {
        ConfigUtil configUtil = ConfigUtil.getInstance();
        
        this.host =
            configUtil.getProperty(
                configSectionName, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
        this.port =
            configUtil.getInteger(
                configSectionName, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
        this.brokerKeepAlive =
            configUtil.getInteger(
                configSectionName, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
        this.enableEncryption =
            configUtil.getBoolean(
                configSectionName, ConfigConst.ENABLE_CRYPT_KEY);
        this.pemFileName =
            configUtil.getProperty(
                configSectionName, ConfigConst.CERT_FILE_KEY);

        this.clientID = MqttClient.generateClientId();
        
        this.persistence = new MemoryPersistence();
        this.connOpts = new MqttConnectOptions();
        
        this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
        this.connOpts.setCleanSession(this.useCleanSession);
        this.connOpts.setAutomaticReconnect(this.enableAutoReconnect);
        
        if (this.enableEncryption) {
            initSecureConnectionParameters(configSectionName);
        }
        
        if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY)) {
            initCredentialConnectionParameters(configSectionName);
        }
        
        this.brokerAddr = this.protocol + "://" + this.host + ":" + this.port;
        
        _Logger.info("Using URL for broker conn: " + this.brokerAddr);
        
        try {
            this.mqttClient = new MqttAsyncClient(this.brokerAddr, this.clientID, this.persistence);
            this.mqttClient.setCallback(this);
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to create MQTT client.", e);
        }
    }
    
    private void initCredentialConnectionParameters(String configSectionName)
    {
        ConfigUtil configUtil = ConfigUtil.getInstance();
        
        try {
            _Logger.info("Checking if credentials file exists and is loadable...");
            
            Properties props = configUtil.getCredentials(configSectionName);
            
            if (props != null) {
                this.connOpts.setUserName(props.getProperty(ConfigConst.USER_NAME_TOKEN_KEY, ""));
                this.connOpts.setPassword(props.getProperty(ConfigConst.USER_AUTH_TOKEN_KEY, "").toCharArray());
                
                _Logger.info("Credentials now set.");
            } else {
                _Logger.warning("No credentials are set.");
            }
        } catch (Exception e) {
            _Logger.log(Level.WARNING, "Credential file non-existent. Disabling auth requirement.");
        }
    }
    
    private void initSecureConnectionParameters(String configSectionName)
    {
        ConfigUtil configUtil = ConfigUtil.getInstance();
        
        try {
            _Logger.info("Configuring TLS...");
            
            if (this.pemFileName != null) {
                File file = new File(this.pemFileName);
                
                if (file.exists()) {
                    _Logger.info("PEM file valid. Using secure connection: " + this.pemFileName);
                } else {
                    this.enableEncryption = false;
                    _Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + this.pemFileName);
                    return;
                }
            }
            
            this.port =
                configUtil.getInteger(
                    configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
            
            this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;
            
            _Logger.info("TLS configuration prepared.");
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
            this.enableEncryption = false;
        }
    }
    
    // Inner classes for message-specific handling
    
    private class ActuatorResponseMessageListener implements IMqttMessageListener
    {
        private ResourceNameEnum resource = null;
        private IDataMessageListener dataMsgListener = null;
        
        ActuatorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
        {
            this.resource = resource;
            this.dataMsgListener = dataMsgListener;
        }
        
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception
        {
            try {
                ActuatorData actuatorData =
                    DataUtil.getInstance().jsonToActuatorData(new String(message.getPayload()));
                
                _Logger.info("Received ActuatorData response: " + actuatorData.getName() + " = " + actuatorData.getValue());
                    
                if (this.dataMsgListener != null) {
                    this.dataMsgListener.handleActuatorCommandResponse(resource, actuatorData);
                }
            } catch (Exception e) {
                _Logger.log(Level.WARNING, "Failed to convert message payload to ActuatorData.", e);
            }
        }
    }
    
    private class SensorDataMessageListener implements IMqttMessageListener
    {
        private ResourceNameEnum resource = null;
        private IDataMessageListener dataMsgListener = null;
        
        SensorDataMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
        {
            this.resource = resource;
            this.dataMsgListener = dataMsgListener;
        }
        
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception
        {
            try {
                SensorData sensorData =
                    DataUtil.getInstance().jsonToSensorData(new String(message.getPayload()));
                
                _Logger.info("Received SensorData: " + sensorData.getName() + " = " + sensorData.getValue());
                
                if (this.dataMsgListener != null) {
                    this.dataMsgListener.handleSensorMessage(resource, sensorData);
                }
            } catch (Exception e) {
                _Logger.log(Level.WARNING, "Failed to convert message payload to SensorData.", e);
            }
        }
    }
    
    private class SystemPerformanceDataMessageListener implements IMqttMessageListener
    {
        private ResourceNameEnum resource = null;
        private IDataMessageListener dataMsgListener = null;
        
        SystemPerformanceDataMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
        {
            this.resource = resource;
            this.dataMsgListener = dataMsgListener;
        }
        
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception
        {
            try {
                SystemPerformanceData sysPerfData =
                    DataUtil.getInstance().jsonToSystemPerformanceData(new String(message.getPayload()));
                
                _Logger.info("Received SystemPerformanceData: CPU=" + sysPerfData.getCpuUtilization() + 
                           "%, Memory=" + sysPerfData.getMemoryUtilization() + "%");
                
                if (this.dataMsgListener != null) {
                    this.dataMsgListener.handleSystemPerformanceMessage(resource, sysPerfData);
                }
            } catch (Exception e) {
                _Logger.log(Level.WARNING, "Failed to convert message payload to SystemPerformanceData.", e);
            }
        }
    }
}