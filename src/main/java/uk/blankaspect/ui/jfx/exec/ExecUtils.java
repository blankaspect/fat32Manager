/*====================================================================*\

ExecUtils.java

Class: utility methods related to the execution of code.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.exec;

//----------------------------------------------------------------------


// IMPORTS


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

import uk.blankaspect.common.thread.DaemonFactory;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS RELATED TO THE EXECUTION OF CODE


/**
 * This class contains utility methods that relate to the execution of code.
 */

public class ExecUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ExecUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified {@link Runnable} on the JavaFX application thread after the specified delay.
	 * <p>
	 * If the delay is zero or negative, the {@code Runnable} is executed by calling {@link Platform#runLater(Runnable)}
	 * immediately; otherwise, {@link Platform#runLater(Runnable)} is called from a background thread that is scheduled
	 * by a {@link ScheduledExecutorService}.
	 *
	 * @param delay
	 *          the delay in milliseconds before {@code runnable} is executed.
	 * @param runnable
	 *          the {@code Runnable} that will be executed after {@code delay}.
	 */

	public static void afterDelay(
		long		delay,
		Runnable	runnable)
	{
		if (delay > 0)
		{
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(DaemonFactory::create);
			executor.scheduleWithFixedDelay(() ->
			{
				executor.shutdown();
				Platform.runLater(runnable);
			},
			delay, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		}
		else
			Platform.runLater(runnable);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
