/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
/**
 * Shell representation of class for student implementation.
 * 
 */
public class RedisPersistenceAdapter implements IPersistenceClient
{
	// static
	ConfigUtil config = ConfigUtil.getInstance();
	private static final Logger _Logger =
		Logger.getLogger(RedisPersistenceAdapter.class.getName());
	private static String _DebugDataPath = "";
	private static String DATA_GATEWAY_SERVICE = "Data.GatewayService";
	File f = new File("/private/tmp/sensordata.csv");
	File fa = new File("/private/tmp/actuatordata.csv");
	File fsc = new File("/private/tmp/systemperformancedataCDA.csv");
	File fsg = new File("/private/tmp/systemperformancedataGDA.csv");
	private CsvMapper csvMapper;
	private CsvSchema csvSchema;
	private CsvMapper csvMapperActuator;
	private CsvSchema csvSchemaActuator;
	private CsvMapper csvMapperSys;
	private CsvSchema csvSchemaSys;
	// private var's
	
	// constructors
	
	/**
	 * Default constructor.
	 * Initialization of the schema of csv files
	 */
	public RedisPersistenceAdapter()
	{
		super();
		this._DebugDataPath = ConfigUtil.getInstance().getProperty(ConfigConst.GATEWAY_DEVICE, ConfigConst.DDM_DEBUG_FILE_PATH_KEY);
		
		//String dataStr1 = "[{\"value\":19.390625,\"sensorType\":3,\"name\":\"TempSensor\",\"timeStamp\":\"2020-12-11 22:58:23.738332\",\"hasError\":false,\"statusCode\":0,\"timeStampMillis\":0,\"stateData\":\"defaultData\"}]";
		//JsonNode jsonTree;
		//jsonTree = new ObjectMapper().readTree(dataStr1);
		//Builder csvSchemaBuilder = CsvSchema.builder();
		//JsonNode firstObject = jsonTree.elements().next();
		//firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
		//this.csvSchema = csvSchemaBuilder.build().withHeader();
		this.csvMapper = new CsvMapper();
		this.csvSchema = CsvSchema.builder().addColumn("value").addColumn("sensorType").addColumn("name").addColumn("timeStamp").addColumn("hasError").addColumn("statusCode").addColumn("timeStampMillis").addColumn("stateData").build();
		this.csvMapperActuator = new CsvMapper();
		this.csvSchemaActuator = CsvSchema.builder().addColumn("value").addColumn("command").addColumn("name").addColumn("timeStamp").addColumn("asResponse").addColumn("actuatorType").addColumn("timeStampMillis").addColumn("stateData").addColumn("hasError").addColumn("statusCode").build();
		this.csvMapperSys = new CsvMapper();
		this.csvSchemaSys = CsvSchema.builder().addColumn("device").addColumn("cpuUtilization").addColumn("memoryUtilization").addColumn("name").addColumn("timeStamp").addColumn("timeStampMillis").addColumn("stateData").addColumn("hasError").addColumn("statusCode").build();
		
		
		initClient();
	}
	
	
	// public methods
	/**
	 * Unused methods
	 */
	@Override
	public boolean connectClient()
	{
		return false;
	}
	/**
	 * Unused methods
	 */
	@Override
	public boolean disconnectClient()
	{
		return false;
	}
	/**
	 * Unused methods
	 */
	@Override
	public ActuatorData[] getActuatorData(String topic, Date startDate, Date endDate)
	{
		return null;
	}
	/**
	 * Unused methods
	 */
	@Override
	public SensorData[] getSensorData(String topic, Date startDate, Date endDate)
	{
		return null;
	}
	/**
	 * Unused methods
	 */
	@Override
	public void registerDataStorageListener(Class cType, IPersistenceListener listener, String... topics)
	{
	}

