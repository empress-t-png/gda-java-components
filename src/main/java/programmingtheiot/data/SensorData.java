package programmingtheiot.data;

/**
 * Represents sensor data information.
 */
public class SensorData extends BaseIotData
{
    public static final float DEFAULT_SENSOR_VALUE = 0.0f;
    
    private float value = DEFAULT_SENSOR_VALUE;
    
    public SensorData()
    {
        super();
    }
    
    public float getValue()
    {
        return this.value;
    }
    
    public void setValue(float value)
    {
        this.value = value;
    }
    
    @Override
    public void handleUpdateData(BaseIotData data)
    {
        if (data instanceof SensorData) {
            SensorData sensorData = (SensorData) data;
            this.setValue(sensorData.getValue());
        }
    }
}
