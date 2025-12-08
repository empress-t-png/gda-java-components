package programmingtheiot.common;

import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Interface for cloud client connectors.
 * Defines the contract for connecting, disconnecting, sending data, and subscribing to cloud events.
 */
public interface ICloudClient
{
    /**
     * Connect to the cloud service.
     * @return true if connection succeeds, false otherwise
     */
    public boolean connectClient();

    /**
     * Disconnect from the cloud service.
     * @return true if disconnect succeeds, false otherwise
     */
    public boolean disconnectClient();

    /**
     * Send sensor data upstream to the cloud.
     * @param resource The resource name enum
     * @param data The sensor data
     * @return true if send succeeds, false otherwise
     */
    public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data);

    /**
     * Send system performance data upstream to the cloud.
     * @param resource The resource name enum
     * @param data The system performance data
     * @return true if send succeeds, false otherwise
     */
    public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data);

    /**
     * Subscribe to cloud events for a given resource.
     * @param resource The resource name enum
     * @return true if subscription succeeds, false otherwise
     */
    public boolean subscribeToCloudEvents(ResourceNameEnum resource);

    /**
     * Unsubscribe from cloud events for a given resource.
     * @param resource The resource name enum
     * @return true if unsubscribe succeeds, false otherwise
     */
    public boolean unsubscribeFromCloudEvents(ResourceNameEnum resource);

    /**
     * Set the data message listener for incoming cloud messages.
     * @param listener The data message listener
     * @return true if listener is set, false otherwise
     */
    public boolean setDataMessageListener(IDataMessageListener listener);
}
