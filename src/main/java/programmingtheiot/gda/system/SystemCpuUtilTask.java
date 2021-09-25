/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;


/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemCpuUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * Initialize SystemCpuUtilTask with constructor of BaseSystemUtilTask
	 */
	public SystemCpuUtilTask()
	{
		super();
	}
	
	
	// protected methods
	/**
	 * Get cpu usage and return the system load average for the last minute.
	 * 
	 * @return float cpu utility
	 */
	@Override
	protected float getSystemUtil()
	{
		return (float)ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage(); //Return CPU performance: Returns the system load average for the last minute.
	}
	
}
