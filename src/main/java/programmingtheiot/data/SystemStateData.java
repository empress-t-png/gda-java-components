package programmingtheiot.data;

import programmingtheiot.common.ConfigConst;

/**
 * SystemStateData represents overall system state including sensor and performance lists.
 */
public class SystemStateData extends BaseIotData
{
    private String command = "";
    private String sensorDataList = "";
    private String systemPerfDataList = "";

    public SystemStateData() { super(); }

    public String getCommand() { return this.command; }
    public void setCommand(String command) { this.command = command; }

    public String getSensorDataList() { return this.sensorDataList; }
    public void setSensorDataList(String sensorDataList) { this.sensorDataList = sensorDataList; }

    public String getSystemPerfDataList() { return this.systemPerfDataList; }
    public void setSystemPerfDataList(String systemPerfDataList) { this.systemPerfDataList = systemPerfDataList; }

    @Override
    public void handleUpdateData(BaseIotData data)
    {
        if (data instanceof SystemStateData) {
            SystemStateData ssd = (SystemStateData) data;
            this.command = ssd.getCommand();
            this.sensorDataList = ssd.getSensorDataList();
            this.systemPerfDataList = ssd.getSystemPerfDataList();
            this.setName(ssd.getName());
        }
    }

    @Override
    public String toString()
    {
        return "SystemStateData [name=" + getName() +
               ", command=" + command +
               ", sensorDataList=" + sensorDataList +
               ", systemPerfDataList=" + systemPerfDataList + "]";
    }
}
