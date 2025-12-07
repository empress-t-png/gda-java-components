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
import programmingtheiot.data.SensorData;

/**
 * Resource handler for sensor data (telemetry) updates from CDA to GDA.
 */
public class UpdateTelemetryResourceHandler extends CoapResource
{
	// static

	private static final Logger _Logger =
		Logger.getLogger(UpdateTelemetryResourceHandler.class.getName());
	
	private static int postCount = 0;
	private static int putCount = 0;

	// params

	private IDataMessageListener dataMsgListener = null;

	// constructors

	/**
	 * Constructor.
	 *
	 * @param resourceName The name of the resource.
	 */
	public UpdateTelemetryResourceHandler(String resourceName)
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

		_Logger.info("Handling DELETE request for SensorData...");

		context.respond(ResponseCode.DELETED, "SensorData deleted");
	}

	@Override
	public void handleGET(CoapExchange context)
	{
		context.accept();

		_Logger.info("Handling GET request for SensorData...");

		context.respond(ResponseCode.CONTENT, "SensorData retrieved");
	}

	@Override
	public void handlePOST(CoapExchange context)
	{
		context.accept();
		
		postCount++;
		if (postCount % 10 == 0) {
			_Logger.info("POST request #" + postCount + " received");
		}

		context.respond(ResponseCode.CREATED, "SensorData created");
	}

	@Override
	public void handlePUT(CoapExchange context)
	{
		ResponseCode code = ResponseCode.NOT_ACCEPTABLE;

		context.accept();
		
		putCount++;
		if (putCount % 10 == 0) {
			_Logger.info("PUT request #" + putCount + " received");
		}

		if (this.dataMsgListener != null) {
			try {
				String jsonData = new String(context.getRequestPayload());

				SensorData sensorData =
					DataUtil.getInstance().jsonToSensorData(jsonData);

				this.dataMsgListener.handleSensorMessage(
					ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData);

				code = ResponseCode.CHANGED;

			} catch (Exception e) {
				_Logger.warning(
					"Failed to handle PUT request. Message: " +
						e.getMessage());

				code = ResponseCode.BAD_REQUEST;
			}
		} else {
			code = ResponseCode.CONTINUE;
		}

		String msg =
			"Update sensor data request handled: " + super.getName();

		context.respond(code, msg);
	}
}