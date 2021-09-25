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
public class ActuatorData extends BaseIotData implements Serializable
{
	// static
	
	public static final int DEFAULT_COMMAND = 0;
	public static final int COMMAND_OFF = DEFAULT_COMMAND;
	public static final int COMMAND_ON = 1;
	
	public static final int HVAC_ACTUATOR_TYPE = 1;
	public static final int	HUMIDIFIER_ACTUATOR_TYPE = 2;
	public static final int	LED_DISPLAY_ACTUATOR_TYPE = 100;
	// private var's
	private int command;
	private float value;
	private boolean asResponse;
	private int actuatorType;
    
    
	// constructors
	
	/**
	 * Default constructor.
	 * 
	 */
	public ActuatorData()
	{
		super();
	}
	
	
	/**
	 * Getter of ActuatorType
	 * @return Got one
	 */
	public int getActuatorType()
	{
		return this.actuatorType;
	}
	/**
	 * Setter of actuatorType
	 * @param val
	 */
	public void setActuatorType(int val)
	{
		this.actuatorType = val;
	}
	/**
	 * Getter of command code
	 * @return Got one
	 */
	public int getCommand()
	{
		return this.command;
	}
	
	/**
	 * Getter of Value
	 * @return value
	 */
	public float getValue()
	{
		return this.value;
	}
	
	/**
	 * Setter of command code
	 * @param new command code
	 */
	public void setCommand(int command)
	{
		this.command = command;
	}
	/**
	 * Setter of the value
	 * @param val  new value
	 */
	public void setValue(float val)
	{
		this.value = val;
	}
	
	/**
	 * Getter of response flag
	 * @return the flag of response
	 */
	public boolean getAsResponse(){
		return this.asResponse;
	}
	
	/**
	 * Setter of response flag
	 * @param asResponse
	 */
	public void setAsResponse(boolean asResponse){
		this.asResponse = asResponse;
	}
	
	// protected methods
	
	/**
	 * Update a data with another one
	 * @param BaseIotData data
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if(!this.hasError()) {
			this.setName(data.getName());
			this.setStateData(data.getStateData());
			this.setStatusCode(data.getStatusCode());
			this.setCommand(((ActuatorData)data).getCommand());
			this.setValue(((ActuatorData)data).getValue());
			this.setAsResponse(((ActuatorData)data).getAsResponse());
		}
	}
	
}
