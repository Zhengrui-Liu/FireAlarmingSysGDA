package programmingtheiot.part04.integration.connection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.gda.app.DeviceDataManager;
import programmingtheiot.gda.connection.SmtpClientConnector;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.connection.*;

public class SocketPictureSenderTest {
	
	private MyThread t;
	
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	
	/**
	 * Test for socket function to transmit picture
	 */
	@Test
	public void testSocket() {
		this.t = new MyThread();
		t.start();
	}
	
	class MyThread extends Thread {
		
		private int latestImage = 0;
		/**
		 * The constructor of the socket thread
		 */
		public MyThread() {
			super();
			
		}
		/**
		 * Create a socket server and listen to the connection of clients
		 */
	    @Override
	    public void run() {
	    	while(true) {
	    		try {
					ServerSocket ss = new ServerSocket(5612);
					Socket s = ss.accept();
					InputStream in = s.getInputStream();
					FileOutputStream fos = new FileOutputStream("/Users/liu.zhengr/Downloads/server"+this.latestImage+".jpeg");
					byte[] buf = new byte[1024];
					int len = 0;
					this.latestImage++;
					while ((len = in.read(buf)) != -1)
					{
						fos.write(buf,0,len);
					}
					fos.close();
					in.close();
					s.close();
					ss.close();
				}catch(Exception e){
					System.out.println("failed!");
				}
			}
		}
	}
}
