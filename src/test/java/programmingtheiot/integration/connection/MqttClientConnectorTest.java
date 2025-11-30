/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 - 2025 by Andrew D. King
 */ 

package programmingtheiot.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * MqttClientConnector. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 * 
 * IMPORTANT NOTE: This test has been updated to work with
 * MqttAsyncClient, which requires delays after connect/disconnect
 * operations to allow async operations to complete.
 * 
 */
public class MqttClientConnectorTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(MqttClientConnectorTest.class.getName());
	
	
	// member var's
	
	private MqttClientConnector mqttClient = null;
	
	
	// test setup methods
	
	@Before
	public void setUp() throws Exception
	{
		this.mqttClient = new MqttClientConnector();
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	// test methods
	
	@Test
	public void testConnectAndDisconnect()
	{
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		assertTrue(this.mqttClient.connectClient());
		
		// IMPORTANT: Add delay for async client to complete connection
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}
		
		assertFalse(this.mqttClient.connectClient());
		
		try {
			Thread.sleep(delay * 1000 + 5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.mqttClient.disconnectClient());
		
		// IMPORTANT: Add delay for async client to complete disconnection
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}
		
		assertFalse(this.mqttClient.disconnectClient());
	}
	
	@Test
	public void testPublishAndSubscribe()
	{
		int qos = 0;
		int delay = ConfigUtil.getInstance().getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		
		assertTrue(this.mqttClient.connectClient());
		
		// IMPORTANT: Add delay for async client to complete connection
		// This allows connectComplete() callback to execute and subscriptions to be made
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, qos));
		assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos));
		assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos));
		assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos));
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "TEST: This is the GDA message payload 1.", qos));
		assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "TEST: This is the GDA message payload 2.", qos));
		assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "TEST: This is the GDA message payload 3.", qos));
		
		try {
			Thread.sleep(25000);
		} catch (Exception e) {
			// ignore
		}
		
		assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));
		assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE));
		assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE));
		assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE));

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
		
		assertTrue(this.mqttClient.disconnectClient());
		
		// IMPORTANT: Add delay for async client to complete disconnection
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}
	}
	
}