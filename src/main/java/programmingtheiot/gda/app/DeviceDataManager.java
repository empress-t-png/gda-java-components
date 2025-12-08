package programmingtheiot.gda.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.system.DefaultDataMessageListener;

/**
 * DeviceDataManager is responsible for managing IoT data flow
 * within the Gateway Device App. It delegates message handling
 * to an IDataMessageListener implementation.
 */
public class DeviceDataManager
{
    private static final Logger _Logger =
        Logger.getLogger(DeviceDataManager.class.getName());
    
    private IDataMessageListener dataMsgListener;
    private MqttClientConnector mqttClient;
    
    /**
     * Default constructor.
     */
    public DeviceDataManager()
    {
        // Use the default listener implementation
        this.dataMsgListener = new DefaultDataMessageListener();
        this.mqttClient = new MqttClientConnector();
        _Logger.info("DeviceDataManager initialized with DefaultDataMessageListener.");
    }
    
    public void setDataMessageListener(IDataMessageListener listener)
    {
        if (listener != null) {
            this.dataMsgListener = listener;
            _Logger.info("Custom IDataMessageListener set.");
        }
    }
    
    public IDataMessageListener getDataMessageListener()
    {
        return this.dataMsgListener;
    }
    
    /**
     * Start the DeviceDataManager.
     */
    public void startManager()
    {
        _Logger.info("DeviceDataManager started.");
        
        if (this.mqttClient != null) {
            this.mqttClient.connectClient();
            _Logger.info("MQTT client connected.");
        }
    }
    
    /**
     * Stop the DeviceDataManager.
     */
    public void stopManager()
    {
        _Logger.info("DeviceDataManager stopped.");
        
        if (this.mqttClient != null) {
            this.mqttClient.disconnectClient();
            _Logger.info("MQTT client disconnected.");
        }
    }
    
    /**
     * Handle incoming messages from cloud or other sources.
     * 
     * @param resourceName The resource type
     * @param msg The message payload (JSON)
     * @return true if handled successfully, false otherwise
     */
    public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
    {
        if (resourceName != null && msg != null) {
            try {
                if (resourceName == ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE) {
                    _Logger.info("Handling incoming ActuatorData message: " + msg);
                    
                    // Validate by converting to ActuatorData and back to JSON
                    ActuatorData ad = DataUtil.getInstance().jsonToActuatorData(msg);
                    String jsonData = DataUtil.getInstance().actuatorDataToJson(ad);
                    
                    if (this.mqttClient != null) {
                        _Logger.fine("Publishing data to MQTT broker: " + jsonData);
                        return this.mqttClient.publishMessage(
                            resourceName.getResourceName(), jsonData, 0);
                    }
                } else {
                    _Logger.warning("Failed to parse incoming message. Unknown type: " + msg);
                    return false;
                }
            } catch (Exception e) {
                _Logger.log(Level.WARNING, "Failed to process incoming message for resource: " + resourceName, e);
            }
        } else {
            _Logger.warning("Incoming message has no data. Ignoring for resource: " + resourceName);
        }
        
        return false;
    }
}