	/**
	 * Store Actuatordata in .dat files
	 * @param String topic  topic of the message
	 * @param int qos  qos level used
	 * @param ActuatorData... data  actuator data to be dtored
	 * @return boolean succeed flag
	 */
	@Override
	public boolean storeData(String topic, int qos, ActuatorData... data)
	{
		String fileName = _DebugDataPath + "/handleActuatorCommandResponse.dat";
		
		_Logger.info("\n\n----- [ActuatorData to JSON to file] -----");
	
		for(int i=0;i<data.length;i++) {
			try {
				Path   filePath = FileSystems.getDefault().getPath(fileName);
				String dataStr  = DataUtil.getInstance().actuatorDataToJson(data[i]);
				
				
				_Logger.info("handleActuatorCommandResponse actuatorData JSON (validated): " + dataStr);
				_Logger.info("Writing actuatorData JSON to GDA data path: " + filePath);
				
				Files.writeString(filePath, dataStr, StandardCharsets.UTF_8);
			} catch (Exception e) {
				_Logger.log(Level.WARNING, "Failed to write file: " + fileName, e);
			}
			
			if(data[i].hasError()) {
				_Logger.info("Error data");
				return false;
			}
			return true;
		}

		return false;
	}
	/**
	 * Store sensor data to a .csv file
	 * @param data the data to be stored
	 * @param file name the file name
	 */
	public boolean storeData(SensorData data, String filename)
	{
		try {
			//String dataStr  = DataUtil.getInstance().sensorDataToJson(data);
			//String dataStr1 = data.getValue()+","+data.getSensorType()+","+data.getName()+","+data.getTimeStamp()+","+data.hasError()+","+data.getStatusCode()+","+data.getTimeStampMillis()+","+data.getStateData()+"]";
			//System.out.println("***********"+csvSchema.getColumnDesc()+"******************8");
	        ObjectWriter writer = this.csvMapper.writer(this.csvSchema);
	        OutputStream outstream = new FileOutputStream(this.f , true);
	        writer.writeValue(outstream,data);
			/*csvMapper.writerFor(JsonNode.class)
				.with(csvSchema)
				.writeValue(this.f,jsonTree);*/
			return true;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	/**
	 * Store actuator data to a .csv file
	 * @param data the data to be stored
	 * @param file name the file name
	 */
	public boolean storeData(ActuatorData data, String filename)
	{
		try {
	        ObjectWriter writer = this.csvMapperActuator.writer(this.csvSchemaActuator);
	        OutputStream outstream = new FileOutputStream(this.fa , true);
	        writer.writeValue(outstream,data);
			return true;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	/**
	 * Store system performance data to a .csv file
	 * Different file for different device
	 * @param data the data to be stored
	 * @param file name the file name
	 */
	public boolean storeData(SystemPerformanceData data, String filename, int device)
	{
		try {
	        ObjectWriter writer = this.csvMapperSys.writer(this.csvSchemaSys);
	        OutputStream outstream;
	        if(device == 1) {
	        	outstream = new FileOutputStream(this.fsc , true);	
	        }
	        else {
	        	outstream = new FileOutputStream(this.fsg , true);	
	        }
	        writer.writeValue(outstream,data);
			return true;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * Store Sensordata in files
	 * @param String topic  topic of the message
	 * @param int qos  qos level used
	 * @param SensorData... data  actuator data to be dtored
	 * @return boolean succeed flag
	 */
	@Override
	public boolean storeData(String topic, int qos, SensorData... data)
	{
		String fileName = _DebugDataPath + "/handleActuatorCommandResponse.dat";
		
		_Logger.info("\n\n----- [ActuatorData to JSON to file] -----");
	
		for(int i=0;i<data.length;i++) {
			try {
				Path   filePath = FileSystems.getDefault().getPath(fileName);
				String dataStr  = DataUtil.getInstance().sensorDataToJson(data[i]);
				
				_Logger.info("handleActuatorCommandResponse actuatorData JSON (validated): " + dataStr);
				_Logger.info("Writing actuatorData JSON to GDA data path: " + filePath);
				
				Files.writeString(filePath, dataStr, StandardCharsets.UTF_8);
			} catch (Exception e) {
				_Logger.log(Level.WARNING, "Failed to write file: " + fileName, e);
			}
			
			if(data[i].hasError()) {
				_Logger.info("Error data");
				return false;
			}
			return true;
		}

		return false;
	}

	/**
	 * Store SystemPerformancedata in files
	 * @param String topic
	 * @param int qos
	 * @param ActuatorData... data
	 * @return boolean succeed flag
	 */
	@Override
	public boolean storeData(String topic, int qos, SystemPerformanceData... data)
	{
		String fileName = _DebugDataPath + "/handleActuatorCommandResponse.dat";
		
		_Logger.info("\n\n----- [ActuatorData to JSON to file] -----");
	
		for(int i=0;i<data.length;i++) {
			try {
				Path   filePath = FileSystems.getDefault().getPath(fileName);
				String dataStr  = DataUtil.getInstance().systemPerformanceDataToJson(data[i]);
				
				_Logger.info("handleActuatorCommandResponse actuatorData JSON (validated): " + dataStr);
				_Logger.info("Writing actuatorData JSON to GDA data path: " + filePath);
				
				Files.writeString(filePath, dataStr, StandardCharsets.UTF_8);
			} catch (Exception e) {
				_Logger.log(Level.WARNING, "Failed to write file: " + fileName, e);
			}
			
			if(data[i].hasError()) {
				_Logger.info("Error data");
				return false;
			}
			return true;
		}

		return false;
	}
	
	/**
	 * demo validate: judge if active
	 * @return
	 */
	public boolean isValid() {
		return true;
	}
	
	// private methods
	
	/**
	 * Generates a listener key map from the class type and topic.
	 * The format will be as follows:
	 * <br>'simple class name' + "_" + 'topic name'
	 * <br>e.g. ActuatorData_localhost/fan
	 * <br>e.g. SensorData_localhost/temperature
	 * <p>
	 * If the class type is null, it will simply be dropped and
	 * only the topic name will be used in the key. If the topic
	 * name is also null or invalid (e.g. empty), the 'all' keyword
	 * will be used instead.
	 * 
	 * @param cType The class type to use in the key.
	 * @param topic The topic name to use in the key.
	 * @return String The key derived from cType and topic, as per above.
	 */
	private String getListenerMapKey(Class cType, String topic)
	{
		StringBuilder buf = new StringBuilder();
		
		if (cType != null) {
			buf.append(cType.getSimpleName()).append("_");
		}
		
		if (topic != null && topic.trim().length() > 0) {
			buf.append(topic.trim());
		} else {
			buf.append("all");
		}
		
		String key = buf.toString();
		
		_Logger.info("Generated listener map lookup key from '" + cType + "' and '" + topic + "': " + key);
		
		return key;
	}
	
	/**
	 * unused method
	 */
	private void initClient()
	{
	}
	/**
	 * Unused method
	 */
	private Long updateRedisDataElement(String topic, double score, String payload)
	{
		return 0L;
	}




}
