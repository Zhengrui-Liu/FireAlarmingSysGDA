/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.SystemStateData;

import programmingtheiot.gda.connection.CloudClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.connection.ICloudClient;
import programmingtheiot.gda.connection.IPersistenceClient;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.IRequestResponseClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.connection.SmtpClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;
import com.ubidots.*;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DeviceDataManager implements IDataMessageListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager.class.getName());
	
	
	
	
	// private var's
	
	private boolean enableMqttClient = true;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	private ConfigUtil configUtil = null;
	private DataUtil dataUtil = null;
	
	private IPubSubClient mqttClient = null;
	private ICloudClient cloudClient = null;
	private IPersistenceClient persistenceClient = null;
	private IRequestResponseClient smtpClient = null;
	private CoapServerGateway coapServer = null;
	private static String _DebugDataPath = "";
	private SystemPerformanceManager sysPerfManager; // new instance of SystemPerformanceManager
	private float humidCeiling;
	private float humidFloor;
	private boolean imageSent = false;
	private int latestImage = 0;
	//public Thread t = new MyThread();
	//private ICloudClient ubidotsClient = null;
	
	
	
	// constructors
	/**
	 * default constructors
	 * DeviceDataManager includes the processes of: CoapServer, MqttClient, SystemPerformanceManager,CloudClient
	 * DDM is used for integrating all modules into a system.
	 */
	public DeviceDataManager()
	{
		super();
		configUtil = ConfigUtil.getInstance();
		this.sysPerfManager = new SystemPerformanceManager();
		this.sysPerfManager.setDataListener(this);
		this.enableMqttClient  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
		this.enableCoapServer  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
		this.enableCloudClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
		this.enableSmtpClient  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SMTP_CLIENT_KEY);
		this.enablePersistenceClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
		this._DebugDataPath = ConfigUtil.getInstance().getProperty(ConfigConst.GATEWAY_DEVICE, ConfigConst.DDM_DEBUG_FILE_PATH_KEY);
		this.humidFloor = configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, ConfigConst.TRIGGER_HUMID_FLOOR_KEY);
		this.humidCeiling = configUtil.getFloat(ConfigConst.GATEWAY_DEVICE, ConfigConst.TRIGGER_HUMID_CEILING_KEY);
		initConnections();
	}
	
	/**
	 * constructor with params
	 */
	public DeviceDataManager(
		boolean enableMqttClient,
		boolean enableCoapServer,
		boolean enableCloudClient,
		boolean enableSmtpClient,
		boolean enablePersistenceClient)
	{
		super();
		this.sysPerfManager = new SystemPerformanceManager();
		this.sysPerfManager.setDataListener(this);
		configUtil = ConfigUtil.getInstance();
		this._DebugDataPath = ConfigUtil.getInstance().getProperty(ConfigConst.GATEWAY_DEVICE, ConfigConst.DDM_DEBUG_FILE_PATH_KEY);
		this.enableCloudClient = enableCloudClient;
		this.enableMqttClient = enableMqttClient;
		this.enableCoapServer = enableCoapServer;
		this.enableSmtpClient = enableSmtpClient;
		this.enablePersistenceClient = enablePersistenceClient;
		
		initConnections();
	}
	
	
	// public methods
	/**
	 * Judge the statement of the connect and out put the returned data to file
	 * @param ResourceNameEnum resourceName  the resource name of the data
	 * @param ActuatorData data the data to be handled
	 * @return boolean  a succeed flag
	 */
	@Override
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
	{
		_Logger.info("handleActuatorCommandResponse is called............");
		String actStr = DataUtil.getInstance().actuatorDataToJson(data);
		_Logger.info("The response data is: "+data.toString());
		if(this.persistenceClient.isValid()) {
			//return this.persistenceClient.storeData("topic",0, data);
		}
		if(data.getAsResponse() == true) {
			_Logger.info("Got the response of Actuator Data!!!!!!");
		}
		else {
			this.mqttClient.publishMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, actStr, 0);
		}
		return false;
	}
	/**
	 * convert incoming json data to data instance
	 * @param ResourceNameEnum resourceName   resourceName  the resource name of the data
	 * @param String msg  the data to be handled
	 * @return boolean  a succeed flag
	 */
	@Override
	public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
	{
		_Logger.info("handleIncomingMessage is called............");
		this.dataUtil = DataUtil.getInstance();
		try {
			ActuatorData ad = dataUtil.jsonToActuatorData(msg);	
			handleIncomingDataAnalysis(resourceName,ad);
			return true;
		}catch(Exception e) {
			_Logger.info("Not able to convert msg to actuate data.");
		}
		try {
			SystemStateData ssd = dataUtil.jsonToSystemStateData(msg);	
			handleIncomingDataAnalysis(resourceName,ssd);
			return true;
		}catch(Exception e) {
			_Logger.info("Not able to convert msg to system state data.");
		}
		return false;
	}

	/**
	 * Judge the statement of the connect and out put the returned data to file
	 * Store the sensor data in csv file
	 * send an email to an user
	 * @param ResourceNameEnum resourceName  the resource name of the data
	 * @param SensorData data the data to be handled
	 * @return boolean a succeed flag
	 */
	@Override
	public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
	{
		_Logger.info("handleSensorMessage is called............"+data.getValue());
		try {
			
			if(this.cloudClient != null) {
				this.cloudClient.sendEdgeDataToCloud(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, data);
			}
			
			if(data.getSensorType() == SensorData.HUMIDITY_SENSOR_TYPE) {
				if(data.getValue()>this.humidCeiling || data.getValue()<this.humidFloor) {
					ActuatorData respondActuatorData = new ActuatorData();
					respondActuatorData.setCommand(ActuatorData.COMMAND_ON);
					respondActuatorData.setStateData("Commond on");
					respondActuatorData.setValue(this.humidFloor);
					respondActuatorData.setActuatorType(ActuatorData.HUMIDIFIER_ACTUATOR_TYPE);
					respondActuatorData.setName(ConfigConst.HUMIDIFIER_ACTUATOR_NAME);
					String jsonActuatorData = DataUtil.getInstance().actuatorDataToJson(respondActuatorData);
					this.mqttClient.publishMessage(resourceName,jsonActuatorData,0);
					if(this.persistenceClient.isValid()) {
						this.persistenceClient.storeData(respondActuatorData, "actuatordata.csv");
					}
				}
				else {
					ActuatorData respondActuatorData = new ActuatorData();
					respondActuatorData.setCommand(ActuatorData.COMMAND_OFF);
					respondActuatorData.setStateData("Commond off");
					respondActuatorData.setValue(this.humidFloor);
					respondActuatorData.setActuatorType(ActuatorData.HUMIDIFIER_ACTUATOR_TYPE);
					respondActuatorData.setName(ConfigConst.HUMIDIFIER_ACTUATOR_NAME);
					String jsonActuatorData = DataUtil.getInstance().actuatorDataToJson(respondActuatorData);
					this.mqttClient.publishMessage(resourceName,jsonActuatorData,0);
					if(this.persistenceClient.isValid()) {
						this.persistenceClient.storeData(respondActuatorData, "actuatordata.csv");
						//return this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
					}
					
				}
			}
			else if(data.getSensorType() == SensorData.TEMP_SENSOR_TYPE) {
				if(data.getValue() > 45.0 && this.imageSent == false) {
					
					this.sendEmailTo("jerryliubupt@gmail.com",data.getValue());
					this.imageSent = true;
				}
				else if(data.getValue() < 45.0 && this.imageSent == true) {
					this.imageSent = false;
				}
			}
			if(this.persistenceClient.isValid()) {
				this.persistenceClient.storeData(data, "sensordata.csv");
				//return this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
			}
			//this.handleUpstreamTransmission(resourceName, jsonSensorData);
			return true;
		}catch(Exception e) {
			_Logger.info("handled failed (handleSensorMessage)");
			return false;
		}
	}

	/**
	 * Judge the statement of the connect and out put the returned data to file
	 * Store the data in a csv file
	 * @param ResourceNameEnum resourceName the resource name of the data
	 * @param  SystemPerformanceData data the data to be handled
	 * @return boolean teh flad for succeed
	 */
	@Override
	public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
	{
		_Logger.info("handleSystemPerformanceMessage is called............");
		
		try {
			System.out.println("*********************"+ resourceName.getDeviceName());
			if(this.cloudClient != null) {
				this.cloudClient.sendEdgeDataToCloud(resourceName, data);
			}
			if(this.persistenceClient.isValid()) {
				if(data.getDevice() == "GDA") {
					this.persistenceClient.storeData(data,"", 2);	
				}
				else {
					this.persistenceClient.storeData(data,"", 1);
				}
				//return this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
			}
			//this.handleUpstreamTransmission(resourceName, jsonSensorData);
			return true;
		}catch(Exception e) {
			_Logger.info("handled failed (handleSensorMessage)");
			return false;
		}
	}
	/**
	 * Start DDM
	 * Start coap/mqtt client
	 * Start cloud client
	 * Start performance task
	 */
	public void startManager()
	{
		_Logger.info("Starting DeviceDataManager...");
		this.sysPerfManager.startManager();
		if(this.enablePersistenceClient) {
			boolean pcFlag = this.persistenceClient.connectClient();
			if(!pcFlag) {
				_Logger.info("connection failed");
			}
			else {
				_Logger.info("connected");
			}
		}
		if(this.enableCoapServer) {
			this.coapServer = new CoapServerGateway();
			boolean csFlag = this.coapServer.startServer();
			if(!csFlag) {
				_Logger.info("Starting failed");
			}
			else {
				_Logger.info("Started");
			}
		}
		if(this.enableMqttClient) {
			this.mqttClient = new MqttClientConnector();
			this.mqttClient.setDataMessageListener(this);
			this.mqttClient.connectClient();
			this.cloudClient = new CloudClientConnector();
			this.cloudClient.setDataMessageListener(this);
			this.cloudClient.connectClient();
			
		}
		
	}
	/**
	 * Stop DDM
	 * Stop coap/mqtt client
	 * Stop performance task
	 */
	public void stopManager()
	{
		_Logger.info("Stopping DeviceDataManager...");
		this.sysPerfManager.stopManager();
		if(this.enablePersistenceClient) {
			boolean pcFlag = this.persistenceClient.disconnectClient();
			if(!pcFlag) {
				_Logger.info("disconnection failed");
			}
			else {
				_Logger.info("disconnected");
			}
		}
		if(this.enableCoapServer) {
			boolean csFlag = this.coapServer.stopServer();
			if(!csFlag) {
				_Logger.info("Stopping failed");
			}
			else {
				_Logger.info("Stopped");
			}
		}
		if(this.enableMqttClient) {
			this.mqttClient.disconnectClient();
			this.cloudClient.disconnectClient();
		}
		
	}

	
	// private methods
	// Unused Methods
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, SystemStateData data) {
		_Logger.info("handleIncomingDataAnalysis(SYSTEM)......................");
	}
	
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, ActuatorData data) {
		_Logger.info("handleIncomingDataAnalysis(ACTUATOR)......................");
	}
	
	private void handleUpstreamTransmission(ResourceNameEnum resourceName, String msg) {
		_Logger.info("handleUpstreamTransmission......................");
	}
	
	
	/**
	 * Initializes the enabled connections. This will NOT start them, but only create the
	 * instances that will be used in the {@link #startManager() and #stopManager()) methods.
	 * 
	 */
	private void initConnections()
	{
		this.persistenceClient = new RedisPersistenceAdapter();
		Thread t = new MyThread();
        t.start();
	}
	/**
	 * Send an email to an address
	 * @param email   the address to be sent an email to
	 * @param value   the temperature to be sent
	 * @return a succeed flag
	 */
	public boolean sendEmailTo(String email, float value) {
		// Recipient's email ID needs to be mentioned.
        String to = email;

        // Sender's email ID needs to be mentioned
        String from = "test@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("jerryliubupt@gmail.com", "1319769359");

            }

        });

        //session.setDebug(true);
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Temperature alarm!!!");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart attachmentPart = new MimeBodyPart();

            MimeBodyPart textPart = new MimeBodyPart();

            try {

                File f =new File("/Users/liu.zhengr/Downloads/server"+this.latestImage+".jpeg");

                attachmentPart.attachFile(f);
                textPart.setText("The temperature has reached: "+value+"! Please check the latest picture!!!");
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);

            } catch (IOException e) {

                e.printStackTrace();

            }

            message.setContent(multipart);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
		
	}
	
	class MyThread extends Thread {
		
		private int latestImage = 0;
		/**
		 * The constructor of the socket thread
		 */
		public MyThread() {
			super();
			
		}
		/**
		 * Create a socket server and listen to the connection of clients
		 */
	    @Override
	    public void run() {
	    	while(true) {
	    		try {
					ServerSocket ss = new ServerSocket(5612);
					Socket s = ss.accept();
					InputStream in = s.getInputStream();
					FileOutputStream fos = new FileOutputStream("/Users/liu.zhengr/Downloads/server"+this.latestImage+".jpeg");
					byte[] buf = new byte[1024];
					int len = 0;
					this.latestImage++;
					while ((len = in.read(buf)) != -1)
					{
						fos.write(buf,0,len);
					}
					fos.close();
					in.close();
					s.close();
					ss.close();
				}catch(Exception e){
					System.out.println("failed!");
				}
			}
		}
	}
}
