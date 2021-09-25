/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part04.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * CloudClientConnector. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class CloudClientConnectorTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CloudClientConnectorTest.class.getName());
	
	
	// member var's
	
	private CloudClientConnector cloudClient = null;
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		ConfigUtil.getInstance();
		this.cloudClient = new CloudClientConnector();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	// test methods
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.CloudClientConnector#connectClient()}.
	 */
	//@Test
	public void testConnectAndDisconnect()
	{
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		assertTrue(this.cloudClient.connectClient());
		assertFalse(this.cloudClient.connectClient());
		
		try {
			Thread.sleep(delay * 1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
		assertFalse(this.cloudClient.disconnectClient());
	}
	
	/**
	 * Test method for {@link programmingtheiot.gda.connection.CloudClientConnector#publishMessage(programmingtheiot.common.ResourceNameEnum, java.lang.String, int)}.
	 */
	//@Test
	public void testPublishAndSubscribe()
	{
		int qos = 0;
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		IDataMessageListener listener = new DefaultDataMessageListener();
		
		assertTrue(this.cloudClient.connectClient());
		assertTrue(this.cloudClient.subscribeToEdgeEvents(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		
		SensorData sensorData = new SensorData();
		sensorData.setName(ConfigConst.TEMP_SENSOR_NAME);
		sensorData.setValue(21.4f);
		
		assertTrue(this.cloudClient.sendEdgeDataToCloud(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData));
		
		SystemPerformanceData sysPerfData = new SystemPerformanceData();
		
		assertTrue(this.cloudClient.sendEdgeDataToCloud(ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE, sysPerfData));
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.unsubscribeFromEdgeEvents(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));

		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}

		try {
			Thread.sleep(delay * 1000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
	}
	/*
	 * test subscript a variable form cloud and send it to gda then pass it to cda
	 */
	@Test
	public void testSubscribeCloud() {
		int qos = 0;
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		IDataMessageListener listener = new DefaultDataMessageListener();
		
		assertTrue(this.cloudClient.connectClient());
		assertTrue(this.cloudClient.subscribeToEdgeEvents(ResourceNameEnum.CLOUD_ACTUATION_RESOURCE));
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		
		SensorData sensorData0 = new SensorData();
		sensorData0.setName("pressuresensor");
		sensorData0.setValue(800);
		sensorData0.setSensorType(SensorData.PRESSURE_SENSOR_TYPE);
		SensorData sensorData1 = new SensorData();
		sensorData1.setName("pressuresensor");
		sensorData1.setSensorType(SensorData.PRESSURE_SENSOR_TYPE);
		sensorData1.setValue(400);
		
		assertTrue(this.cloudClient.sendEdgeDataToCloud(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData0));
		try {
			Thread.sleep(50000);
		} catch (Exception e) {
			// ignore
		}
		assertTrue(this.cloudClient.sendEdgeDataToCloud(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData1));
		

		try {
			Thread.sleep(delay * 1000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.cloudClient.disconnectClient());
	}
	
	
}