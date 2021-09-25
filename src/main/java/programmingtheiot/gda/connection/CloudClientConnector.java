/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * CloudClientConnector
 *
 */
public class CloudClientConnector implements IPubSubClient, ICloudClient
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CloudClientConnector.class.getName());
	
	// private var's
	private String topicPrefix = "";
	private MqttClientConnector mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	private int qosLevel = 0;
	
	// constructors
	
	/**
	 * Default constructor
	 * get the prefix of the url of the topic in ubidots
	 */
	public CloudClientConnector()
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.topicPrefix =
			configUtil.getProperty(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.BASE_TOPIC_KEY);
		
		// Depending on the cloud service, the topic names may or may not begin with a "/", so this code
		// should be updated according to the cloud service provider's topic naming conventions
		if (topicPrefix == null) {
			topicPrefix = "/";
		} else {
			if (! topicPrefix.endsWith("/")) {
				topicPrefix += "/";
			}
		}
		
	}
	
	// public methods
	/**
	 * connect the mqtt client
	 */
	@Override
	public boolean connectClient()
	{
		if (this.mqttClient == null) {
			this.mqttClient = new MqttClientConnector(true);
			this.mqttClient.setDataMessageListener(this.dataMsgListener);
			//this.mqttClient.setClientID(true);
		}
		
		if(this.mqttClient.isConnected()) {
			return false;
		}
		else {
			this.mqttClient.connectClient();
			return true;
		}
	}

	/**
	 * disconnect the mqtt client
	 */
	@Override
	public boolean disconnectClient()
	{
		if(this.mqttClient!=null) {
			return this.mqttClient.disconnectClient();
		}
		return false;
	}

	/**
	 * Create a mqtt topic for a resource
	 * @param resource the resource used to carry the value
	 * @return String the topic uri
	 */
	private String createTopicName(ResourceNameEnum resource)
	{
		return this.topicPrefix + resource.getDeviceName() + "/" + resource.getResourceType();
	}
	public boolean isConnected()
	{
		return false;
	}
	
	/**
	 * Unused interfaces
	 */
	@Override
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)
	{
		return false;
	}
	/**
	 * Unused interfaces
	 */
	@Override
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos)
	{
		return false;
	}
	/**
	 * Unused interfaces
	 */
	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		return false;
	}

	/**
	 * Setter of the dataMsgListener
	 * @param IDataMessageListener listener  the data listener used to send data
	 * @return boolean setter's succeed flag
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		if(listener != null) {
			this.dataMsgListener = listener;
			return true;	
		}
		return false;
	}
	/**
	 * Publish resource to cloud broker
	 * @param resource The resource to be published
	 * @param itemName a part of topic name
	 * @param payload the message to be published
	 * @return boolean succeed flag
	 */
	private boolean publishMessageToCloud(ResourceNameEnum resource, String itemName, String payload)
	{
		String topicName = createTopicName(resource) + "-" + itemName;
		
		try {
			_Logger.finest("Publishing payload value(s) to Ubidots: " + topicName);
			
			this.mqttClient.publishMessage(topicName, payload.getBytes(), this.qosLevel);
			
			return true;
		} catch (Exception e) {
			_Logger.warning("Failed to publish message to Ubidots: " + topicName);
		}
		
		return false;
	}
	/**
	 * Publish Sensordata to cloud
	 * @param ResourceNameEnum resource the resource name to be sent to cloud
	 * @param SensorData data The data to be sent to cloud
	 * @return boolean  succeed flag
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data)
	{
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().sensorDataToJson(data);
			
			return publishMessageToCloud(resource, data.getName(), payload);
		}
		
		return false;
	}
	/**
	 * Publish Actuatordata to cloud
	 * @param ResourceNameEnum resource the resource name to be sent to cloud
	 * @param ActuatorData data The data to be sent to cloud
	 * @return boolean  succeed flag
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, ActuatorData data)
	{
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().actuatorDataToJson(data);
			
			return publishMessageToCloud(resource, data.getName(), payload);
		}
		
		return false;
	}
	/**
	 * Publish SystemPerformancedata to cloud
	 * @param ResourceNameEnum resource The resource name to be sent to cloud
	 * @param SystemPerformanceData   The data to be sent to cloud
	 * @return boolean  succeed flag
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data)
	{
		if (resource != null && data != null) {
			SensorData cpuData = new SensorData();
			cpuData.setName(ConfigConst.CPU_UTIL_NAME);
			cpuData.setValue(data.getCpuUtilization());
			
			boolean cpuDataSuccess = sendEdgeDataToCloud(resource, cpuData);
			
			if (! cpuDataSuccess) {
				_Logger.warning("Failed to send CPU utilization data to cloud service.");
			}
			
			SensorData memData = new SensorData();
			memData.setName(ConfigConst.MEM_UTIL_NAME);
			memData.setValue(data.getMemoryUtilization());
			
			boolean memDataSuccess = sendEdgeDataToCloud(resource, memData);
			
			if (! memDataSuccess) {
				_Logger.warning("Failed to send memory utilization data to cloud service.");
			}
			
			return (cpuDataSuccess == memDataSuccess);
		}
		
		return false;
	}
	/**
	 * Unsubscribe a topic from cloud broker
	 * @param ResourceNameEnum resource The resource name to be subscribed
	 * @return boolean  succeed flag
	 */
	@Override
	public boolean subscribeToEdgeEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		
		String topicName = null;
		
		System.out.println("*********SUB_CLOUD_ACT");
		
		if (isMqttClientConnected()) {
			topicName = createTopicName(resource);
			
			//this.mqttClient.subscribeToTopic(topicName, 1);
			
			this.mqttClient.subscribeToTopic("/v1.6/devices/ConstrainedDevice/ActuatorCmd-pressureActuator/lv", 0);
			
			success = true;
			if(success == true) {
				System.out.println("*********SUB_CLOUD_ACT_SUCCEED******");
			}
		} else {
			_Logger.warning("Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}
	
	/**
	 * Unsubscribe a topic from the cloud
	 * @param ResourceNameEnum resource The resource name to be subscribed
	 * @return boolean succeed flag
	 */
	@Override
	public boolean unsubscribeFromEdgeEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		
		String topicName = null;
		
		if (isMqttClientConnected()) {
			topicName = createTopicName(resource);
			
			this.mqttClient.unsubscribeFromTopic(topicName);
			
			success = true;
		} else {
			_Logger.warning("Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}
	
	/**
	 * Judge if the client is connected with a broker
	 * @return boolean succeed flag
	 */
	public boolean isMqttClientConnected() {
		if(this.mqttClient.isConnected()) {
			return true;
		}
		return false;
	}
	// private methods
	
}
