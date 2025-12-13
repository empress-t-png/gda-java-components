package programmingtheiot.common;

import java.util.Properties;

/**
 * Utility class for configuration management.
 */
public final class ConfigUtil
{
    private static ConfigUtil instance = null;
    private Properties props = new Properties();

    private ConfigUtil()
    {
        // Load defaults or properties file here if needed
    }

    public static synchronized ConfigUtil getInstance()
    {
        if (instance == null) {
            instance = new ConfigUtil();
        }
        return instance;
    }

    public boolean getBoolean(String section, String key, boolean defaultValue)
    {
        String val = props.getProperty(section + "." + key);
        return (val != null) ? Boolean.parseBoolean(val) : defaultValue;
    }

    public int getInteger(String section, String key, int defaultValue)
    {
        String val = props.getProperty(section + "." + key);
        try {
            return (val != null) ? Integer.parseInt(val) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getFloat(String section, String key, float defaultValue)
    {
        String val = props.getProperty(section + "." + key);
        try {
            return (val != null) ? Float.parseFloat(val) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String getProperty(String section, String key, String defaultValue)
    {
        String val = props.getProperty(section + "." + key);
        return (val != null) ? val : defaultValue;
    }
}
