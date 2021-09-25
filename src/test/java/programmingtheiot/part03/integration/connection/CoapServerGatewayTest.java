package programmingtheiot.part03.integration.connection;

import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.WebLink;
import org.junit.Test;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.gda.connection.CoapClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;

public class CoapServerGatewayTest {
	
	public static final int DEFAULT_TIMEOUT = 90000;
	public static final boolean USE_DEFAULT_RESOURCES = true;
	
	private static final Logger _Logger =
		Logger.getLogger(CoapClientToServerConnectorTest.class.getName());
	
	private static CoapServerGateway csg = null;
	
	/**
	 * Test create a coap server and test with discover req
	 */
	@Test
	public void testRunSimpleCoapServerGatewayIntegration()
	{
		try {
			String url = "coap://localhost:5683";
			
			this.csg = new CoapServerGateway(); // assumes the no-arg constructor will create all resources internally
			this.csg.startServer();
			
			CoapClient clientConn = new CoapClient(url);
			
			Set<WebLink> wlSet = clientConn.discover();
				
			if (wlSet != null) {
				for (WebLink wl : wlSet) {
					_Logger.info(" --> WebLink: " + wl.getURI() + ". Attributes: " + wl.getAttributes());
				}
			}
			
			Thread.sleep(DEFAULT_TIMEOUT); // DEFAULT_TIMEOUT is in milliseconds - for instance, 120000 (2 minutes)
			
			this.csg.stopServer();
		} catch (Exception e) {
			// log a message!
		}
	}
}
