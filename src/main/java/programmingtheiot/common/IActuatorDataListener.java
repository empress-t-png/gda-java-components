package programmingtheiot.common;

import programmingtheiot.data.ActuatorData;

/**
 * Interface for receiving actuator data events.
 */
public interface IActuatorDataListener
{
    void onActuatorCommandReceived(ActuatorData data);
}
