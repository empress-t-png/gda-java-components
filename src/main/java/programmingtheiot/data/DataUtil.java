/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */
package programmingtheiot.data;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility class for converting data objects to/from JSON.
 *
 */
public class DataUtil
{
	// static
	
	private static final DataUtil _Instance = new DataUtil();
	
	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return DataUtil
	 */
	public static final DataUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	
	private Gson gson;
	
	// constructors
	
	/**
	 * Default (private).
	 * 
	 */
	private DataUtil()
	{
		super();
		
		// Create Gson instance for JSON conversion
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	
	// public methods
	
	/**
	 * Converts ActuatorData to JSON string.
	 * 
	 * @param actuatorData The ActuatorData instance to convert.
	 * @return JSON string representation, or null if actuatorData is null.
	 */
	public String actuatorDataToJson(ActuatorData actuatorData)
	{
		if (actuatorData != null) {
			return this.gson.toJson(actuatorData);
		}
		return null;
	}
	
	/**
	 * Converts SensorData to JSON string.
	 * 
	 * @param sensorData The SensorData instance to convert.
	 * @return JSON string representation, or null if sensorData is null.
	 */
	public String sensorDataToJson(SensorData sensorData)
	{
		if (sensorData != null) {
			return this.gson.toJson(sensorData);
		}
		return null;
	}
	
	/**
	 * Converts SystemPerformanceData to JSON string.
	 * 
	 * @param sysPerfData The SystemPerformanceData instance to convert.
	 * @return JSON string representation, or null if sysPerfData is null.
	 */
	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		if (sysPerfData != null) {
			return this.gson.toJson(sysPerfData);
		}
		return null;
	}
	
	/**
	 * Converts SystemStateData to JSON string.
	 * 
	 * @param sysStateData The SystemStateData instance to convert.
	 * @return JSON string representation, or null if sysStateData is null.
	 */
	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		if (sysStateData != null) {
			return this.gson.toJson(sysStateData);
		}
		return null;
	}
	
	/**
	 * Converts JSON string to ActuatorData object.
	 * 
	 * @param jsonData The JSON string to convert.
	 * @return ActuatorData instance, or null if jsonData is null or empty.
	 */
	public ActuatorData jsonToActuatorData(String jsonData)
	{
		if (jsonData != null && jsonData.trim().length() > 0) {
			return this.gson.fromJson(jsonData, ActuatorData.class);
		}
		return null;
	}
	
	/**
	 * Converts JSON string to SensorData object.
	 * 
	 * @param jsonData The JSON string to convert.
	 * @return SensorData instance, or null if jsonData is null or empty.
	 */
	public SensorData jsonToSensorData(String jsonData)
	{
		if (jsonData != null && jsonData.trim().length() > 0) {
			return this.gson.fromJson(jsonData, SensorData.class);
		}
		return null;
	}
	
	/**
	 * Converts JSON string to SystemPerformanceData object.
	 * 
	 * @param jsonData The JSON string to convert.
	 * @return SystemPerformanceData instance, or null if jsonData is null or empty.
	 */
	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		if (jsonData != null && jsonData.trim().length() > 0) {
			return this.gson.fromJson(jsonData, SystemPerformanceData.class);
		}
		return null;
	}
	
	/**
	 * Converts JSON string to SystemStateData object.
	 * 
	 * @param jsonData The JSON string to convert.
	 * @return SystemStateData instance, or null if jsonData is null or empty.
	 */
	public SystemStateData jsonToSystemStateData(String jsonData)
	{
		if (jsonData != null && jsonData.trim().length() > 0) {
			return this.gson.fromJson(jsonData, SystemStateData.class);
		}
		return null;
	}
	
}