/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 */
package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;

/**
 * CoAP client connector implementation using Californium library.
 */
public class CoapClientConnector implements IRequestResponseClient
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CoapClientConnector.class.getName());
	
	// params
	private String protocol;
	private String host;
	private int port;
	private String serverAddr;
	private IDataMessageListener dataMsgListener;
	
	// constructors
	
	/**
	 * Default.
	 * 
	 * All config data will be loaded from the config file.
	 */
	public CoapClientConnector()
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.host = configUtil.getProperty(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		this.port = configUtil.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_COAP_PORT);
		
		this.protocol = "coap";
		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;
		
		// Create Californium3.properties file to avoid configuration errors
		System.setProperty("COAP_DISABLE_CONFIG_FILE", "true");
		
		_Logger.info("CoAP client configured for server: " + this.serverAddr);
	}
		
	/**
	 * Constructor.
	 * 
	 * @param host
	 * @param isSecure
	 * @param enableConfirmedMsgs
	 */
	public CoapClientConnector(String host, boolean isSecure, boolean enableConfirmedMsgs)
	{
		this.host = host;
		this.port = ConfigConst.DEFAULT_COAP_PORT;
		
		if (isSecure) {
			this.protocol = "coaps";
			this.port = ConfigConst.DEFAULT_COAP_SECURE_PORT;
		} else {
			this.protocol = "coap";
		}
		
		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;
		
		System.setProperty("COAP_DISABLE_CONFIG_FILE", "true");
		
		_Logger.info("CoAP client configured for server: " + this.serverAddr);
	}
	
	// public methods
	
	@Override
	public boolean sendDiscoveryRequest(int timeout)
	{
		return false;
	}
	
	@Override
	public boolean sendDeleteRequest(ResourceNameEnum resource, String name, boolean enableCON, int timeout)
	{
		return false;
	}
	
	@Override
	public boolean sendGetRequest(ResourceNameEnum resource, String name, boolean enableCON, int timeout)
	{
		return false;
	}
	
	@Override
	public boolean sendPostRequest(ResourceNameEnum resource, String name, boolean enableCON, String payload, int timeout)
	{
		if (resource == null) {
			_Logger.warning("Resource is null. Ignoring POST request.");
			return false;
		}
		
		String uriPath = createUriPath(resource, name);
		
		CoapClient clientConn = null;
		
		try {
			clientConn = new CoapClient(uriPath);
			
			CoapResponse response = null;
			
			if (enableCON) {
				clientConn.useCONs();
				response = clientConn.post(payload, MediaTypeRegistry.APPLICATION_JSON);
			} else {
				clientConn.useNONs();
				response = clientConn.post(payload, MediaTypeRegistry.APPLICATION_JSON);
			}
			
			if (response != null) {
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			return false;
		} finally {
			if (clientConn != null) {
				clientConn.shutdown();
			}
		}
	}
	
	@Override
	public boolean sendPutRequest(ResourceNameEnum resource, String name, boolean enableCON, String payload, int timeout)
	{
		if (resource == null) {
			_Logger.warning("Resource is null. Ignoring PUT request.");
			return false;
		}
		
		String uriPath = createUriPath(resource, name);
		
		CoapClient clientConn = null;
		
		try {
			clientConn = new CoapClient(uriPath);
			
			CoapResponse response = null;
			
			if (enableCON) {
				clientConn.useCONs();
				response = clientConn.put(payload, MediaTypeRegistry.APPLICATION_JSON);
			} else {
				clientConn.useNONs();
				response = clientConn.put(payload, MediaTypeRegistry.APPLICATION_JSON);
			}
			
			if (response != null) {
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			return false;
		} finally {
			if (clientConn != null) {
				clientConn.shutdown();
			}
		}
	}
	
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		return false;
	}
	
	public void clearEndpointPath()
	{
	}
	
	public void setEndpointPath(ResourceNameEnum resource)
	{
	}
	
	@Override
	public boolean startObserver(ResourceNameEnum resource, String name, int ttl)
	{
		return false;
	}
	
	@Override
	public boolean stopObserver(ResourceNameEnum resourceType, String name, int timeout)
	{
		return false;
	}
	
	// private methods
	
	private String createUriPath(ResourceNameEnum resource, String name)
	{
		StringBuilder uriPath = new StringBuilder(this.serverAddr);
		
		if (resource != null) {
			uriPath.append("/").append(resource.getResourceName());
			
			if (name != null && name.trim().length() > 0) {
				uriPath.append("/").append(name);
			}
		}
		
		return uriPath.toString();
	}
}