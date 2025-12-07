/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 */

package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.gda.connection.*;

/**
 * Performance test for CoAP client using CON and NON messages.
 * Tests POST performance for both message types.
 */
public class CoapClientPerformanceTest
{
	// static

	private static final Logger _Logger =
		Logger.getLogger(CoapClientPerformanceTest.class.getName());

	// NOTE: We'll use only 10,000 requests for CoAP
	public static final int MAX_TEST_RUNS = 10000;

	private static final int DEFAULT_TIMEOUT = 5;

	// member var's

	private CoapClientConnector coapClient = null;


	// test setup methods

	@Before
	public void setUp() throws Exception
	{
		this.coapClient = new CoapClientConnector();
	}

	@After
	public void tearDown() throws Exception
	{
		// Cleanup if needed
	}


	// test methods

	@Test
	public void testPostRequestCon()
	{
		_Logger.info("Testing POST - CON");
		execTestPost(MAX_TEST_RUNS, true);
	}

	@Test
	public void testPostRequestNon()
	{
		_Logger.info("Testing POST - NON");
		execTestPost(MAX_TEST_RUNS, false);
	}


	// private methods

	private void execTestPost(int maxTestRuns, boolean enableCON)
	{
		SensorData sd = new SensorData();
		String payload = DataUtil.getInstance().sensorDataToJson(sd);

		long startMillis = System.currentTimeMillis();

		for (int seqNo = 0; seqNo < maxTestRuns; seqNo++) {
		    this.coapClient.sendPostRequest(
		        ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE,
		        payload, enableCON,
		        payload,
		        DEFAULT_TIMEOUT
		    );
		}

		long endMillis = System.currentTimeMillis();
		long elapsedMillis = endMillis - startMillis;

		_Logger.info("POST message - useCON " + enableCON + " [" + maxTestRuns + "]: " + elapsedMillis + " ms");
	}
}