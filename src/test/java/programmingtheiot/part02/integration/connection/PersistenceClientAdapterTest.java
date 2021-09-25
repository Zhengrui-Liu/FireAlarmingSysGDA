/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part02.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import programmingtheiot.gda.connection.RedisPersistenceAdapter;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * This test case class contains very basic integration tests for
 * RedisPersistenceAdapter. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class PersistenceClientAdapterTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(PersistenceClientAdapterTest.class.getName());
	
	
	// member var's
	
	private RedisPersistenceAdapter rpa = new RedisPersistenceAdapter();
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
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
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter.storeData}.
	 */
	@Test
	public void testStoreSensor()
	{
		SensorData sd = new SensorData();
		assertTrue(this.rpa.storeData(sd, "sensordata.csv"));
	}
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter.storeData}.
	 */
	@Test
	public void testStoreActuator()
	{
		ActuatorData sd = new ActuatorData();
		assertTrue(this.rpa.storeData(sd, "actuatordata.csv"));
	}
	/**
	 * Test method for {@link programmingtheiot.gda.connection.RedisPersistenceAdapter.storeData}.
	 */
	@Test
	public void testStoreSysPerf()
	{
		SystemPerformanceData sd = new SystemPerformanceData();
		assertTrue(this.rpa.storeData(sd, "systemperformancedataCDA.csv",1));
		assertTrue(this.rpa.storeData(sd, "systemperformancedataGDA.csv",2));
	}
	
	
	
	
}
