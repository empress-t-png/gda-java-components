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
package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;

/**
 * Resource handler for actuator commands - supports OBSERVE pattern.
 */
public class GetActuatorCommandResourceHandler extends CoapResource
	implements IActuatorDataListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GetActuatorCommandResourceHandler.class.getName());
	
	// params
	
	private ActuatorData actuatorData = null;
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName The name of the resource.
	 */
	public GetActuatorCommandResourceHandler(String resourceName)
	{
		super(resourceName);
		
		// Initialize with default ActuatorData
		this.actuatorData = new ActuatorData();
		
		// Set the resource to be observable
		super.setObservable(true);
		
		_Logger.info("Resource handler created with name: " + resourceName);
	}
	
	// public methods
	
	@Override
	public boolean onActuatorDataUpdate(ActuatorData data)
	{
		if (data != null && this.actuatorData != null) {
			this.actuatorData.updateData(data);
			
			// Notify all connected clients
			super.changed();
			
			_Logger.fine("Actuator data updated for URI: " + super.getURI() + 
				": Data value = " + this.actuatorData.getValue());
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void handleGET(CoapExchange context)
	{
		// Accept the request
		context.accept();
		
		_Logger.info("Handling GET request for ActuatorData...");
		
		// Convert the locally stored ActuatorData to JSON
		String jsonData = 
			DataUtil.getInstance().actuatorDataToJson(this.actuatorData);
		
		// Send response with JSON data
		context.respond(ResponseCode.CONTENT, jsonData);
	}
	
	@Override
	public void handlePUT(CoapExchange context)
	{
		context.accept();
		
		_Logger.info("Handling PUT request for ActuatorData...");
		
		context.respond(ResponseCode.CHANGED, "ActuatorData updated");
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{
		context.accept();
		
		_Logger.info("Handling POST request for ActuatorData...");
		
		context.respond(ResponseCode.CREATED, "ActuatorData created");
	}
	
	@Override
	public void handleDELETE(CoapExchange context)
	{
		context.accept();
		
		_Logger.info("Handling DELETE request for ActuatorData...");
		
		context.respond(ResponseCode.DELETED, "ActuatorData deleted");
	}
}