/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;


import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.handlers.GenericCoapResourceHandler;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class CoapServerGateway
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CoapServerGateway.class.getName());
	
	// params
	private CoapServer coapServer = null;
	private IDataMessageListener dataMsgListener = null;
	
	// constructors
	
	/**
	 * Default constructor.
	 * 
	 */
	public CoapServerGateway()
	{
		//this((ResourceNameEnum[]) null);
		this(ResourceNameEnum.values());
	}

	/**
	 * Constructor with a param.
	 * 
	 * @param useDefaultResources flag to determine if a default resource is used
	 */
	public CoapServerGateway(boolean useDefaultResources)
	{
		this(useDefaultResources ? ResourceNameEnum.values() : (ResourceNameEnum[]) null);
	}

	/**
	 * Constructor with a param.
	 * 
	 * @param resources the resource to be used
	 */
	public CoapServerGateway(
		ResourceNameEnum ...resources)
	{
		super();
		this.initServer(resources);
	}

	
	// public methods
	/**
	 * call method to create a linked list of resources
	 * @param resource the resource to be used
	 */
	public void addResource(ResourceNameEnum resource)
	{
		if (resource != null) {
			// break out the hierarchy of names and build the resource
			// handler generation(s) as needed, checking if any parent already
			// exists - and if so, add to the existing resource
			_Logger.info("Adding server resource handler chain: " + resource.getResourceName());
			
			createAndAddResourceChain(resource);
		}
	}
	
	public boolean hasResource(String name)
	{
		return false;
	}
	
	/**
	 * Setter of the data listener
	 * @param listener The target listener
	 */
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;
	}
	/**
	 * start the coap server
	 * @return succeed flag
	 */
	public boolean startServer()
	{
		try {
			this.coapServer.start();
			return true;
		}catch(Exception e){
			_Logger.warning("Not started!");
		}
		return false;
	}
	/**
	 * stop the coap server
	 * @return succeed flag
	 */
	public boolean stopServer()
	{
		try {
			this.coapServer.stop();
			return true;
		}catch(Exception e){
			_Logger.warning("Not started!");
		}
		return false;
	}
	
	
	// private methods
	/**
	 * Unused
	 * @param resource
	 * @return
	 */
	private Resource createResourceChain(ResourceNameEnum resource)
	{
		return null;
	}
	
	/**
	 * create a chain and add the head element to the server
	 * @param resource the reource to be added to hte chain
	 */
	private void createAndAddResourceChain(ResourceNameEnum resource)
	{
		List<String> resourceNames = resource.getResourceNameChain();
		Queue<String> queue = new ArrayBlockingQueue<>(resourceNames.size());
		
		queue.addAll(resourceNames);
		
		// check if we have a parent resource
		Resource parentResource = this.coapServer.getRoot();
		
		// if no parent resource, add it in now (should be named "PIOT")
		if (parentResource == null) {
			parentResource = new GenericCoapResourceHandler(queue.poll());
			//this.coapServer.add(parentResource);
		}
		
		while (! queue.isEmpty()) {
			// get the next resource name
			String   resourceName = queue.poll();
			Resource nextResource = parentResource.getChild(resourceName);
			
			if (nextResource == null) {
				// TODO: if this is the last entry, use a custom resource handler implementation that
				// is specific to the resource's implementation needs (e.g. SensorData, ActuatorData, etc.)
				nextResource = new GenericCoapResourceHandler(resourceName);
				parentResource.add(nextResource);
			}
			parentResource = nextResource;
		}
	}
	
	
	/**
	 * instanciate the server and add resource to it
	 * @param resources the resources to be used
	 */
	private void initServer(ResourceNameEnum ...resources)
	{
		this.coapServer = new CoapServer();
		
		for (ResourceNameEnum rn : resources) {
			addResource(rn);
			System.out.println(rn.toString());
		}
	}
}
