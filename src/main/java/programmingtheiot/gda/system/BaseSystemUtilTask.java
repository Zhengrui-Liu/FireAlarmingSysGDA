/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.logging.Logger;

import programmingtheiot.data.SensorData;

/**
 *
 */
public abstract class BaseSystemUtilTask
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(BaseSystemUtilTask.class.getName());
	
	
	// private
	private SensorData latestSensorData = null;
	
	// constructors
	
	public BaseSystemUtilTask()
	{
		super();
		
	}
	
	
	// public methods
	/**
	 * generate the latest sensor data
	 * @return sensorData the generated sensor data
	 */
	public SensorData generateTelemetry()
	{
		SensorData sensorData = new SensorData();
		this.latestSensorData = sensorData;
		this.latestSensorData.setValue(this.getSystemUtil());
		return this.latestSensorData;
	}
	
	/**
	 * get, log out and return the system usage info
	 * 
	 * @return float the value of the system utility
	 */
	public float getTelemetryValue()
	{
		if(this.latestSensorData == null) {
			return this.generateTelemetry().getValue();
		
		}
		float val = getSystemUtil();  // return a new value: val.
		_Logger.info("val = " + val);
		return val;
	}
	
	
	// protected methods
	
	/**
	 * Template method definition. Sub-class will implement this to retrieve
	 * the system utilization measure.
	 * 
	 * @return float
	 */
	protected abstract float getSystemUtil();
	
}
