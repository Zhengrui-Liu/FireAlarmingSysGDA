/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemMemUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * Initialize SystemMemUtilTask with constructor of BaseSystemUtilTask
	 */
	public SystemMemUtilTask()
	{
		super();
	}
	
	
	// protected methods
	/**
	 * Get Memory usage and return the used heap memory in float.
	 * 
	 * @return float memory utility
	 */
	@Override
	protected float getSystemUtil()
	{
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed(); // return memory performance
	}
	
}
