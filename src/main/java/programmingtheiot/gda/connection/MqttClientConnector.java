package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.gda.connection.IConnectionListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

public class MqttClientConnector implements MqttCallbackExtended
{
    private static final Logger _Logger = Logger.getLogger(MqttClientConnector.class.getName());

    private MqttAsyncClient mqttClient = null;
    private MqttConnectOptions connOpts = null;
    private MemoryPersistence persistence = null;
    private IDataMessageListener dataMsgListener = null;
    private IConnectionListener connListener = null;
    private boolean useCloudGatewayConfig = false;

    public MqttClientConnector()
    {
        this(false);
    }

    public MqttClientConnector(boolean useCloudGatewayConfig)
    {
        this.useCloudGatewayConfig = useCloudGatewayConfig;

        try {
            ConfigUtil configUtil = ConfigUtil.getInstance();
            String brokerUrl = configUtil.getProperty(
                useCloudGatewayConfig ? ConfigConst.CLOUD_GATEWAY_SERVICE : ConfigConst.GATEWAY_DEVICE,
                ConfigConst.HOST_KEY);

            this.persistence = new MemoryPersistence();
            this.connOpts = new MqttConnectOptions();
            this.connOpts.setAutomaticReconnect(true);
            this.connOpts.setCleanSession(true);

            this.mqttClient = new MqttAsyncClient(brokerUrl, MqttAsyncClient.generateClientId(), this.persistence);
            this.mqttClient.setCallback(this);
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to initialize MQTT client.", e);
        }
    }

    public boolean connectClient()
    {
        try {
            if (!this.mqttClient.isConnected()) {
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
                this.mqttClient.disconnect().waitForCompletion();
                return true;
            }
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Disconnect failed.", e);
        }
        return false;
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

    public boolean subscribeToTopic(String topic, int qos)
    {
        try {
            this.mqttClient.subscribe(topic, qos);
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Subscribe failed to topic: " + topic, e);
        }
        return false;
    }

    public boolean subscribeToTopic(String topic, int qos, IMqttMessageListener listener)
    {
        try {
            this.mqttClient.subscribe(topic, qos, listener);
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Subscribe with listener failed to topic: " + topic, e);
        }
        return false;
    }

    public boolean unsubscribeFromTopic(String topic)
    {
        try {
            this.mqttClient.unsubscribe(topic);
            return true;
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Unsubscribe failed for topic: " + topic, e);
        }
        return false;
    }

    public void setDataMessageListener(IDataMessageListener listener)
    {
        this.dataMsgListener = listener;
    }

    public boolean setConnectionListener(IConnectionListener listener)
    {
        if (listener != null) {
            _Logger.info("Setting connection listener.");
            this.connListener = listener;
            return true;
        }
        _Logger.warning("No connection listener specified. Ignoring.");
        return false;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI)
    {
        _Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);

        if (this.connListener != null) {
            this.connListener.onConnect(reconnect);
        }
    }

    @Override
    public void connectionLost(Throwable cause)
    {
        _Logger.warning("Connection lost: " + cause.getMessage());

        if (this.connListener != null) {
            this.connListener.onDisconnect();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        _Logger.info("Delivery complete for token: " + token.getMessageId());
    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception
    {
        String payload = new String(msg.getPayload());
        _Logger.info("Message arrived on topic: " + topic + " | Payload: " + payload);

        if (this.dataMsgListener != null) {
            ResourceNameEnum resource = ResourceNameEnum.getEnumFromValue(topic);
            this.dataMsgListener.handleIncomingMessage(resource != null ? resource : ResourceNameEnum.UNKNOWN, payload);
        }
    }
}