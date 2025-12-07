/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License.
 */

package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.connection.MqttClientConnector;

/**
 * Performance test for MQTT client using different QoS levels.
 * Tests publish performance for QoS 0, 1, and 2.
 */
public class MqttClientPerformanceTest
{
    private static final Logger _Logger =
        Logger.getLogger(MqttClientPerformanceTest.class.getName());

    public static final int MAX_TEST_RUNS = 1000;
    private static final String TEST_TOPIC = "test/topic";

    private MqttClientConnector mqttClient = null;
    private AtomicInteger receivedCount = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception
    {
        this.mqttClient = new MqttClientConnector();

        this.mqttClient.setDataMessageListener(new IDataMessageListener() {
            @Override
            public boolean handleIncomingMessage(ResourceNameEnum resource, String msg) {
                _Logger.info("Message received on topic: " + resource.getResourceName() + " | Payload: " + msg);
                receivedCount.incrementAndGet();
                return true;
            }

            @Override
            public boolean handleSensorMessage(ResourceNameEnum resource, SensorData data) {
                return true;
            }

            @Override
            public boolean handleSystemPerformanceMessage(ResourceNameEnum resource, SystemPerformanceData data) {
                return true;
            }

            @Override
            public boolean handleActuatorCommandResponse(ResourceNameEnum resource, ActuatorData data) {
                return true;
            }

            @Override
            public boolean handleActuatorCommandRequest(ResourceNameEnum resource, ActuatorData data) {
                return true;
            }

            @Override
            public void setActuatorDataListener(String name, IActuatorDataListener listener) {
                // Not needed for this test
            }
        });
    }

    @After
    public void tearDown() throws Exception
    {
        if (this.mqttClient != null) {
            this.mqttClient.disconnectClient();
        }
    }

    @Test
    public void testConnectAndDisconnect()
    {
        assertTrue("Connect failed", this.mqttClient.connectClient());
        assertTrue("Disconnect failed", this.mqttClient.disconnectClient());
    }

    @Test
    public void testPublishQoS0()
    {
        execTestPublish(MAX_TEST_RUNS, 0);
    }

    @Test
    public void testPublishQoS1()
    {
        execTestPublish(MAX_TEST_RUNS, 1);
    }

    @Test
    public void testPublishQoS2()
    {
        execTestPublish(MAX_TEST_RUNS, 2);
    }

    private void execTestPublish(int maxTestRuns, int qos)
    {
        assertTrue("Connect failed", this.mqttClient.connectClient());
        assertTrue("Subscribe failed", this.mqttClient.subscribeToTopic(TEST_TOPIC, qos));

        String payload = "{\"name\":\"test\",\"value\":42}";

        for (int sequenceNo = 0; sequenceNo < maxTestRuns; sequenceNo++) {
            boolean pubResult = this.mqttClient.publishMessage(TEST_TOPIC, payload, qos);
            assertTrue("Publish failed at sequence " + sequenceNo, pubResult);
        }

        try {
            Thread.sleep(2000); // allow time for messages to arrive
        } catch (InterruptedException e) {
            // ignore
        }

        assertTrue("Disconnect failed", this.mqttClient.disconnectClient());
        assertTrue("No messages received during publish test", receivedCount.get() > 0);

        _Logger.info("Publish test complete - QoS " + qos + ", received: " + receivedCount.get());
    }
}