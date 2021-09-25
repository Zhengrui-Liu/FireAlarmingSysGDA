/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public class SystemPerformanceData extends BaseIotData implements Serializable
{
	// static
	
	
	// private var's
	private float cpuUtilization = 0.0f;
	private float memoryUtilization = 0.0f;
	private String device = "GDA";
    
	// constructors
	/**
	 * Default Constructor
	 */
	public SystemPerformanceData()
	{
		super();
		
		super.setName(ConfigConst.SYS_PERF_DATA);
	}
	
	
	// public methods
	/**
	 * Getter of the device flat of the system performance data
	 * @return a device string to determain if it is a data from cda
	 */
	public String getDevice() {
		return this.device;
	}
	/**
	 * Setter of the device flat of the system performance data
	 * @param device  a device string to determain if it is a data from cda
	 */
	public void setDevice(String device) {
		this.device = device;
	}
	
	/**
	 * Getter of the CPU utilization
	 * @return the util of cpu
	 */
	public float getCpuUtilization()
	{
		return this.cpuUtilization;
	}
	/**
	 * Getter of the Memory utilization
	 * @return the util of memory
	 */
	public float getMemoryUtilization()
	{
		return this.memoryUtilization;
	}
	/**
	 * Setter of the CPU utilization
	 * @param val The new value
	 */
	public void setCpuUtilization(float val)
	{
		this.cpuUtilization = val;
	}

	/**
	 * Setter of the Memory utilization
	 * @param val The new value
	 */
	public void setMemoryUtilization(float val)
	{
		this.memoryUtilization = val;
	}
	
	
	// protected methods
	
	
	/**
	 * Use a data to update another one
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if(!this.hasError()) {
			this.setName(data.getName());
			this.setStateData(data.getStateData());
			this.setStatusCode(data.getStatusCode());
			this.setCpuUtilization(((SystemPerformanceData)data).getCpuUtilization());
			this.setMemoryUtilization(((SystemPerformanceData)data).getMemoryUtilization());
			
		}
	}
	
}
