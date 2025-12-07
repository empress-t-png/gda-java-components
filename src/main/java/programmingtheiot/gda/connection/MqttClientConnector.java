/*
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License.
 */

package programmingtheiot.gda.connection;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
<<<<<<< Updated upstream
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
=======
import org.eclipse.paho.client.mqttv3.MqttCallback;
>>>>>>> Stashed changes
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.common.SimpleCertManagementUtil;
<<<<<<< Updated upstream
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.DataUtil;
=======
>>>>>>> Stashed changes

public class MqttClientConnector
{
<<<<<<< Updated upstream
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(MqttClientConnector.class.getName());
	
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
	private boolean useAsyncClient = false;
	
	private IConnectionListener connListener = null;
	private IDataMessageListener dataMsgListener = null;
	
	// NOTE: MQTT client updated to use async client vs sync client
	private MqttAsyncClient mqttClient = null;
	// private MqttClient mqttClient = null;
	
	private MqttConnectOptions connOpts = null;
	private MemoryPersistence persistence = null;
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public MqttClientConnector()
	{
		super();
		
		initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
	}
	
	
	// public methods
	
	@Override
	public boolean connectClient()
	{
		try {
			if (this.mqttClient == null) {
				// NOTE: MQTT client updated to use async client vs sync client
				this.mqttClient = new MqttAsyncClient(this.brokerAddr, this.clientID, this.persistence);
				// this.mqttClient = new MqttClient(this.brokerAddr, this.clientID, this.persistence);
				
				this.mqttClient.setCallback(this);
			}
			
			if (! this.mqttClient.isConnected()) {
				_Logger.info("MQTT client connecting to broker: " + this.brokerAddr);
				
				this.mqttClient.connect(this.connOpts);
				
				// NOTE: When using the async client, returning 'true' here doesn't mean
				// the client is actually connected - yet. Use the connectComplete() callback
				// to determine result of connectClient().
				return true;
			} else {
				_Logger.warning("MQTT client already connected to broker: " + this.brokerAddr);
			}
		} catch (MqttException e) {
			_Logger.log(Level.SEVERE, "Failed to connect MQTT client to broker.", e);
		}
		
		return false;
	}
=======
    private Logger _Logger = Logger.getLogger(MqttClientConnector.class.getName());
>>>>>>> Stashed changes

    private String host = null;
    private int port = ConfigConst.DEFAULT_MQTT_PORT;
    private String protocol = ConfigConst.DEFAULT_MQTT_PROTOCOL;
    private String brokerAddr = null;
    private String clientID = null;

    private int brokerKeepAlive = ConfigConst.DEFAULT_KEEP_ALIVE;
    private boolean enableEncryption = false;
    private boolean useCleanSession = false;
    private boolean enableAutoReconnect = true;

    private String pemFileName = null;
    private boolean useAsyncClient = false;

