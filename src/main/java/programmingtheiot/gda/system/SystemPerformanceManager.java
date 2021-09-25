/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.app.GatewayDeviceApp;

/* new imports*/
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemPerformanceManager
{
	
	
	// private var's
	private int pollSecs = 30;
	private static final Logger _Logger = Logger.getLogger(SystemPerformanceManager.class.getName());
	private ScheduledExecutorService schedExecSvc = null; 
	private SystemCpuUtilTask cpuUtilTask = null;  // new instance of SystemCpuUtilTask
	private SystemMemUtilTask memUtilTask = null;  // new instance of SystemCpuUtilTask
	private Runnable taskRunner = null;
	private boolean isStarted = false;
	private IDataMessageListener listener = null;
	
	// constructors
	
	/**
	 * Default.
	 * Constructor with default pollSec(60s)
	 */
	public SystemPerformanceManager()
	{

		this(ConfigConst.DEFAULT_POLL_CYCLES);
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param pollSecs The number of seconds between each scheduled task poll.
	 */
	public SystemPerformanceManager(int pollSecs)
	{
		if(pollSecs > 1 && pollSecs < Integer.MAX_VALUE)  // Set this.pollSecs to pollSecs if pollSecs belongs to (1,MAX_INT)
		{  	
			this.pollSecs = pollSecs;
		}
		
		this.schedExecSvc = Executors.newScheduledThreadPool(1); // Instantiate scheduler
		this.cpuUtilTask = new SystemCpuUtilTask(); // Instantiate SystemCpuUtilTask
		this.memUtilTask = new SystemMemUtilTask(); // Instantiate SystemMemUtilTask

		this.taskRunner = () -> {
		    this.handleTelemetry(); 
		};
	}
	
	
	// public methods
	/**
	 * Get CPU and Memory usage and log them out
	 */
	
	public void handleTelemetry() 
	{
		float cpuUtilTask = this.cpuUtilTask.getTelemetryValue(); // Get CPU usage value
		float memUtilTask = this.memUtilTask.getTelemetryValue(); // Get Memory usage value
		_Logger.info("cpuUtilTask = " + cpuUtilTask); // Log out CPU performance
		_Logger.info("memUtilTask = " + memUtilTask); // Log out Memory performance
		SystemPerformanceData data = new SystemPerformanceData();
		data.setCpuUtilization(cpuUtilTask);
		data.setMemoryUtilization(memUtilTask);
		this.listener.handleSystemPerformanceMessage(ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE, data);
	}
	/**
	 * Unused method
	 * @param listener
	 */
	public void setDataMessageListener(IDataMessageListener listener)
	{
	}
	
	/**
	 * Start the system performance task
	 */
	public void startManager()  // start SystemPerformanceManager and run handleTelemetry every (pollSecs) seconds
	{
		if (! this.isStarted) {
		    ScheduledFuture<?> futureTask = this.schedExecSvc.scheduleAtFixedRate(this.taskRunner, 0L, this.pollSecs, TimeUnit.SECONDS);
		    this.isStarted = true;
		}
		_Logger.info("SystemPerformanceManager started successfully."); // Log an info when started
	}
	/**
	 * Stop the system performance task
	 */
	public void stopManager()   // stop SystemPerformanceManager
	{
		this.schedExecSvc.shutdown(); // stop the scheduler
		_Logger.info("SystemPerformanceManager is stopped."); // Log an info when stopped
	}
	
	
	public void setDataListener(IDataMessageListener listener) {
		this.listener = listener;
	}
	
}
