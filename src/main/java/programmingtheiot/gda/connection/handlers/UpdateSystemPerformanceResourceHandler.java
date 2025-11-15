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

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Resource handler for system performance data updates from CDA to GDA.
 */
public class UpdateSystemPerformanceResourceHandler extends CoapResource
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(UpdateSystemPerformanceResourceHandler.class.getName());
	
	// params
	
	private IDataMessageListener dataMsgListener = null;
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName The name of the resource.
	 */
	public UpdateSystemPerformanceResourceHandler(String resourceName)
	{
		super(resourceName);
	}
	
	// public methods
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}
	
	@Override
	public void handleDELETE(CoapExchange context)
	{
		context.accept();
		
		_Logger.info("Handling DELETE request for SystemPerformanceData...");
		
		context.respond(ResponseCode.DELETED, "SystemPerformanceData deleted");
	}
	
	@Override
	public void handleGET(CoapExchange context)
	{
		context.accept();
		
		_Logger.info("Handling GET request for SystemPerformanceData...");
		
		context.respond(ResponseCode.CONTENT, "SystemPerformanceData retrieved");
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{
		context.accept();
		
		_Logger.info("Handling POST request for SystemPerformanceData...");
		
		context.respond(ResponseCode.CREATED, "SystemPerformanceData created");
	}
	
	@Override
	public void handlePUT(CoapExchange context)
	{
		ResponseCode code = ResponseCode.NOT_ACCEPTABLE;
		
		context.accept();
		
		if (this.dataMsgListener != null) {
			try {
				String jsonData = new String(context.getRequestPayload());
				
				SystemPerformanceData sysPerfData =
					DataUtil.getInstance().jsonToSystemPerformanceData(jsonData);
				
				this.dataMsgListener.handleSystemPerformanceMessage(
					ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, sysPerfData);
				
				code = ResponseCode.CHANGED;
				
				_Logger.info("Handling PUT request for SystemPerformanceData: " + sysPerfData.getName());
			} catch (Exception e) {
				_Logger.warning(
					"Failed to handle PUT request. Message: " +
						e.getMessage());
				
				code = ResponseCode.BAD_REQUEST;
			}
		} else {
			_Logger.info(
				"No callback listener for request. Ignoring PUT.");
			
			code = ResponseCode.CONTINUE;
		}
		
		String msg =
			"Update system perf data request handled: " + super.getName();
		
		context.respond(code, msg);
	}
}