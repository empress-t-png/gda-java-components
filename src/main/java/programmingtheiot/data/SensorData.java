package programmingtheiot.data;

import programmingtheiot.common.ConfigConst;

/**
 * SensorData represents data from a sensor.
 */
public class SensorData extends BaseIotData
{
    private float value = ConfigConst.DEFAULT_VAL;
    private int typeID = ConfigConst.DEFAULT_SENSOR_TYPE;
    private int locationID = ConfigConst.NOT_SET;

    public SensorData() { super(); }

    public float getValue() { return this.value; }
    public void setValue(float value) { this.value = value; }

    public int getTypeID() { return this.typeID; }
    public void setTypeID(int typeID) { this.typeID = typeID; }

    public int getLocationID() { return this.locationID; }
    public void setLocationID(int locationID) { this.locationID = locationID; }

    @Override
    public void handleUpdateData(BaseIotData data)
    {
        if (data instanceof SensorData) {
            SensorData sd = (SensorData) data;
            this.value = sd.getValue();
            this.typeID = sd.getTypeID();
            this.locationID = sd.getLocationID();
            this.setName(sd.getName());
        }
    }

    @Override
    public String toString()
    {
        return "SensorData [name=" + getName() + ", value=" + value +
               ", typeID=" + typeID + ", locationID=" + locationID + "]";
    }
}
