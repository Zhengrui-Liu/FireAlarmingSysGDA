/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.OptionSet;


/**
 * Shell representation of class for student implementation.
 *
 */
public class GenericCoapResourceHandler extends CoapResource
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GenericCoapResourceHandler.class.getName());
	
	// params
	private IDataMessageListener dataMsgListener = null;
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resource Basically, the path (or topic)
	 */
	public GenericCoapResourceHandler(ResourceNameEnum resource)
	{
		this(resource.getResourceName());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName The name of the resource.
	 */
	
	public GenericCoapResourceHandler(String resourceName)
	{
		super(resourceName);
	}
	
	// public methods
	/**
	 * handle a DELETE request from client
	 * @param context the content to be handled
	 */
	@Override
	public void handleDELETE(CoapExchange context)
	{
		_Logger.info("handleDELETE   "+ context.toString());
		context.accept();
		String msg = "DELETEANS";
		context.respond(ResponseCode.DELETED, msg);
	}
	/**
	 * handle a GET request from client
	 * @param context the content to be handled
	 */
	@Override
	public void handleGET(CoapExchange context)
	{
		_Logger.info("handleGET   "+ context.toString());
		context.accept();
		String msg = "GETANS";
		context.respond(ResponseCode.VALID, msg);
	}
	/**
	 * handle a POST request from client
	 * @param context the content to be handled
	 */
	@Override
	public void handlePOST(CoapExchange context)
	{
		_Logger.info("handlePOST   " + context.toString());
		context.accept();
		String payload = context.getRequestText();
		String msg = "POSTANS";
		context.respond(ResponseCode.CREATED, msg);
	}
	/**
	 * handle a PUT request from client
	 * @param context the content to be handled
	 */
	@Override
	public void handlePUT(CoapExchange context)
	{
		_Logger.info("handlePUT   " + context.toString());
		context.accept();
		String msg = "PUTANS";
	    context.respond(ResponseCode.CHANGED, msg);
	}
	
	/**
	 * Setter of the data listener
	 * @param listener listener to be used
	 */
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;
	}
	
	
}
