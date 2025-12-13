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
    
    // Original interface methods
    @Override
    public void onActuatorDataReceived(ActuatorData data)
    {
        _Logger.info("onActuatorDataReceived: " + data);
    }
    
    @Override
    public void onSensorDataReceived(SensorData data)
    {
        _Logger.info("onSensorDataReceived: " + data);
    }
    
    @Override
    public void onSystemPerformanceDataReceived(SystemPerformanceData data)
    {
        _Logger.info("onSystemPerformanceDataReceived: " + data);
    }
    
    // Additional interface methods
    @Override
    public boolean handleActuatorCommand(ResourceNameEnum resourceName, ActuatorData data)
    {
        _Logger.info("handleActuatorCommand: " + resourceName + " - " + data);
        return true;
    }
    
    @Override
    public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
    {
        _Logger.info("handleActuatorCommandResponse: " + resourceName + " - " + data);
        return true;
    }
    
    @Override
    public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
    {
        _Logger.info("handleSensorMessage: " + resourceName + " - " + data);
        return true;
    }
    
    @Override
    public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
    {
        _Logger.info("handleSystemPerformanceMessage: " + resourceName + " - " + data);
        return true;
    }
    
    @Override
    public boolean onActuatorCommandReceived(ActuatorData data)
    {
        _Logger.info("onActuatorCommandReceived: " + data);
        return true;
    }
    
    @Override
    public boolean setActuatorDataListener(Object listener, Object handler)
    {
        _Logger.info("setActuatorDataListener: " + listener + " - " + handler);
        return true;
    }
    
    @Override
    public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
    {
        _Logger.info("handleIncomingMessage: " + resourceName + " - " + msg);
        return true;
    }
}
