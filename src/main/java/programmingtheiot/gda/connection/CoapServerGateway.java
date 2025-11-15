/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */
package programmingtheiot.gda.connection;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.UdpConfig;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.handlers.GenericCoapResourceHandler;
import programmingtheiot.gda.connection.handlers.GetActuatorCommandResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateSystemPerformanceResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateTelemetryResourceHandler;

/**
 * CoAP Server Gateway implementation.
 * 
 */
public class CoapServerGateway
{
	// static initialization for Californium 3.x
	static {
		CoapConfig.register();
		UdpConfig.register();
	}
	
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CoapServerGateway.class.getName());
	
	// params
	
	private CoapServer coapServer = null;
	
	private IDataMessageListener dataMsgListener = null;
	
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param dataMsgListener
	 */
	public CoapServerGateway(IDataMessageListener dataMsgListener)
	{
		super();
		
		this.dataMsgListener = dataMsgListener;
		
		initServer();
	}
		
	// public methods
	
	public void addResource(ResourceNameEnum resourceType, Resource resource)
	{
		if (resourceType != null && resource != null) {
			createAndAddResourceChain(resourceType, resource);
		}
	}
	
	public boolean hasResource(String name)
	{
		return false;
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}
	
	public boolean startServer()
	{
		try {
			if (this.coapServer != null) {
				this.coapServer.start();
				
				// Add message tracer for logging
				for (Endpoint ep : this.coapServer.getEndpoints()) {
					ep.addInterceptor(new MessageTracer());
				}
				
				_Logger.info("CoAP server started successfully.");
				return true;
			} else {
				_Logger.warning("CoAP server START failed. Not yet initialized.");
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to start CoAP server.", e);
		}
		
		return false;
	}
	
	public boolean stopServer()
	{
		try {
			if (this.coapServer != null) {
				this.coapServer.stop();
				
				_Logger.info("CoAP server stopped successfully.");
				return true;
			} else {
				_Logger.warning("CoAP server STOP failed. Not yet initialized.");
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to stop CoAP server.", e);
		}
		
		return false;
	}
	
	
	// private methods
	
	private void createAndAddResourceChain(ResourceNameEnum resourceType, Resource resource)
	{
		_Logger.info("Adding server resource handler chain: " + resourceType.getResourceName());
		
		List<String> resourceNames = resourceType.getResourceNameChain();
		Queue<String> queue = new ArrayBlockingQueue<>(resourceNames.size());
		
		queue.addAll(resourceNames);
		
		// Check if we have a parent resource
		Resource parentResource = this.coapServer.getRoot();
		
		// If no parent resource, add it in now (should be named "PIOT")
		if (parentResource == null) {
			parentResource = new CoapResource(queue.poll());
			this.coapServer.add(parentResource);
		}
		
		while (!queue.isEmpty()) {
			// Get the next resource name
			String resourceName = queue.poll();
			Resource nextResource = parentResource.getChild(resourceName);
			
			if (nextResource == null) {
				if (queue.isEmpty()) {
					// This is the leaf resource - use the provided resource handler
					nextResource = resource;
					nextResource.setName(resourceName);
				} else {
					// This is an intermediate resource - create a generic CoapResource
					nextResource = new CoapResource(resourceName);
				}
				
				parentResource.add(nextResource);
			}
			
			parentResource = nextResource;
		}
	}
	
	private void initServer(ResourceNameEnum ...resources)
	{
		// Create the CoAP server instance
		this.coapServer = new CoapServer();
		
		// Initialize default resources
		initDefaultResources();
		
		_Logger.info("CoAP server initialized on port 5683 with resource handlers.");
	}
	
	private void initDefaultResources()
	{
		// Create GetActuatorCommandResourceHandler
		GetActuatorCommandResourceHandler getActuatorCmdResourceHandler =
			new GetActuatorCommandResourceHandler(
				ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.getResourceType());
		
		if (this.dataMsgListener != null) {
			this.dataMsgListener.setActuatorDataListener(null, getActuatorCmdResourceHandler);
		}
		
		addResource(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, getActuatorCmdResourceHandler);
		
		// Create UpdateTelemetryResourceHandler
		UpdateTelemetryResourceHandler updateTelemetryResourceHandler =
			new UpdateTelemetryResourceHandler(
				ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceType());
		
		updateTelemetryResourceHandler.setDataMessageListener(this.dataMsgListener);
		
		addResource(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, updateTelemetryResourceHandler);
		
		// Create UpdateSystemPerformanceResourceHandler
		UpdateSystemPerformanceResourceHandler updateSystemPerformanceResourceHandler =
			new UpdateSystemPerformanceResourceHandler(
				ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceType());
		
		updateSystemPerformanceResourceHandler.setDataMessageListener(this.dataMsgListener);
		
		addResource(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, updateSystemPerformanceResourceHandler);
	}
}