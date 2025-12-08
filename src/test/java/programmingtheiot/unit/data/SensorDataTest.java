package programmingtheiot.common;

/**
 * Configuration and other constants for use across the application.
 */
public final class ConfigConst
{
    // Static initializer
    static {
        // Nothing to do here for now
    }
    
    // Private constructor
    private ConfigConst() {
        super();
    }
    
    
    // ====================
    // GENERAL CONFIGURATION CONSTANTS
    // ====================
    
    public static final String PRODUCT_NAME = "PIOT";
    public static final String CLOUD = "Cloud";
    public static final String GATEWAY_DEVICE = "GatewayDevice";
    public static final String CONSTRAINED_DEVICE = "ConstrainedDevice";
    public static final String UBIDOTS_CLOUD_SERVICE = "Ubidots";
    public static final String AWS_CLOUD_SERVICE = "AWS";
    public static final String AZURE_CLOUD_SERVICE = "Azure";
    
    public static final String DEVICE_LOCATION_ID_KEY = "deviceLocationID";
    public static final String HAS_DISPLAY_KEY = "hasDisplay";
    public static final String HAS_EMULATOR_KEY = "hasEmulator";
    
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8080;
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    
    // ====================
    // RESOURCE AND TOPIC NAMES
    // ====================
    
    public static final String CDA_SENSOR_DATA_MSG_RESOURCE = "CDA_SENSOR_MSG";
    public static final String CDA_ACTUATOR_CMD_MSG_RESOURCE = "CDA_ACTUATOR_CMD_MSG";
    public static final String CDA_ACTUATOR_RESPONSE_MSG_RESOURCE = "CDA_ACTUATOR_RESPONSE_MSG";
    public static final String CDA_MGMT_STATUS_MSG_RESOURCE = "CDA_MGMT_STATUS_MSG";
    public static final String CDA_SYSTEM_PERF_MSG_RESOURCE = "CDA_SYSTEM_PERF_MSG";
    
    // ====================
    // CONFIGURATION FILE KEYS
    // ====================
    
    public static final String CONFIG_FILE_KEY = "configFile";
    public static final String CRED_FILE_KEY = "credFile";
    public static final String DEFAULT_CONFIG_FILE_NAME = "PiotConfig.props";
    public static final String DEFAULT_CRED_FILE_NAME = "PiotCred.props";
    
    // ====================
    // TEST DATA PATH KEYS
    // ====================
    
    public static final String TEST_GDA_DATA_PATH_KEY = "testGdaDataPath";
    public static final String TEST_CDA_DATA_PATH_KEY = "testCdaDataPath";
    public static final String TEST_EMPTY_APP_KEY = "testEmptyApp";
    
    // ====================
    // MQTT CLIENT CONFIGURATION
    // ====================
    
    public static final String MQTT_GATEWAY_SERVICE = "MqttGatewayService";
    public static final String MQTT_GATEWAY_HOST_KEY = "host";
    public static final String MQTT_GATEWAY_PORT_KEY = "port";
    public static final String MQTT_GATEWAY_SECURE_PORT_KEY = "securePort";
    
    public static final String ENABLE_AUTH_KEY = "enableAuth";
    public static final String ENABLE_CRYPT_KEY = "enableCrypt";
    public static final String ENABLE_EMULATOR_KEY = "enableEmulator";
    public static final String ENABLE_LOGGING_KEY = "enableLogging";
    public static final String ENABLE_MQTT_CLIENT_KEY = "enableMqttClient";
    public static final String ENABLE_COAP_CLIENT_KEY = "enableCoapClient";
    public static final String ENABLE_COAP_SERVER_KEY = "enableCoapServer";
    public static final String ENABLE_CLOUD_CLIENT_KEY = "enableCloudClient";
    public static final String ENABLE_SMTP_CLIENT_KEY = "enableSmtpClient";
    public static final String ENABLE_SYSTEM_PERF_KEY = "enableSystemPerf";
    public static final String ENABLE_SENSE_HAT_KEY = "enableSenseHat";
    
    public static final String POLL_CYCLES_KEY = "pollCycleSecs";
    public static final String KEEP_ALIVE_KEY = "keepAlive";
    public static final String DEFAULT_QOS_KEY = "defaultQos";
    
    public static final String USE_CLOUD_CONFIG_KEY = "useCloudConfig";
    
    public static final String USER_NAME_TOKEN_KEY = "userToken";
    public static final String USER_AUTH_TOKEN_KEY = "authToken";
    public static final String API_TOKEN_KEY = "apiToken";
    
    // ====================
    // COAP CLIENT / SERVER CONFIGURATION
    // ====================
    
    public static final String COAP_GATEWAY_SERVICE = "CoapGatewayService";
    public static final String COAP_DEVICE_SERVICE = "CoapDeviceService";
    public static final String COAP_GATEWAY_HOST_KEY = "gatewayHost";
    public static final String COAP_GATEWAY_PORT_KEY = "gatewayPort";
    public static final String COAP_GATEWAY_SECURE_PORT_KEY = "gatewaySecurePort";
    public static final String COAP_DEVICE_HOST_KEY = "deviceHost";
    public static final String COAP_DEVICE_PORT_KEY = "devicePort";
    public static final String COAP_DEVICE_SECURE_PORT_KEY = "deviceSecurePort";
    
    public static final String DEFAULT_COAP_PROTOCOL = "coap";
    public static final String DEFAULT_COAP_SECURE_PROTOCOL = "coaps";
    public static final int DEFAULT_COAP_PORT = 5683;
    public static final int DEFAULT_COAP_SECURE_PORT = 5684;
    
