/*====================================================================*\

OrderedCharacterMatcher.java

Class: ordered character matcher.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.matcher;

//----------------------------------------------------------------------


// CLASS: ORDERED CHARACTER MATCHER


/**
 * This class implements {@link OrderedElementMatcher} for sequences of the type {@link CharSequence}.
 *
 * @see OrderedElementMatcher
 */

public class OrderedCharacterMatcher
	extends OrderedElementMatcher<Character>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a matcher for elements of a {@link CharSequence} with no source or target sequence.
	 */

	public OrderedCharacterMatcher()
	{
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a matcher for elements of a {@link CharSequence} with no source sequence and the
	 * specified target sequence.  A source sequence can be set with {@link #setSource(CharSequence)}.
	 *
	 * @param  target
	 *           the target sequence that will be matched against a source sequence.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code target} is {@code null}, or</li>
	 *             <li>{@code target} is an empty sequence.</li>
	 *           </ul>
	 */

	public OrderedCharacterMatcher(
		CharSequence	target)
	{
		// Call superclass constructor
		super(new Sequence(target));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a matcher for elements of a {@link CharSequence} with the specified source and target
	 * sequences.
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
	 *             <li>{@code target} is an empty sequence.</li>
	 *           </ul>
	 */

	public OrderedCharacterMatcher(
		CharSequence	source,
		CharSequence	target)
	{
		// Call superclass constructor
		super(new Sequence(source), new Sequence(target));
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

	public CharSequence getSource()
	{
		Sequence source = (Sequence)getSourceSequence();
		return ((source == null) ? null : source.sequence);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the target sequence of this matcher.
	 *
	 * @return the target sequence of this matcher.
	 */

	public CharSequence getTarget()
	{
		Sequence target = (Sequence)getTargetSequence();
		return ((target == null) ? null : target.sequence);
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

	public OrderedCharacterMatcher setSource(
		CharSequence	source)
	{
		return (OrderedCharacterMatcher)setSourceSequence(new Sequence(source));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the target sequence of this matcher to the specified sequence and returns this matcher.
	 *
	 * @param  target
	 *           the sequence that will be set as the target sequence of this matcher.
	 * @return this matcher.
	 * @throws IllegalArgumentException
	 *           if {@code target} is {@code null} or {@code target} is an empty sequence.
	 */

	public OrderedCharacterMatcher setTarget(
		CharSequence	target)
	{
		return (OrderedCharacterMatcher)setTargetSequence(new Sequence(target));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: SEQUENCE


	/**
	 * This class implements {@link OrderedElementMatcher.ISequence} by wrapping an instance of {@link CharSequence}.
	 */

	private static class Sequence
		implements OrderedElementMatcher.ISequence<Character>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The character sequence that backs this sequence. */
		private	CharSequence	sequence;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a sequence that wraps the specified instance of {@link CharSequence}.
		 *
		 * @param sequence
		 *          the character sequence that will be wrapped by the sequence.
		 */

		private Sequence(
			CharSequence	sequence)
		{
			this.sequence = sequence;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : OrderedElementMatcher.ISequence interface
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public int getLength()
		{
			return sequence.length();
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public Character getElement(
			int	index)
		{
			return sequence.charAt(index);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
