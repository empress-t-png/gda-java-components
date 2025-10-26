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
import java.io.Serializable;
import programmingtheiot.common.ConfigConst;
/**
 * Shell representation of class for student implementation.
 *
 */
public class SensorData extends BaseIotData implements Serializable
{
	// static
	
	public static final int DEFAULT_STATUS = 0;
	
	// private var's
	
	private float value = ConfigConst.DEFAULT_VAL;
	private int sensorType = ConfigConst.DEFAULT_SENSOR_TYPE;
    
	// constructors
	
	public SensorData()
	{
		super();
		super.setName(ConfigConst.NOT_SET);
	}
	
	public SensorData(int sensorType)
	{
		super();
		super.setName(ConfigConst.NOT_SET);
		this.sensorType = sensorType;
	}
	
	
	// public methods
	
	public int getSensorType()
	{
		return this.sensorType;
	}
	
	public float getValue()
	{
		return this.value;
	}
	
	public void setSensorType(int sensorType)
	{
		this.sensorType = sensorType;
	}
	
	public void setValue(float val)
	{
		this.value = val;
	}
	
	/**
	 * Returns a string representation of this instance. This will invoke the base class
	 * {@link #toString()} method, then append the output from this call.
	 * 
	 * @return String The string representing this instance, returned in CSV 'key=value' format.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		
		sb.append(',');
		sb.append(ConfigConst.VALUE_PROP).append('=').append(this.getValue());
		
		return sb.toString();
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if (data instanceof SensorData) {
			SensorData sData = (SensorData) data;
			this.value = sData.getValue();
			this.sensorType = sData.getSensorType();
		}
	}
	
}