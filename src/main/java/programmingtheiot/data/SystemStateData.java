/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import programmingtheiot.common.ConfigConst;

/**
 * Convenience wrapper to store system state data, including location
 * information, action command, state data and a list of the following
 * data items:
 * <p>SystemPerformanceData
 * <p>SensorData
 * 
 */
public class SystemStateData extends BaseIotData implements Serializable
{
	// static
	
	public static final int NO_ACTION = 0;
	public static final int REBOOT_SYSTEM_ACTION = 1;
	public static final int GET_SYSTEM_STATE_ACTION = 2;
	
	public static final String DEFAULT_LOCATION = ConfigConst.NOT_SET;
	
	// private var's
	
    private String location = DEFAULT_LOCATION;
    
    private int actionCmd = NO_ACTION;
    
    private List<SystemPerformanceData> sysPerfDataList = null;
    private List<SensorData> sensorDataList = null;
    
    
	// constructors
	/**
	 * default constructor
	 */
	public SystemStateData()
	{
		super();
		super.setName(ConfigConst.SYS_STATE_DATA);
		this.sysPerfDataList = new ArrayList<SystemPerformanceData>();
		this.sensorDataList = new ArrayList<SensorData>();
	}
	
	
	// public methods
	/**
	 * add new sensor data to the list
	 * @param data the data to be added to the list
	 */
	public boolean addSensorData(SensorData data)
	{
		this.sensorDataList.add(data);
		return true;
	}
	/**
	 * add new SystemPerformanceDatato the list
	 * @param data the data to be added to the list
	 */
	public boolean addSystemPerformanceData(SystemPerformanceData data)
	{
		this.sysPerfDataList.add(data);
		return false;
	}

	/**
	 * The getter of the action command
	 * @return the command
	 */
	public int getActionCommand()
	{
		return this.actionCmd;
	}
	/**
	 * Getter of location
	 * @return location
	 */
	public String getLocation()
	{
		return this.location;
	}
	/**
	 * Getter of the Sensor data list
	 * @return sensorDataList the list
	 */
	public List<SensorData> getSensorDataList()
	{
		return this.sensorDataList;
	}
	/**
	 * Getter of the System performance list
	 * @return sysPerfDataList the list
	 */
	public List<SystemPerformanceData> getSystemPerformanceDataList()
	{
		return this.sysPerfDataList;
	}
	/**
	 * Setter of the action command
	 * @param actionCmd the new action command
	 */
	public void setActionCommand(int actionCmd)
	{
		this.actionCmd = actionCmd;
	}
	/**
	 * Setter of location
	 * @param location new location
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	
	// protected methods
	
	
	/**
	 * Update a statusData with another one
	 * @param dat The used one
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if(!this.hasError()) {
			this.setName(data.getName());
			this.setStateData(data.getStateData());
			this.setStatusCode(data.getStatusCode());
			this.setLocation(((SystemStateData)data).getLocation());
			this.setActionCommand(((SystemStateData)data).getActionCommand());
		}
	}
	
}
