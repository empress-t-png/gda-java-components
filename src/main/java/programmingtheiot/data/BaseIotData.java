package programmingtheiot.data;

import java.time.OffsetDateTime;

/**
 * Abstract base class for all IoT data types.
 */
public abstract class BaseIotData
{
    private String name = "";
    private String locationID = "";
    private int typeID = 0;
    private long timeStamp = System.currentTimeMillis();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocationID() { return locationID; }
    public void setLocationID(String locationID) { this.locationID = locationID; }

    public int getTypeID() { return typeID; }
    public void setTypeID(int typeID) { this.typeID = typeID; }

    public long getTimeStampMillis() { return timeStamp; }

    public String getTimeStamp() {
        return OffsetDateTime.now().toString();
    }

    public abstract void handleUpdateData(BaseIotData data);
}
