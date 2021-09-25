/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

/**
 * Shell representation of class for student implementation.
 *
 */
public class SensorData extends BaseIotData implements Serializable
{
	// static
	
	public static final int DEFAULT_SENSOR_TYPE = 0;
	public static final int HUMIDITY_SENSOR_TYPE = 1;
	public static final int PRESSURE_SENSOR_TYPE = 2;
	public static final int TEMP_SENSOR_TYPE = 3;
	

	// private var's
	private float value = 0;
	private int sensorType = 0;
    
	// constructors
	
	/**
	 * The default constructor
	 */
	public SensorData()
	{
		super();
	}
	/**
	 * The default constructor with param
	 * @param sensorType The type of this sensor
	 */
	public SensorData(int sensorType)
	{
		super();
		this.sensorType = sensorType;
	}
	
	
	// public methods
	/**
	 * The getter of  value
	 * @return Got one
	 */
	public float getValue()
	{
		return this.value;
	}
	/**
	 * The getter of  SensorType
	 * @return Got one
	 */
	public int getSensorType() {
		return this.sensorType;
	}
	/**
	 * The Setter of the sensor type
	 * @param sensorType new type
	 */
	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}
	/**
	 * The Setter of the value
	 * @param val new value
	 */
	public void setValue(float val)
	{
		this.value = val;
	}
	
	
	// protected methods
	
	/** 
	 * Update a data with another one
	 * @param BaseIotData data Use another data to update a data
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if(!this.hasError()) {
			this.setName(data.getName());
			this.setStateData(data.getStateData());
			this.setStatusCode(data.getStatusCode());
			this.setValue(((SensorData)data).value);
		}
	}
	
}