    // ====================
    // CLOUD CLIENT CONFIGURATION
    // ====================
    
    public static final String CLOUD_GATEWAY_SERVICE = "CloudGatewayService";
    public static final String CLOUD_API_KEY = "apiKey";
    public static final String CLOUD_DEVICE_KEY = "deviceKey";
    public static final String CLOUD_TOPIC_PREFIX_KEY = "topicPrefix";
    
    public static final String UBIDOTS_CLOUD_SECTION = "UbidotsCloudService";
    public static final String AWS_CLOUD_SECTION = "AwsIotCloudService";
    public static final String AZURE_CLOUD_SECTION = "AzureIotCloudService";
    
    // ====================
    // SMTP CLIENT CONFIGURATION
    // ====================
    
    public static final String SMTP_CLOUD_SERVICE = "SmtpCloudService";
    public static final String SMTP_GATEWAY_SERVICE = "SmtpGatewayService";
    public static final String SMTP_CLOUD_HOST_KEY = "host";
    public static final String SMTP_CLOUD_PORT_KEY = "port";
    public static final String SMTP_CLOUD_USER_KEY = "fromAddr";
    public static final String SMTP_CLOUD_AUTH_KEY = "authToken";
    public static final String SMTP_CLOUD_TO_KEY = "toAddr";
    
    // ====================
    // DATA PERSISTENCE CONFIGURATION
    // ====================
    
    public static final String PERSISTENCE_SERVICE = "PersistenceService";
    
    // ====================
    // JSON PROPERTY NAMES
    // ====================
    
    public static final String TIMESTAMP_PROP = "timestamp";
    public static final String VALUE_PROP = "value";
    
    // ====================
    // DATA TYPE IDENTIFIERS
    // ====================
    
    public static final String SYS_PERF_DATA = "SystemPerformanceData";
    public static final String SYS_STATE_DATA = "SystemStateData";
    
    // ====================
    // COMMAND AND CONTROL CONSTANTS
    // ====================
    
    public static final int DEFAULT_COMMAND = 0;
    public static final int NOT_SET = -1;
    public static final String DEFAULT_NAME = "Not Set";
    public static final int ON_COMMAND = 1;
    public static final int OFF_COMMAND = 0;
    
    public static final int DEFAULT_STATUS = 0;
    
    public static final int DEFAULT_TIMEOUT = 5;
    public static final int DEFAULT_TTL = 300;
    public static final int DEFAULT_QOS = 0;
    public static final int DEFAULT_POLL_CYCLES = 60;
    public static final int DEFAULT_KEEP_ALIVE = 60;
    
    // ====================
    // ACTUATOR TYPES AND NAMES
    // ====================
    
    public static final int DEFAULT_TYPE_ID = 0;
    public static final int DEFAULT_TYPE_CATEGORY_ID = 0;
    public static final int DEFAULT_ACTUATOR_TYPE = DEFAULT_TYPE_ID;
    
    public static final String HVAC_ACTUATOR_NAME = "HvacActuator";
    public static final int HVAC_ACTUATOR_TYPE = 1;
    
    public static final String HUMIDIFIER_ACTUATOR_NAME = "HumidifierActuator";
    public static final int HUMIDIFIER_ACTUATOR_TYPE = 1001;
    
    public static final String LED_DISPLAY_ACTUATOR_NAME = "LedDisplayActuator";
    public static final int LED_DISPLAY_ACTUATOR_TYPE = 1002;
    
    public static final String LED_ACTUATOR_NAME = "LedActuator";
    public static final int LED_ACTUATOR_TYPE = 2001;
    
    // ====================
    // SENSOR TYPES AND NAMES
    // ====================
    
    public static final int DEFAULT_SENSOR_TYPE = DEFAULT_TYPE_ID;
    
    public static final String HUMIDITY_SENSOR_NAME = "HumiditySensor";
    public static final int HUMIDITY_SENSOR_TYPE = 1010;
    public static final String HUMIDITY_SENSOR_TYPE_NAME = "Humidity";
    
    public static final String PRESSURE_SENSOR_NAME = "PressureSensor";
    public static final int PRESSURE_SENSOR_TYPE = 1012;
    public static final String PRESSURE_SENSOR_TYPE_NAME = "Pressure";
    
    public static final String TEMP_SENSOR_NAME = "TempSensor";
    public static final int TEMP_SENSOR_TYPE = 1013;
    public static final String TEMP_SENSOR_TYPE_NAME = "Temp";
    
    // ====================
    // SYSTEM PERFORMANCE TYPES
    // ====================
    
    public static final String SYSTEM_PERF_NAME = "SystemPerformance";
    public static final int SYSTEM_PERF_TYPE = 9000;
    
    public static final String CPU_UTIL_NAME = "CpuUtil";
    public static final int CPU_UTIL_TYPE = 9001;
    
    public static final String DISK_UTIL_NAME = "DiskUtil";
    public static final int DISK_UTIL_TYPE = 9002;
    
    public static final String MEM_UTIL_NAME = "MemUtil";
    public static final int MEM_UTIL_TYPE = 9003;
    
    // ====================
    // DEFAULT VALUES
    // ====================
    
    public static final float DEFAULT_VAL = 0.0f;
    public static final float DEFAULT_LAT = 0.0f;
    public static final float DEFAULT_LON = 0.0f;
    public static final float DEFAULT_ELEVATION = 0.0f;
    
    // ====================
    // THRESHOLD VALUES
    // ====================
    
    public static final float NOMINAL_TEMP = 20.0f;
    public static final float NOMINAL_HUMIDITY = 30.0f;
    public static final float NOMINAL_PRESSURE = 1013.0f;
    
    // Not defined as a constant
}