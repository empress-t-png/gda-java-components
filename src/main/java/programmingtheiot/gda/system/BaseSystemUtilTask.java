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
package programmingtheiot.gda.system;

import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;

/**
 * Base class for system utilization tasks.
 */
public abstract class BaseSystemUtilTask
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(BaseSystemUtilTask.class.getName());
	
	
	// private
	
	private String name = ConfigConst.NOT_SET;
	private int typeID = ConfigConst.DEFAULT_TYPE_ID;
	
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param name The name of the system utilization task.
	 * @param typeID The type ID of the system utilization task.
	 */
	public BaseSystemUtilTask(String name, int typeID)
	{
		super();
		
		if (name != null) {
			this.name = name;
		}
		
		this.typeID = typeID;
	}
	
	
	// public methods
	
	/**
	 * Returns the name of the system utilization task.
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Returns the type ID of the system utilization task.
	 * 
	 * @return int
	 */
	public int getTypeID()
	{
		return this.typeID;
	}
	
	/**
	 * Template method definition. Sub-class will implement this to retrieve
	 * the system utilization measure.
	 * 
	 * @return float
	 */
	public abstract float getTelemetryValue();
	
}