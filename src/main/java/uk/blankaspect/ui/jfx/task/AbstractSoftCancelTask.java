/*====================================================================*\

AbstractSoftCancelTask.java

Class: abstract task with 'soft' cancellation.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.task;

//----------------------------------------------------------------------


// IMPORTS


import javafx.concurrent.Task;

import uk.blankaspect.common.task.ITaskStatus;

//----------------------------------------------------------------------


// CLASS: ABSTRACT TASK WITH 'SOFT' CANCELLATION


/**
 * This abstract class extends {@link Task} and modifies it so that a task may be cancelled in a 'soft' manner by
 * setting a flag that is tested in the {@link #call()} method.  The flag is set by calling {@link #cancel(boolean)},
 * which overrides the corresponding method in the superclass and changes its behaviour in two important respects:
 * <ul>
 *   <li>it does not interrupt the task, and</li>
 *   <li>it does not cause the task to enter the {@link State#CANCELLED CANCELLED} state.</li>
 * </ul>
 * <p>
 * The task may be made to enter the {@link State#CANCELLED CANCELLED} state by calling {@link #hardCancel(boolean)}
 * with the argument {@code true} or by calling {@link #hardCancel(boolean)} after calling {@link #cancel(boolean)}.
 * </p>
 * @param <V>
 *          the type of the value that is returned by the task.
 */

public abstract class AbstractSoftCancelTask<V>
	extends Task<V>
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** Flag: if {@code true}, the task has been cancelled. */
	private	boolean	cancelled;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a task that may be cancelled by setting a flag that is tested in the {@link #call()}
	 * method.
	 */

	protected AbstractSoftCancelTask()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the <i>cancelled</i> flag of this task to {@code true}.  The corresponding superclass method is not called,
	 * and the task is not interrupted.  Instead, the {@link Task#call()} method should call {@link #isCancelled()} at
	 * appropriate points to determine whether the task has been cancelled, and it should return as soon as possible
	 * after {@link #isCancelled()} returns {@code true}.
	 * <p>
	 * This task will not enter the {@link State#CANCELLED CANCELLED} state as a result of calling this method.  To
	 * cause the task to enter the {@code CANCELLED} state, call {@link #hardCancel(boolean)} with the argument {@code
	 * true} or after calling this method.
	 * </p>
	 *
	 * @param  mayInterruptIfRunning
	 *           this argument is ignored.
	 * @return {@code true}.
	 */

	@Override
	public boolean cancel(
		boolean	mayInterruptIfRunning)
	{
		cancelled = true;
		return true;
	};

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	};

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Calls {@link Task#cancel(boolean)} if either of the following conditions is {@code true}:
	 * <ul>
	 *   <li>
	 *     {@code force} is {@code true}, or
	 *   </li>
	 *   <li>
	 *     the <i>cancelled</i> flag of this task is {@code true} as a result of a previous call to {@link
	 *     #cancel(boolean)}.
	 *   </li>
	 * </ul>
	 *
	 * @param force
	 *          if (@code true}, {@link Task#cancel(boolean)} will be called with the argument {@code true}; otherwise,
	 *          if the <i>cancelled</i> flag of this task is {@code true}, {@link Task#cancel(boolean)} will be called
	 *          with the argument {@code false}.
	 */

	protected void hardCancel(
		boolean	force)
	{
		if (force || isCancelled())
			super.cancel(force);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of {@link ITaskStatus} whose methods do the following:
	 * <ul>
	 *   <li>
	 *     {@link ITaskStatus#setMessage(String) setMessage(String)} calls the {@link #updateMessage(String)} method of
	 *     this task with its argument.
	 *   </li>
	 *   <li>
	 *     {@link ITaskStatus#setProgress(double) setProgress(double)} calls the {@link #updateProgress(double, double)}
	 *     method of this task with its argument as the first argument of {@code updateProgress(double, double)} and 1.0
	 *     as the second argument.
	 *   </li>
	 *   <li>
	 *     {@link ITaskStatus#isCancelled() isCancelled()} calls the {@link #isCancelled()} method and returns the
	 *     result.
	 *   </li>
	 * </ul>
	 *
	 * @return a new instance of {@link ITaskStatus} whose methods behave in the way described above.
	 */

	protected ITaskStatus createTaskStatus()
	{
		return new ITaskStatus()
		{
			@Override
			public void setMessage(
				String	message)
			{
				updateMessage(message);
			}

			@Override
			public void setProgress(
				double	progress)
			{
				updateProgress(progress, 1.0);
			}

			@Override
			public boolean isCancelled()
			{
				return AbstractSoftCancelTask.this.isCancelled();
			}
		};
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
