package programmingtheiot.common;

/**
 * Enum for resource names used in messaging across CDA, GDA, and cloud services.
 */
public enum ResourceNameEnum
{
    // CDA (Constrained Device Application) Resources
    CDA_ACTUATOR_CMD_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "ActuatorCmd"),
    CDA_ACTUATOR_RESPONSE_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "ActuatorResponse"),
    CDA_MGMT_STATUS_CMD_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "MgmtStatusCmd"),
    CDA_MGMT_STATUS_MSG_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "MgmtStatusMsg"),
    CDA_SENSOR_MSG_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "SensorMsg"),
    CDA_SYSTEM_PERF_MSG_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "SystemPerfMsg"),
    CDA_UPDATE_NOTIFICATIONS_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, "UpdateMsg"),
    
    // GDA (Gateway Device Application) Resources
    GDA_MGMT_STATUS_CMD_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, "MgmtStatusCmd"),
    GDA_MGMT_STATUS_MSG_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, "MgmtStatusMsg"),
    GDA_SYSTEM_PERF_MSG_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, "SystemPerfMsg"),
    GDA_UPDATE_NOTIFICATIONS_RESOURCE(ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, "UpdateMsg"),
    
    // Unknown/Default
    UNKNOWN("Unknown", "Unknown", "Unknown");

    // Instance variables
    private final String productName;
    private final String deviceName;
    private final String resourceType;
    private final String resourceName;

    /**
     * Constructor for ResourceNameEnum.
     * 
     * @param productName The product name
     * @param deviceName The device name
     * @param resourceType The resource type
     */
    private ResourceNameEnum(String productName, String deviceName, String resourceType)
    {
        this.productName = productName;
        this.deviceName = deviceName;
        this.resourceType = resourceType;
        
        // Create the full resource name in format: PRODUCT/DEVICE/RESOURCE
        this.resourceName = productName + "/" + deviceName + "/" + resourceType;
    }

    /**
     * Returns the full resource name.
     * 
     * @return String The full resource name
     */
    public String getResourceName()
    {
        return this.resourceName;
    }

    /**
     * Returns the product name.
     * 
     * @return String The product name
     */
    public String getProductName()
    {
        return this.productName;
    }

    /**
     * Returns the device name.
     * 
     * @return String The device name
     */
    public String getDeviceName()
    {
        return this.deviceName;
    }

    /**
     * Returns the resource type.
     * 
     * @return String The resource type
     */
    public String getResourceType()
    {
        return this.resourceType;
    }

    /**
     * Gets the enum constant from a string value.
     * 
     * @param value The string value to match
     * @return ResourceNameEnum The matching enum, or UNKNOWN if no match
     */
    public static ResourceNameEnum getEnumFromValue(String value)
    {
        if (value != null && value.trim().length() > 0) {
            for (ResourceNameEnum e : values()) {
                if (e.getResourceName().equalsIgnoreCase(value)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }
}