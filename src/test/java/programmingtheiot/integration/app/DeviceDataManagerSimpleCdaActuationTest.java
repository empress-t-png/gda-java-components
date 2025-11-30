/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 - 2025 by Andrew D. King
 */

package programmingtheiot.integration.app;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.gda.app.DeviceDataManager;

/**
 * This test case class contains basic integration tests for
 * DeviceDataManager humidity threshold logic.
 */
public class DeviceDataManagerSimpleCdaActuationTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManagerSimpleCdaActuationTest.class.getName());
	
	// member var's
	
	private DeviceDataManager devDataMgr = null;
	
	// test setup methods
	
	@Before
	public void setUp() throws Exception
	{
		this.devDataMgr = new DeviceDataManager();
	}
	
	@After
	public void tearDown() throws Exception
	{
		if (this.devDataMgr != null) {
			this.devDataMgr.stopManager();
		}
	}
	
	// test methods
	
	/**
	 * Test method for running the DeviceDataManager humidity analysis.
	 * 
	 */
	@Test
	public void testSendActuationEventsToCda()
	{
		// Start the DeviceDataManager
		this.devDataMgr.startManager();
		
		ConfigUtil cfgUtil = ConfigUtil.getInstance();
		
		float nominalVal = cfgUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "nominalHumiditySetting");
		float lowVal     = cfgUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "triggerHumidifierFloor");
		float highVal    = cfgUtil.getFloat(ConfigConst.GATEWAY_DEVICE, "triggerHumidifierCeiling");
		int   delay      = cfgUtil.getInteger(ConfigConst.GATEWAY_DEVICE, "humidityMaxTimePastThreshold");
		
		_Logger.info("==========");
		_Logger.info("Test Configuration:");
		_Logger.info("  Nominal humidity: " + nominalVal);
		_Logger.info("  Low threshold: " + lowVal);
		_Logger.info("  High threshold: " + highVal);
		_Logger.info("  Time delay: " + delay + " seconds");
		_Logger.info("==========");
		
		// Test Sequence No. 1 - Send nominal values (should not trigger actuation)
		_Logger.info("\n========== Test Sequence 1: Nominal Values ==========");
		generateAndProcessHumiditySensorDataSequence(
			this.devDataMgr, nominalVal, lowVal, highVal, delay);
		
		_Logger.info("\n========== Test Complete ==========");
	}
	
	/**
	 * Helper method to generate and process a sequence of humidity sensor data.
	 */
	private void generateAndProcessHumiditySensorDataSequence(
		DeviceDataManager ddm, float nominalVal, float lowVal, float highVal, int delay)
	{
		SensorData sd = new SensorData();
		sd.setName("Test Humidity Sensor");
		sd.setLocationID("constraineddevice001");
		sd.setTypeID(ConfigConst.HUMIDITY_SENSOR_TYPE);
		
		// Send nominal values (no actuation expected)
		_Logger.info("\n--- Sending nominal humidity values ---");
		sd.setValue(nominalVal);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(2);
		
		sd.setValue(nominalVal);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(2);
		
		// Send low values (actuation ON expected after delay)
		_Logger.info("\n--- Sending LOW humidity values (below floor threshold) ---");
		sd.setValue(lowVal - 2);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		_Logger.info("Waiting " + (delay + 1) + " seconds for threshold timer...");
		waitForSeconds(delay + 1);
		
		sd.setValue(lowVal - 1);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		_Logger.info("Waiting " + (delay + 1) + " seconds for threshold timer...");
		waitForSeconds(delay + 1);
		
		// Send values back to normal (actuation OFF expected)
		_Logger.info("\n--- Sending humidity values back to nominal range ---");
		sd.setValue(lowVal + 1);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(delay + 1);
		
		sd.setValue(nominalVal);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(delay + 1);
		
		_Logger.info("\n--- Humidity sequence complete ---");
	}
	
	/**
	 * Helper method to wait for a specified number of seconds.
	 */
	private void waitForSeconds(int seconds)
	{
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}