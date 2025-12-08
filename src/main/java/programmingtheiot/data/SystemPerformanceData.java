package programmingtheiot.data;

import programmingtheiot.common.ConfigConst;

/**
 * SystemPerformanceData represents CPU, memory, and disk utilization.
 */
public class SystemPerformanceData extends BaseIotData
{
    private float cpuUtilization = ConfigConst.DEFAULT_VAL;
    private float memoryUtilization = ConfigConst.DEFAULT_VAL;
    private float diskUtilization = ConfigConst.DEFAULT_VAL;

    public SystemPerformanceData() { super(); }

    public float getCpuUtilization() { return this.cpuUtilization; }
    public void setCpuUtilization(float cpuUtilization) { this.cpuUtilization = cpuUtilization; }

    public float getMemoryUtilization() { return this.memoryUtilization; }
    public void setMemoryUtilization(float memoryUtilization) { this.memoryUtilization = memoryUtilization; }

    public float getDiskUtilization() { return this.diskUtilization; }
    public void setDiskUtilization(float diskUtilization) { this.diskUtilization = diskUtilization; }

    @Override
    public void handleUpdateData(BaseIotData data)
    {
        if (data instanceof SystemPerformanceData) {
            SystemPerformanceData spd = (SystemPerformanceData) data;
            this.cpuUtilization = spd.getCpuUtilization();
            this.memoryUtilization = spd.getMemoryUtilization();
            this.diskUtilization = spd.getDiskUtilization();
            this.setName(spd.getName());
        }
    }

    @Override
    public String toString()
    {
        return "SystemPerformanceData [name=" + getName() +
               ", CPU=" + cpuUtilization + ", Memory=" + memoryUtilization +
               ", Disk=" + diskUtilization + "]";
    }
}
