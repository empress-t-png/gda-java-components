package programmingtheiot.common;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

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
        loadConfig();
    }

    public static synchronized ConfigUtil getInstance()
    {
        if (instance == null) {
            instance = new ConfigUtil();
        }
        return instance;
    }

    private void loadConfig()
    {
        try {
            // Try to load from classpath first (src/main/resources/)
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("GatewayDeviceApp.properties");
            if (inputStream != null) {
                props.load(inputStream);
                inputStream.close();
                System.out.println("ConfigUtil: Loaded properties from classpath: GatewayDeviceApp.properties");
            } else {
                // Try to load from file system (project root)
                File configFile = new File("GatewayDeviceApp.properties");
                if (configFile.exists()) {
                    FileInputStream fis = new FileInputStream(configFile);
                    props.load(fis);
                    fis.close();
                    System.out.println("ConfigUtil: Loaded properties from file: GatewayDeviceApp.properties");
                } else {
                    // Try config/ folder
                    configFile = new File("config/GatewayDeviceApp.properties");
                    if (configFile.exists()) {
                        FileInputStream fis = new FileInputStream(configFile);
                        props.load(fis);
                        fis.close();
                        System.out.println("ConfigUtil: Loaded properties from config/GatewayDeviceApp.properties");
                    } else {
                        System.out.println("ConfigUtil: Config file not found. Using defaults.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ConfigUtil: Failed to load config: " + e.getMessage());
        }
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
