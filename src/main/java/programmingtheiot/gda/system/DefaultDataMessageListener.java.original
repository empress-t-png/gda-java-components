package programmingtheiot.gda.system;

import java.util.logging.Logger;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Default implementation of IDataMessageListener.
 * This is a simple stub that logs received messages.
 */
public class DefaultDataMessageListener implements IDataMessageListener
{
    private static final Logger _Logger =
        Logger.getLogger(DefaultDataMessageListener.class.getName());
    
    @Override
    public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
    {
        _Logger.info("Handling SensorData message: " + data);
        return true;
    }
    
    @Override
    public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
    {
        _Logger.info("Handling SystemPerformanceData message: " + data);
        return true;
    }
    
    @Override
    public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
    {
        _Logger.info("Handling ActuatorData response: " + data);
        return true;
    }
    
    @Override
    public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
    {
        _Logger.info("Handling incoming message from resource: " + resourceName + " | Message: " + msg);
        return true;
    }
}