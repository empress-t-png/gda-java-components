package programmingtheiot.gda.system;

import java.util.logging.Logger;

import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Default implementation of IDataMessageListener.
 * Simply logs incoming messages but takes no action.
 * Used for testing and as a placeholder listener.
 */
public class DefaultDataMessageListener implements IDataMessageListener {

    // static
    private static final Logger _Logger =
        Logger.getLogger(DefaultDataMessageListener.class.getName());

    // constructors
    
    /**
     * Default constructor.
     */
    public DefaultDataMessageListener() {
        super();
    }

    // public methods

    @Override
    public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data) {
        if (data != null) {
            _Logger.info("Handling actuator command response for resource: " + 
                resourceName.getResourceName() + " - " + data.getName());
        }
        return true;
    }

    @Override
    public boolean handleActuatorCommandRequest(ResourceNameEnum resourceName, ActuatorData data) {
        if (data != null) {
            _Logger.info("Handling actuator command request for resource: " + 
                resourceName.getResourceName() + " - " + data.getName());
        }
        return true;
    }

    @Override
    public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg) {
        if (msg != null) {
            _Logger.info("Handling incoming message for resource: " + 
                resourceName.getResourceName());
        }
        return true;
    }

    @Override
    public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data) {
        if (data != null) {
            _Logger.info("Handling sensor message for resource: " + 
                resourceName.getResourceName() + " - " + data.getName());
        }
        return true;
    }

    @Override
    public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data) {
        if (data != null) {
            _Logger.info("Handling system performance message for resource: " + 
                resourceName.getResourceName());
        }
        return true;
    }

    @Override
    public void setActuatorDataListener(String name, IActuatorDataListener listener) {
        // No action needed for default implementation
        _Logger.info("Actuator data listener set for: " + name);
    }
}