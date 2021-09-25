/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public abstract class BaseIotData implements Serializable
{
	// static
	
	public static final float DEFAULT_VAL = 0.0f;
	public static final int DEFAULT_STATUS = 0;
	
	// private var's

    private String  name       = ConfigConst.NOT_SET;
	private String  timeStamp  = null;
    private boolean hasError   = false;
    private int     statusCode = 0;
    
    private long    timeStampMillis = 0L;
    private String stateData = "default data";

	// constructors
	
	/**
	 * Default constructor of the data type.
	 * 
	 */
	protected BaseIotData()
	{
		super();
		Date date = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
		this.timeStamp = dateFormat.format(date);
	}
	
	
	// public methods
	/**
	 * The getter for the data name
	 * @return the name of the data
	 */
	public String getName()
	{
		return this.name;
	}
	/**
	 * The getter of the statedata
	 * @return the statusdata field in the data
	 */
	public String getStateData()
	{
		return this.stateData;
	}
	/**
	 * The Getter of the Ststus Code
	 * @return Status code
	 */
	public int getStatusCode()
	{
		return this.statusCode;
	}
	/**
	 * The getter of the timestamp
	 * @return the current time
	 */
	public String getTimeStamp()
	{
		return this.timeStamp;
	}
	/**
	 * The getter of the timestampMilli
	 * @return the timestampMilli
	 */
	public long getTimeStampMillis()
	{
		return this.timeStampMillis;
	}
	
	/**
	 * The getter of hasError flag
	 * @return flag shows if there is an error
	 */
	public boolean hasError()
	{
		return this.hasError;
	}
	/**
	 * The setter of  name
	 * @param name of the data
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 * The setter of the stateData
	 * @param new statusdata to be set
	 */
	public void setStateData(String data)
	{
		this.stateData = data;
	}
	/**
	 * The setter of the stateCode
	 * @param new statusCode to be set
	 */
	public void setStatusCode(int code)
	{
		this.statusCode = code;
	}
	
	/**
	 * Update a data with another one
	 * @param data    The data used
	 */
	public void updateData(BaseIotData data)
	{
		// TODO: update local var's
		
		handleUpdateData(data);
	}
	
	
	// protected methods
	
	/**
	 * Template method to handle data update for the sub-class.
	 * 
	 * @param BaseIotData While the parameter must implement this method,
	 * the sub-class is expected to cast the base class to its given type.
	 */
	protected abstract void handleUpdateData(BaseIotData data);
	
}
