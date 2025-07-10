/*====================================================================*\

ITaskStatus.java

Interface: status of a task.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.task;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.common.message.MessageConstants;

//----------------------------------------------------------------------


// INTERFACE: STATUS OF A TASK


public interface ITaskStatus
	extends ICancellable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

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

	/**
	 * Returns a string that may be used to separate adjacent components of the text of a status message.
	 *
	 * @return a string that may be used to separate adjacent components of the text of a status message.
	 */

	default String messageSeparator()
	{
		return MessageConstants.SEPARATOR;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that contains a space (U+0020) followed by a string that may be used to separate adjacent
	 * components of the text of a status message.
	 *
	 * @return a string that contains a space (U+0020) followed by a string that may be used to separate adjacent
	 *         components of the text of a status message.
	 */

	default String spaceMessageSeparator()
	{
		return MessageConstants.SPACE_SEPARATOR;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the message of this task status to the result of joining the string representations of the two specified
	 * objects with the value that is returned by {@link #spaceMessageSeparator()}.
	 *
	 * @param obj1
	 *          the object whose string representation will constitute the first part of the message.
	 * @param obj2
	 *          the object whose string representation will constitute the second part of the message.
	 */

	default void setSpacedMessage(
		Object	obj1,
		Object	obj2)
	{
		setMessage(obj1 + spaceMessageSeparator() + obj2);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
