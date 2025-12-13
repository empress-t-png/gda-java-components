package programmingtheiot.common;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Interface for receiving data messages from cloud or edge.
 */
public interface IDataMessageListener
{
    // Original methods
    void onActuatorDataReceived(ActuatorData data);
    void onSensorDataReceived(SensorData data);
    void onSystemPerformanceDataReceived(SystemPerformanceData data);
    
    // Additional methods needed by the codebase
    boolean handleActuatorCommand(ResourceNameEnum resource, ActuatorData data);
    boolean handleActuatorCommandResponse(ResourceNameEnum resource, ActuatorData data);
    boolean handleSensorMessage(ResourceNameEnum resource, SensorData data);
    boolean handleSystemPerformanceMessage(ResourceNameEnum resource, SystemPerformanceData data);
    boolean onActuatorCommandReceived(ActuatorData data);
    boolean setActuatorDataListener(Object listener, Object handler);
    boolean handleIncomingMessage(ResourceNameEnum resource, String msg);
}
