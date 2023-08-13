/*====================================================================*\

OrderedElementMatcher.java

Class: ordered element matcher.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.matcher;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: ORDERED ELEMENT MATCHER


/**
 * This class provides a means of finding the sets of elements of a <i>source sequence</i> that match all the elements
 * of a <i>target sequence</i> in the order in which they appear in the target sequence.  An element of the source
 * sequence, <i>s</i>, is deemed to match an element of the target sequence, <i>t</i>, if {@code t.equals(s)} returns
 * {@code true}.  The target sequence may not contain {@code null} elements.
 *
 * @param <T>
 *          the type of the elements of the source and target sequences.
 */

public class OrderedElementMatcher<T>
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The source sequence that will be matched against {@link #target}. */
	private	ISequence<T>	source;

	/** The target sequence that will be matched against {@link #source}. */
	private	ISequence<T>	target;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an element matcher with no source or target sequence.
	 */

	public OrderedElementMatcher()
	{
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an element matcher with no source sequence and the specified target sequence.  A source
	 * sequence can be set with {@link #setSourceSequence(ISequence)}.
	 *
	 * @param  target
	 *           the target sequence that will be matched against a source sequence.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code target} is {@code null}, or</li>
	 *             <li>the length of {@code target} is less than 1.</li>
	 *           </ul>
	 */

	public OrderedElementMatcher(
		ISequence<T>	target)
	{
		// Validate arguments
		if ((target == null) || (target.getLength() < 1))
			throw new IllegalArgumentException("Invalid target");

		// Set instance variables
		this.target = target;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an element matcher with the specified source and target sequences.
	 *
	 * @param  source
	 *           the source sequence that will be matched against {@code target}.
	 * @param  target
	 *           the target sequence that will be matched against {@code source}.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code source} is {@code null}, or</li>
	 *             <li>{@code target} is {@code null}, or</li>
	 *             <li>the length of {@code target} is less than 1.</li>
	 *           </ul>
	 */

	public OrderedElementMatcher(
		ISequence<T>	source,
		ISequence<T>	target)
	{
		// Validate arguments
		if (source == null)
			throw new IllegalArgumentException("Invalid source");
		if ((target == null) || (target.getLength() < 1))
			throw new IllegalArgumentException("Invalid target");

		// Set instance variables
		this.source = source;
		this.target = target;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the source sequence of this matcher.
	 *
	 * @return the source sequence of this matcher.
	 */

	public ISequence<T> getSourceSequence()
	{
		return source;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the target sequence of this matcher.
	 *
	 * @return the target sequence of this matcher.
	 */

	public ISequence<T> getTargetSequence()
	{
		return target;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the source sequence of this matcher to the specified sequence and returns this matcher.
	 *
	 * @param  source
	 *           the sequence that will be set as the source sequence of this matcher.
	 * @return this matcher.
	 * @throws IllegalArgumentException
	 *           if {@code source} is {@code null}.
	 */

	public OrderedElementMatcher<T> setSourceSequence(
		ISequence<T>	source)
	{
		// Validate arguments
		if (source == null)
			throw new IllegalArgumentException("Invalid source");

		// Set instance variables
		this.source = source;

		// Return this matcher
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the target sequence of this matcher to the specified sequence and returns this matcher.
	 *
	 * @param  target
	 *           the sequence that will be set as the target sequence of this matcher.
	 * @return this matcher.
	 * @throws IllegalArgumentException
	 *           if {@code target} is {@code null} or the length of {@code target} is less than 1.
	 */

	public OrderedElementMatcher<T> setTargetSequence(
		ISequence<T>	target)
	{
		// Validate arguments
		if ((target == null) || (target.getLength() < 1))
			throw new IllegalArgumentException("Invalid target");

		// Set instance variables
		this.target = target;

		// Return this matcher
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if all the elements of the target sequence occur one or more times in the source sequence in
	 * the same order in which they occur in the target sequence.
	 *
	 * @return {@code true} if all the elements of the target sequence occur one or more times in the source sequence in
	 *         the same order in which they occur in the target sequence, {@code false} otherwise.
	 * @throws IllegalStateException
	 *           if no source or target sequence has been set on this matcher.
	 */

	public boolean match()
	{
		return findMatches(0, 0, new BitSet(source.getLength()), 1, new ArrayList<>());
	}

	//------------------------------------------------------------------

	/**
	 * Finds the matching elements of the source and target sequences of this matcher.  The search terminates when no
	 * more matches can be found.
	 *
	 * @return a list of the matches (ie, bit arrays of indices of source elements) that were found.
	 * @throws IllegalStateException
	 *           if no source or target sequence has been set on this matcher.
	 */

	public List<BitSet> findMatches()
	{
		return findMatches(Integer.MAX_VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * Finds the matching elements of the source and target sequences of this matcher.  The search terminates when the
	 * specified maximum number of matches is reached or when no more matches can be found.
	 *
	 * @param  maxNumMatches
	 *           the maximum number of matches that will be found before the search is terminated.
	 * @return a list of the matches (ie, bit arrays of indices of source elements) that were found.
	 * @throws IllegalArgumentException
	 *           if {@code maxNumMatches} is less than 1.
	 * @throws IllegalStateException
	 *           if no source or target sequence has been set on this matcher.
	 */

	public List<BitSet> findMatches(
		int	maxNumMatches)
	{
		// Validate arguments
		if (maxNumMatches < 1)
			throw new IllegalArgumentException("Maximum number of matches out of bounds: " + maxNumMatches);

		// Create a list of matches by comparing the elements of the source sequence against the elements of the target
		// sequence
		List<BitSet> matches = new ArrayList<>();
		findMatches(0, 0, new BitSet(source.getLength()), maxNumMatches, matches);
		return matches;
	}

	//------------------------------------------------------------------

	/**
	 * Finds the matching elements of the source and target sequences of this matcher starting at the specified indices.
	 * This method calls itself recursively as it traverses the source and target sequences.  If the last element of the
	 * target sequence is matched, a bit array of the indices of elements of the source sequence that match elements of
	 * the target sequence is added to the specified list of bit arrays.  The search terminates when the specified
	 * maximum number of matches is reached or when no more matches can be found.
	 *
	 * @param  sourceIndex
	 *           the index of the element of the source sequence at which the search will start.
	 * @param  targetIndex
	 *           the index of the element of the target sequence at which the search will start.
	 * @param  sourceIndices
	 *           a bit array of indices of source elements that have already been matched.
	 * @param  maxNumMatches
	 *           the maximum number of matches that will be found before the search is terminated.
	 * @param  matches
	 *           a list of the matches (ie, bit arrays of indices of source elements) that have already been found in
	 *           the current search.
	 * @return {@code true} if the search was terminated because the maximum number of matches was reached;
	 *         {@code false} if the search was terminated because no more matches could be found.
	 */

	private boolean findMatches(
		int				sourceIndex,
		int				targetIndex,
		BitSet			sourceIndices,
		int				maxNumMatches,
		List<BitSet>	matches)
	{
		// Get the next element from the target sequence
		T targetElement = target.getElement(targetIndex);

		// Initialise loop variables
		int targetEndIndex = targetIndex + 1;
		int sourceEndIndex = source.getLength() - target.getLength() + targetEndIndex;
		int index = sourceIndex;

		// While there are elements remaining in the source sequence ...
		while (index < sourceEndIndex)
		{
			// If the current target element matches an element of the source sequence ...
			if (targetElement.equals(source.getElement(index)))
			{
				// Set the appropriate bit in the bit array of source indices
				sourceIndices.set(index);

				// If all elements of the target have been matched, add the indices of the matching source elements to
				// the list of matches
				if (targetEndIndex >= target.getLength())
				{
					// Add a copy of the bit array of source indices to the list of matches
					matches.add((BitSet)sourceIndices.clone());

					// If all required matches have been found, return
					if (matches.size() >= maxNumMatches)
						return true;
				}

				// ... otherwise, continue to match the next elements of the source and target sequences
				else if (findMatches(index + 1, targetEndIndex, sourceIndices, maxNumMatches, matches))
					return true;

				// Clear the bit in the bit array of source indices
				sourceIndices.clear(index);
			}

			// Increment the source index
			++index;
		}

		// Indicate failure
		return false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: ELEMENT SEQUENCE


	/**
	 * This interface defines the methods that must be implemented by the sequences that are processed by an instance of
	 * {@link OrderedElementMatcher}.
	 */

	public interface ISequence<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the length of this sequence.
		 *
		 * @return the length of this sequence.
		 */

		int getLength();

		//--------------------------------------------------------------

		/**
		 * Returns the element at the specified index in this sequence.
		 *
		 * @param  index
		 *           the index of the required element.
		 * @return the element at {@code index} in this sequence.
		 */

		T getElement(
			int	index);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
