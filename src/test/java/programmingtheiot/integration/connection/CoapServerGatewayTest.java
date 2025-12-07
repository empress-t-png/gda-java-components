package programmingtheiot.integration.connection;

import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.WebLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.system.DefaultDataMessageListener;

/**
 * Integration test for CoapServerGateway.
 * Tests server startup, resource discovery, and resource access.
 */
public class CoapServerGatewayTest {
    // static
    private static final Logger _Logger = 
        Logger.getLogger(CoapServerGatewayTest.class.getName());
    
    // params
    private CoapServerGateway csg = null;
    
    // setup and teardown
    
    @Before
    public void setUp() throws Exception {
        _Logger.info("Setting up CoapServerGatewayTest...");
        
        // Create server with default data message listener
        this.csg = new CoapServerGateway(new DefaultDataMessageListener());
        this.csg.startServer();
        
        // Give server time to initialize
        Thread.sleep(3000);
        
        _Logger.info("CoAP server started for testing.");
    }
    
    @After
    public void tearDown() throws Exception {
        _Logger.info("Tearing down CoapServerGatewayTest...");
        
        if (this.csg != null) {
            this.csg.stopServer();
            this.csg = null;
        }
        
        _Logger.info("CoAP server stopped.");
    }
    
    // test methods
    
    /**
     * Test simple CoAP server gateway integration.
     * Performs resource discovery and tests individual resource access.
     */
    @Test
    public void testRunSimpleCoapServerGatewayIntegration() {
        try {
            _Logger.info("Starting testRunSimpleCoapServerGatewayIntegration...");
            
            // Build server URL using config constants
            String url = ConfigConst.DEFAULT_COAP_PROTOCOL + "://" + 
                        ConfigConst.DEFAULT_HOST + ":" + 
                        ConfigConst.DEFAULT_COAP_PORT;
            
            _Logger.info("Testing CoAP server at: " + url);
            
            // Create CoAP client
            CoapClient clientConn = new CoapClient(url);
            
            // Perform resource discovery
            _Logger.info("Performing resource discovery...");
            Set<WebLink> wlSet = clientConn.discover();
            
            if (wlSet != null && !wlSet.isEmpty()) {
                _Logger.info("Discovered " + wlSet.size() + " resources:");
                for (WebLink wl : wlSet) {
                    _Logger.info(" --> WebLink: " + wl.getURI() + 
                        ". Attributes: " + wl.getAttributes());
                }
            } else {
                _Logger.warning("No resources discovered.");
            }
            
            // Test individual resource access
            _Logger.info("\nTesting individual resource access...");
            
            // Test CDA System Performance Message resource
            _Logger.info("Testing: " + ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
            clientConn.setURI(url + "/" + ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
            clientConn.get();
            
            // Test CDA Sensor Message resource
            _Logger.info("Testing: " + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
            clientConn.setURI(url + "/" + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
            clientConn.get();
            
            // Test CDA Actuator Command resource
            _Logger.info("Testing: " + ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.getResourceName());
            clientConn.setURI(url + "/" + ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.getResourceName());
            clientConn.get();
            
            // Allow time for interaction - EXTENDED TO 60 SECONDS
            _Logger.info("\nWaiting for asynchronous responses...");
            Thread.sleep(60000);  // 60 seconds - plenty of time to run CDA test!
            
            _Logger.info("testRunSimpleCoapServerGatewayIntegration completed successfully.");
            
        } catch (InterruptedException e) {
            _Logger.warning("Test interrupted: " + e.getMessage());
        } catch (Exception e) {
            _Logger.severe("Exception during CoAP server test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test server startup and basic discovery.
     * Quick verification of server lifecycle and resource registration.
     */
    @Test
    public void testServerDiscovery() {
        _Logger.info("Starting testServerDiscovery...");
        
        try {
            String url = ConfigConst.DEFAULT_COAP_PROTOCOL + "://" + 
                        ConfigConst.DEFAULT_HOST + ":" + 
                        ConfigConst.DEFAULT_COAP_PORT;
            
            CoapClient clientConn = new CoapClient(url);
            
            // Perform discovery
            Set<WebLink> wlSet = clientConn.discover();
            
            if (wlSet != null) {
                _Logger.info("Discovery successful. Found " + wlSet.size() + " resources.");
                
                // Verify expected resources are present
                boolean foundSensorMsg = false;
                boolean foundSysPerfMsg = false;
                boolean foundActuatorCmd = false;
                
                for (WebLink wl : wlSet) {
                    String uri = wl.getURI();
                    if (uri.contains("SensorMsg")) {
                        foundSensorMsg = true;
                    }
                    if (uri.contains("SystemPerfMsg")) {
                        foundSysPerfMsg = true;
                    }
                    if (uri.contains("ActuatorCmd")) {
                        foundActuatorCmd = true;
                    }
                }
                
                _Logger.info("Resource verification:");
                _Logger.info("  SensorMsg found: " + foundSensorMsg);
                _Logger.info("  SystemPerfMsg found: " + foundSysPerfMsg);
                _Logger.info("  ActuatorCmd found: " + foundActuatorCmd);
            } else {
                _Logger.warning("Discovery returned null.");
            }
            
        } catch (Exception e) {
            _Logger.severe("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        _Logger.info("testServerDiscovery completed.");
    }
}