    private MemoryPersistence persistence = null;
    private MqttConnectOptions connOpts = null;

<<<<<<< Updated upstream
	@Override
	public boolean setConnectionListener(IConnectionListener listener)
	{
		if (listener != null) {
			this.connListener = listener;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		return false;
	}
	
	// callbacks
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
		
		if (this.connListener != null) {
			this.connListener.onConnect();
		}
		
		int qos = 1;
		
		// Subscribe to CDA topics using inner class listeners (Option 2)
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
	public void connectionLost(Throwable t)
	{
		_Logger.log(Level.WARNING, "Lost connection to MQTT broker: " + this.brokerAddr, t);
		
		if (this.connListener != null) {
			this.connListener.onDisconnect();
		}
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		_Logger.info("Delivered MQTT message with ID: " + token.getMessageId());
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		try {
			_Logger.info("MQTT message arrived on topic: '" + topic + "'");
			
			String payload = new String(msg.getPayload());
			_Logger.fine("MQTT message payload: " + payload);
			
			// Pass the message to the data message listener if it exists
			if (this.dataMsgListener != null) {
				ResourceNameEnum resourceType = ResourceNameEnum.getEnumFromValue(topic);
				
				if (resourceType != null) {
					this.dataMsgListener.handleIncomingMessage(resourceType, payload);
				} else {
					_Logger.warning("Unknown topic for incoming message: " + topic);
				}
			} else {
				_Logger.warning("No data message listener registered. Ignoring message on topic: " + topic);
			}
		} catch (Exception e) {
			_Logger.log(Level.WARNING, "Error processing MQTT message on topic: " + topic, e);
		}
	}
	
	// private methods
	
	/**
	 * Called by the constructor to set the MQTT client parameters to be used for the connection.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
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
		
		// This next config file boolean property is optional
		this.useAsyncClient =
		    configUtil.getBoolean(
		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.USE_ASYNC_CLIENT_KEY);

		// NOTE: updated from Lab Module 07 - attempt to load clientID from configuration file
		this.clientID =
			configUtil.getProperty(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.DEVICE_LOCATION_ID_KEY, MqttClient.generateClientId());
		
		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(this.useCleanSession);
		this.connOpts.setAutomaticReconnect(this.enableAutoReconnect);
		
		// if encryption is enabled, try to load and apply the cert(s)
		if (this.enableEncryption) {
			initSecureConnectionParameters(configSectionName);
		}
		
		// if there's a credential file, try to load and apply them
		if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY)) {
			initCredentialConnectionParameters(configSectionName);
		}
		
		// NOTE: URL does not have a protocol handler for "tcp" or "ssl",
		// so construct the URL manually
		this.brokerAddr  = this.protocol + "://" + this.host + ":" + this.port;
		
		_Logger.info("Using URL for broker conn: " + this.brokerAddr);
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to load credentials.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
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
	
	/**
	 * Called by {@link #initClientParameters(String)} to enable encryption.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
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
					
					_Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + this.pemFileName, new Exception());
					
					return;
				}
			}
			
			SSLSocketFactory sslFactory =
				SimpleCertManagementUtil.getInstance().loadCertificate(this.pemFileName);
			
			this.connOpts.setSocketFactory(sslFactory);
			
			// override current config parameters
			this.port =
				configUtil.getInteger(
					configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
			
			this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;
			
			_Logger.info("TLS enabled.");
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
			
			this.enableEncryption = false;
		}
	}
	
	// Inner classes for message listeners (Option 2)
	
	/**
	 * Inner class to handle ActuatorData response messages.
	 */
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
				
				_Logger.info("Received ActuatorData response: " + actuatorData.getValue());
					
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleActuatorCommandResponse(resource, actuatorData);
				}
			} catch (Exception e) {
				_Logger.log(Level.WARNING, "Failed to convert message payload to ActuatorData.", e);
			}
		}
	}
	
	/**
	 * Inner class to handle SensorData messages.
	 */
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
	
	/**
	 * Inner class to handle SystemPerformanceData messages.
	 */
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
				
				_Logger.info("Received SystemPerformanceData: CPU = " + sysPerfData.getCpuUtilization() + "%, Memory = " + sysPerfData.getMemoryUtilization() + "%");
				
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleSystemPerformanceMessage(resource, sysPerfData);
				}
			} catch (Exception e) {
				_Logger.log(Level.WARNING, "Failed to convert message payload to SystemPerformanceData.", e);
			}
		}	
	}
}
=======
    private MqttClient mqttClient = null;
    private IDataMessageListener dataMsgListener = null;

    public MqttClientConnector()
    {
        super();
        initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
    }

    private void initClientParameters(String configSectionName)
    {
        ConfigUtil configUtil = ConfigUtil.getInstance();

        this.host = configUtil.getProperty(configSectionName, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
        this.port = configUtil.getInteger(configSectionName, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
        this.brokerKeepAlive = configUtil.getInteger(configSectionName, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
        this.enableEncryption = configUtil.getBoolean(configSectionName, ConfigConst.ENABLE_CRYPT_KEY);
        this.pemFileName = configUtil.getProperty(configSectionName, ConfigConst.CERT_FILE_KEY);

        this.useAsyncClient = configUtil.getBoolean(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.USE_ASYNC_CLIENT_KEY);
        this.clientID = configUtil.getProperty(ConfigConst.GATEWAY_DEVICE, ConfigConst.DEVICE_LOCATION_ID_KEY, MqttClient.generateClientId());

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
                    _Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + this.pemFileName, new Exception());
                    return;
                }
            }

            SSLSocketFactory sslFactory = SimpleCertManagementUtil.getInstance().loadCertificate(this.pemFileName);
            this.connOpts.setSocketFactory(sslFactory);

            this.port = configUtil.getInteger(configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
            this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;

            _Logger.info("TLS enabled.");
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
            this.enableEncryption = false;
        }
    }

    public void setDataMessageListener(IDataMessageListener listener)
    {
        if (listener != null) {
            this.dataMsgListener = listener;
            _Logger.info("DataMessageListener set.");
        } else {
            _Logger.warning("Attempted to set null DataMessageListener.");
        }
    }

    public boolean connectClient()
    {
        try {
            this.mqttClient = new MqttClient(this.brokerAddr, this.clientID, this.persistence);
            this.mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    _Logger.warning("MQTT connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    _Logger.info("Message arrived on topic: " + topic);
                    if (dataMsgListener != null) {
                        try {
                            String payload = new String(message.getPayload());
                            dataMsgListener.handleIncomingMessage(ResourceNameEnum.getEnumFromTopic(topic), payload);
                        } catch (Exception e) {
                            _Logger.log(Level.SEVERE, "Failed to forward incoming message to listener.", e);
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    _Logger.fine("Message delivery complete. Token: " + token.toString());
                }
            });

            this.mqttClient.connect(this.connOpts);

            // Subscribe to CDA topics
            this.mqttClient.subscribe(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getValue());
            this.mqttClient.subscribe(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getValue());
            this.mqttClient.subscribe(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.getValue());

            _Logger.info("Connected and subscribed to CDA topics.");
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to connect to MQTT broker.", e);
            return false;
        }
    }

    public boolean disconnectClient()
    {
        try {
            if (this.mqttClient != null && this.mqttClient.isConnected()) {
                this.mqttClient.disconnect();
                _Logger.info("Disconnected from MQTT broker.");
                return true;
            }
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to disconnect from MQTT broker.", e);
        }
        return false;
    }
}
>>>>>>> Stashed changes
