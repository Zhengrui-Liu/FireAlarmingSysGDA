/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.nio.file.FileSystems;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DataUtil
{
	// static
	
	private static final DataUtil _Instance = new DataUtil();

	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil  the only Instance of the Singleton
	 */
	public static final DataUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	
	
	// constructors
	
	/**
	 * Default (private).
	 * 
	 */
	private DataUtil()
	{
		super();
	}
	
	
	// public methods
	/**
	 * convert actuatorData To Json
	 * @param actuatorData  Data before transform
	 * @return String  target String
	 */
	public String actuatorDataToJson(ActuatorData actuatorData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(actuatorData);
		return jsonData;
	}
	
	/**
	 * Convert sensorData To Json
	 * @param sensorData Data before transform
	 * @return String  target String
	 */
	public String sensorDataToJson(SensorData sensorData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(sensorData);
		return jsonData;
	}
	
	/**
	 * Convert systemPerformanceData To Json
	 * @param sysPerfData  Data before transform
	 * @return String  target String
	 */
	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(sysPerfData);
		return jsonData;
	}
	
	/**
	 * Convert systemStateData To Json
	 * @param sysStateData  Data before transform
	 * @return String  target String
	 */  
	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(sysStateData);
		return jsonData;
	}
	
	/**
	 * Convert json To ActuatorData
	 * @param jsonData  String before transform
	 * @return ActuatorData The target data
	 */
	public ActuatorData jsonToActuatorData(String jsonData)
	{
		Gson gson = new Gson();
		ActuatorData actuatorData = gson.fromJson(jsonData, ActuatorData.class);
		return actuatorData;
	}
	
	/**
	 * Convert json To SensorData
	 * @param jsonData   String before transform
	 * @return SensorData  The target data
	 */
	public SensorData jsonToSensorData(String jsonData)
	{
		Gson gson = new Gson();
		SensorData sensorData = gson.fromJson(jsonData, SensorData.class);
		return sensorData;
	}
	/**
	 * Convert json To SystemPerformanceData
	 * @param jsonData  String before transform
	 * @return SystemPerformanceData    The target data
	 */
	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		Gson gson = new Gson();
		SystemPerformanceData sysPerfData = gson.fromJson(jsonData, SystemPerformanceData.class);
		return sysPerfData;
	}
	/**
	 * Convert json To SystemStateData
	 * @param jsonData   String before transform
	 * @return SystemStateData  The target data
	 */
	public SystemStateData jsonToSystemStateData(String jsonData)
	{
		Gson gson = new Gson();
		SystemStateData sysStateData = gson.fromJson(jsonData, SystemStateData.class);
		return sysStateData;
	}
	
}
