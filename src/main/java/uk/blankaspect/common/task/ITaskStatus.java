/*====================================================================*\

ITaskStatus.java

Interface: status of a task.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.task;

//----------------------------------------------------------------------


// INTERFACE: STATUS OF A TASK


public interface ITaskStatus
	extends ICancellable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A string that may be used to separate adjacent components of the text of a status message. */
	String	MESSAGE_SEPARATOR		= "\u200B";		// zero-width space;

	/** A space followed by a string that may be used to separate adjacent components of the text of a status
		message. */
	String	SPACE_MESSAGE_SEPARATOR	= " " + MESSAGE_SEPARATOR;

	/** A task status that has no effect. */
	ITaskStatus	VOID	= new ITaskStatus()
	{
		@Override
		public void setMessage(
			String	message)
		{
			// do nothing
		}

		@Override
		public void setProgress(
			double	progress)
		{
			// do nothing
		}
	};

////////////////////////////////////////////////////////////////////////
//  Overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the associated task has been cancelled.  The default implementation returns {@code
	 * false}.
	 *
	 * @return {@code true} if the associated task has been cancelled.
	 */

	@Override
	default boolean isCancelled()
	{
		return false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the message of this task status to the specified value.
	 *
	 * @param message
	 *          the value to which the message of this task status will be set.
	 */

	void setMessage(
		String	message);

	//------------------------------------------------------------------

	/**
	 * Sets the progress of this task status to the specified value.
	 *
	 * @param progress
	 *          the value to which the progress of this task status will be set, in the interval [0, 1].
	 */

	void setProgress(
		double	progress);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
