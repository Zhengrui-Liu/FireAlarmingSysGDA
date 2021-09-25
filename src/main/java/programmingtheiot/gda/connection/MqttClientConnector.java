/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import javax.net.ssl.SSLSocketFactory;
import programmingtheiot.common.SimpleCertManagementUtil;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
//import programmingtheiot.gda.connection.CloudClientConnector.CloudMessageListener;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class MqttClientConnector implements IPubSubClient, MqttCallbackExtended
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(MqttClientConnector.class.getName());
	
	private static final int DEFAULT_QOS = 0;
	
	// params
	
	private String protocol;
	private String host;
	private int port;
	private int brokerKeepAlive;
	private String clientIDCloud;
	private String clientID;
	private MemoryPersistence persistence;
	private MqttConnectOptions connOpts;
	private MqttConnectOptions connOptsCloud;
	private String brokerAddr;
	private MqttClient mqttClient;
	private boolean enableEncryption;
	private String pemFileName;
	private boolean useCleanSession;
	private boolean enableAutoReconnect;
	private boolean useCloudGatewayConfig = false;
	
	IDataMessageListener dataMsgListener = null;
	// constructors
	
	/**
	 * Default constructor.
	 * Initialzation of the vaariables
	 */
	public MqttClientConnector()
	{
		this(false);
		initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
	}
	

	/**
	 * Constructor used by the cloud client
	 * @param useCloudGatewayConfig a flag to determine if it is aconnection to ubidots
	 */
	public MqttClientConnector(boolean useCloudGatewayConfig)
	{
		super();
		
		this.useCloudGatewayConfig = useCloudGatewayConfig;
		
		if (useCloudGatewayConfig) {
			initClientParameters(ConfigConst.CLOUD_GATEWAY_SERVICE);
		} else {
			initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
		}
	}
	
	
	// public methods
	/**
	 * connect MQTT Client
	 */
	@Override
	public boolean connectClient()
	{
		_Logger.info("Client Connecting");
		if (this.mqttClient == null) {
		    try {
				this.mqttClient = new MqttClient(this.brokerAddr, this.clientID, this.persistence);
				this.mqttClient.setTimeToWait(3000);
				//this.mqttClient.setCallback(new CloudMessageListener());
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    this.mqttClient.setCallback(this);
		}

		if (! this.mqttClient.isConnected()) {
		    try {
				this.mqttClient.connect(this.connOpts);
			} catch (MqttSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return true;
		}
		
		return false;
	}

	/**
	 * disconnect MQTT Client
	 */
	@Override
	public boolean disconnectClient()
	{
		_Logger.info("Client Disconnecting");
		if(this.mqttClient.isConnected()) {
			try {
				this.mqttClient.disconnect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/**
	 * Judge if the client is connected
	 * @return boolean flag to show connect status
	 */
	public boolean isConnected()
	{
		if(this.mqttClient == null) {
			_Logger.warning("no client detected!");
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * publish a message of a topic
	 * @param topicName: ResourceNameEnum resource name for the topic
	 * @param msg: String the message to be handled
	 * @param qos: int  qos used
	 */
	@Override
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)
	{
		//_Logger.info("publishMessage is called!");
		if (qos > 2 || qos < 0) {
			qos = this.DEFAULT_QOS;
		}
		if (topicName == null) {
			return false;
		}	
		//this.mqttClient.publish(topicName.getResourceName(), msg.getBytes() , qos, false);
		this.publishMessage(topicName.getResourceName(), msg.getBytes(), qos);
		return true;
	}

	
	/**
	 * subscribe a topic
	 * @param topicName: ResourceNameEnum resource name for the topic
	 * @param qos: int  qos used
	 */
	@Override
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos)
	{
		_Logger.info("subscribeToTopic is called!");
		if(qos > 2 || qos < 0) {
			qos = this.DEFAULT_QOS;
		}
		if(topicName == null) {
			return false;
		}
		this.subscribeToTopic(topicName.getResourceName(), qos);
		return true;
	}

	/**
	 * unsubscribe a topic
	 * @param topicName: ResourceNameEnum resource name for the topic
	 */
	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		_Logger.info("unsubscribeFromTopic is called!");
		/*try {
			this.mqttClient.unsubscribe(topicName.getResourceName());
			return true;
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			this.unsubscribeFromTopic(topicName.getResourceName());
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * Setter of the data manager
	 * @param the listen to be used
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		_Logger.info("setDataMessageListener is called!");
		if (listener != null) {
            this.dataMsgListener = listener;
            return true;
        }
	
        return false;
	}
	
	// callbacks
	/**
	 * Creat a topic for a specific resource (used by client)
	 * @param resource the resource name in the uri
	 * @return the topic url
	 */
	private String createTopicName(ResourceNameEnum resource)
	{
		return "/v1.6/devices/" + resource.getDeviceName() + "/" + resource.getResourceType()+"/lv";
	}
	
	/**
	 * Callback method method called when mqtt is connected
	 * subscribe the topics
	 * @param boolean reconnect if an auto reconnection is used
	 * @param String serverURI the uri used
	 */
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
		
		int qos = 0;
		
		// Option 2
		try {
			System.out.println("this.useCloudGatewayConfig is :"+ this.useCloudGatewayConfig);
			System.out.println(this.createTopicName(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE));
			
			if(this.useCloudGatewayConfig) {
				this.mqttClient.subscribe(
					this.createTopicName(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE),
					qos,
					new CloudMessageListener(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, this.dataMsgListener));
				System.out.println("connected!");
			}
			else {
				this.mqttClient.subscribe(
						ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
						qos,
						new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
				System.out.println("EnableCloud1");
				this.mqttClient.subscribe(
						ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
						qos,
						new SensorMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
				System.out.println("EnableCloud2");
				this.mqttClient.subscribe(
						ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
						qos,
						new CDAPerformanceMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));	
				System.out.println("EnableCloud2");
			}
			
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic.");
		}
		
	}

	/**
	 * Callback method called when the connection break
	 * @param Throwable t
	 */
	@Override
	public void connectionLost(Throwable t)
	{
		_Logger.info("connectionLost is called!");
	}
	
	/**
	 * Callback method called when the sending process finished
	 * @param IMqttDeliveryToken token
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		//_Logger.info("deliveryComplete is called!");
	}
	
	
	/**
	 * Callback function called when get a message of subscribed topic
	 * @param String topic  topic of the message
	 * @param MqttMessage msg  message to be handled
	 */
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		_Logger.info("messageArrived is called!"+msg.getId());
		try {
			_Logger.info("Payload from Cloud:  "+(new String(msg.getPayload())));
			ActuatorData actuatorData = new ActuatorData();
			actuatorData.setActuatorType(ActuatorData.LED_DISPLAY_ACTUATOR_TYPE);
			actuatorData.setAsResponse(false);
			actuatorData.setValue(Float.parseFloat(new String(msg.getPayload())));
			if(actuatorData.getValue() == 1.0) {
				actuatorData.setCommand(ActuatorData.COMMAND_ON);	
			}
			else {
				actuatorData.setCommand(ActuatorData.COMMAND_OFF);	
			}
			actuatorData.setName("LED pressure");
			//_Logger.info("Sensordata:  "+sensorData.getValue());
			//_Logger.info(DataUtil.getInstance().sensorDataToJson(sensorData));
			if (this.dataMsgListener != null) {
				this.dataMsgListener.handleActuatorCommandResponse(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, actuatorData);
			}
			else {
				_Logger.info("dml is null");
			}
		} catch (Exception e) {
			_Logger.warning("Failed to convert message payload to SensorData.");
		}
	}

	
	// private methods
	
	/**
	 * Called by the constructor to set the MQTT client parameters to be used for the connection.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initClientParameters(String configSectionName)
	{
		// TODO: implement this
		this.useCleanSession = false;
		this.enableAutoReconnect = true;
		this.protocol = ConfigConst.DEFAULT_MQTT_PROTOCOL;
		
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.host =
			configUtil.getProperty(
				configSectionName, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		this.port =
			configUtil.getInteger(
				configSectionName, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
		this.brokerKeepAlive =
			configUtil.getInteger(
				configSectionName, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		this.enableEncryption =
			configUtil.getBoolean(
				configSectionName, ConfigConst.ENABLE_CRYPT_KEY);
		this.pemFileName =
			configUtil.getProperty(
				configSectionName, ConfigConst.CERT_FILE_KEY);
		
		// Paho Java client requires a client ID
		this.clientID = MqttClient.generateClientId();
		
		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(this.useCleanSession);
		this.connOpts.setAutomaticReconnect(this.enableAutoReconnect);
		
		
		// if encryption is enabled, try to load and apply the cert(s)
		if (this.enableEncryption) {
			initSecureConnectionParameters(configSectionName);
		}
		
		// if there's a credential file, try to load and apply them
		if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY)&&this.useCloudGatewayConfig==true) {
			initCredentialConnectionParameters(configSectionName);
		}
		
		// NOTE: URL does not have a protocol handler for "tcp" or "ssl",
		// so construct the URL manually
		this.brokerAddr  = this.protocol + "://" + this.host + ":" + this.port;
		
		_Logger.info("Using URL for broker conn: " + this.brokerAddr);
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to load credentials.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	
	/**
	 * Called by {@link #initClientParameters(String)} to enable encryption.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initSecureConnectionParameters(String configSectionName)
	{
		// TODO: implement this
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		try {
			_Logger.info("Configuring TLS...");
			
			if (this.pemFileName != null) {
				File file = new File(this.pemFileName);
				
				if (file.exists()) {
					_Logger.info("PEM file valid. Using secure connection: " + this.pemFileName);
				} else {
					this.enableEncryption = false;
					
					_Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + pemFileName, new Exception());
					
					return;
				}
			}
			
			SSLSocketFactory sslFactory =
				SimpleCertManagementUtil.getInstance().loadCertificate(this.pemFileName);
			
			this.connOpts.setSocketFactory(sslFactory);
			
			// override current config parameters
			this.port =
				configUtil.getInteger(
					configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
			
			this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;
			
			_Logger.info("TLS enabled.");
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
			
			this.enableEncryption = false;
		}	
	}
	
	
	/**
	 * Listener for Actuatordata from CDA
	 * @author liu.zhengr
	 */
	private class ActuatorResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		/**
		 * Default Constructor
		 * @param resource  resource name to be used
		 * @param dataMsgListener the data listener for a topic
		 */
		ActuatorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		/**
		 * Callback function called when get a message of ActuatorData
		 * @param String topic the topic of the arrived message
		 * @param MqttMessage msg the message
		 */
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			try {
				ActuatorData actuatorData =
					DataUtil.getInstance().jsonToActuatorData(new String(message.getPayload()));
				
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleActuatorCommandResponse(resource, actuatorData);
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to ActuatorData.");
			}
		}
		
	}
	
	/**
	 * Listener for Sensordata from CDA
	 * @author liu.zhengr
	 */
	private class SensorMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		/**
		 * Default Constructor
		 * @param resource  resource name to be used
		 * @param dataMsgListener the data listener for a topic
		 */
		SensorMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		/**
		 * Callback function called when get a message of ActuatorData
		 * @param String topic the topic of the arrived message
		 * @param MqttMessage msg the message
		 */
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
	
			try {
				_Logger.info("Payload from CDA:  "+(new String(message.getPayload())));
				SensorData sensorData =
					DataUtil.getInstance().jsonToSensorData(new String(message.getPayload()));
				//_Logger.info("Sensordata:  "+sensorData.getValue());
				//_Logger.info(DataUtil.getInstance().sensorDataToJson(sensorData));
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleSensorMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, sensorData);
				}
				else {
					_Logger.info("dml is null");
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to SensorData.");
			}
		}
		
	}
	
	/**
	 * Callback function called when get a message of Actuation command from cloud
	 * @param String topic
	 * @param MqttMessage msg
	 */
	private class CloudMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		/**
		 * Default Constructor
		 * @param resource  resource name to be used
		 * @param dataMsgListener the data listener for a topic
		 */
		CloudMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		/**
		 * Callback function called when get a message of ActuatorData
		 * @param String topic the topic of the arrived message
		 * @param MqttMessage msg the message
		 */
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
	
			try {
				_Logger.info("Payload from Cloud:  "+(new String(message.getPayload())));
				/*ActuatorData actuatorData =
					DataUtil.getInstance().jsonToCloudDatatoActuatorData(new String(message.getPayload()));*/
				//_Logger.info("Sensordata:  "+sensorData.getValue());
				//_Logger.info(DataUtil.getInstance().sensorDataToJson(sensorData));
				if (this.dataMsgListener != null) {
					//this.dataMsgListener.handleActuatorCommandResponse(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, actuatorData);
				}
				else {
					_Logger.info("dml is null");
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to SensorData.");
			}
		}
		
	}
	
	/**
	 * Callback function called when get a message of CDA System performance from cloud
	 * @param String topic
	 * @param MqttMessage msg
	 */
	private class CDAPerformanceMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		/**
		 * Default Constructor
		 * @param resource  resource name to be used
		 * @param dataMsgListener the data listener for a topic
		 */
		CDAPerformanceMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		/**
		 * Callback function called when get a message of Systemperformancedata
		 * @param String topic the topic of the arrived message
		 * @param MqttMessage msg the message
		 */
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
	
			try {
				_Logger.info("Payload from CDA:  "+(new String(message.getPayload())));
				SystemPerformanceData perfData = DataUtil.getInstance().jsonToSystemPerformanceData(new String(message.getPayload()));
				perfData.setDevice("CDA");
				//_Logger.info("Sensordata:  "+sensorData.getValue());
				//_Logger.info(DataUtil.getInstance().sensorDataToJson(sensorData));
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleSystemPerformanceMessage(this.resource, perfData);
				}
				else {
					_Logger.info("dml is null");
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to SensorData.");
			}
		}
		
	}
	
	/**
	 * Publish a message to a topic to the cloud
	 * @param topic the topic of the message
	 * @param payload the message
	 * @param qos  the qos level used
	 * @return boolean  the succeed flag
	 */
	protected boolean publishMessage(String topic, byte[] payload, int qos)
	{
		MqttMessage message = new MqttMessage(payload);
		
		if (qos < 0 || qos > 2) {
			qos = 0;
		}
		
		message.setQos(qos);
		
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			_Logger.info("Publishing message to topic: " + topic);
			
			this.mqttClient.publish(topic, message);
			_Logger.info("Succeed to publish to topic: " + topic);
			
			return true;
		} catch (MqttPersistenceException e) {
			_Logger.warning("Persistence exception thrown when publishing to topic: " + topic);
		} catch (MqttException e) {
			_Logger.warning("MQTT exception thrown when publishing to topic: " + topic);
		}
		
		return false;
	}
	/**
	 * Subscribe to a topic in a qos
	 * @param topic the topic of the message
	 * @param qos  the qos level used
	 * @return boolean  the succeed flag
	 */
	protected boolean subscribeToTopic(String topic, int qos)
	{
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			this.mqttClient.subscribe(topic, qos);
			_Logger.info("Succeed to subscribe to topic: " + topic);
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic: " + topic);
		}
		
		return false;
	}
	
	/**
	 * Subscribe to a topic with a callback function in MQTTCALLBACK
	 * @param topic the topic of the message
	 * @param messageListener the listener used here
	 * @param qos  the qos level used
	 * @return boolean  the succeed flag
	 */
	protected boolean subscribeToTopic(String topic, int qos, MqttCallback messageListener)
	{
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			System.out.println("*****************subatMQTT");
			this.mqttClient.subscribe(topic, qos);
			_Logger.info("Succeed to subscribe to topic: " + topic);
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic: " + topic);
		}
		
		return false;
	}
	
	/**
	 * Subscribe to a topic with a callback function in IMqttMessageListener
	 * @param topic the topic of the message
	 * @param qos  the qos level used
	 * @param messageListener The message listener used here
	 * @return boolean  the succeed flag
	 */
	protected boolean subscribeToTopic(String topic, int qos, IMqttMessageListener messageListener)
	{
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			System.out.println("*****************subatMQTT");
			this.mqttClient.subscribe(topic, qos,messageListener);
			_Logger.info("Succeed to subscribe to topic: " + topic);
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic: " + topic);
		}
		
		return false;
	}
	
	/**
	 * unsubscribe a topic from cloud
	 * @param topic the topic of the message
	 * @return boolean  the succeed flag
	 */
	protected boolean unsubscribeFromTopic(String topic)
	{
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			this.mqttClient.unsubscribe(topic);
			_Logger.info("Succeed to ubsubscribe to topic: " + topic);
			
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to unsubscribe from topic: " + topic);
		}
		
		return false;
	}
	
	/**
	 * Config a ssl connection between GDA and Cloud
	 * @param configSectionName
	 */
    private void initCredentialConnectionParameters(String configSectionName)
    {
        // TODO: implement this
        ConfigUtil configUtil = ConfigUtil.getInstance();
       
        try {
            _Logger.info("Configuring credentials...");
           
            Properties props = configUtil.getCredentials(configSectionName);
           
            if(props != null) {
                this.connOpts.setUserName(props.getProperty(ConfigConst.USER_NAME_TOKEN_KEY,""));
                System.out.println(props.getProperty(ConfigConst.USER_NAME_TOKEN_KEY));
                this.connOpts.setPassword(props.getProperty(ConfigConst.USER_AUTH_TOKEN_KEY, "").toCharArray());
                //System.out.println(props.getProperty(ConfigConst.USER_AUTH_TOKEN_KEY));
               
                _Logger.info("Credentials now set.");
               
            }else {
                _Logger.log(Level.WARNING,"No creditials are set.");
            }
        }catch (Exception e) {
            _Logger.log(Level.SEVERE,"Failed to initiate secure MQTT connection. Using insecure connection.", e);
           
            this.enableEncryption = false;
        }
    }
    
 
	
}
