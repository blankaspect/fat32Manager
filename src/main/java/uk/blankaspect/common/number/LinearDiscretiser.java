/*====================================================================*\

LinearDiscretiser.java

Class: linear discretising function.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.number;

//----------------------------------------------------------------------


// CLASS: LINEAR DISCRETISING FUNCTION


/**
 * This class provides a means of mapping continuous values on a specified interval to the indices of a specified number
 * of subintervals of equal width.  An input value is mapped to the zero-based index of the subinterval that contains
 * it.  If the input value is equal to the upper endpoint of the interval, it is assigned to the last subinterval.
 * <p>
 * If the lower and upper endpoints of the interval are <i>a</i> and <i>b</i> respectively and the number of
 * subintervals of [<i>a</i>, <i>b</i>] is <i>n</i>, then the output value, <i>i</i>, is given by<br>
 * &nbsp;&nbsp;&nbsp;<i>i</i> = min(&#x230A; <i>n</i>(<i>x</i> &minus; <i>a</i>) / (<i>b</i> &minus; <i>a</i>) &#x230B;,
 * <i>n</i>&minus;1)<br>
 * where <i>x</i> is the input value.
 * </p>
 * <p>
 * Alternatively, if the width of one of the <i>n</i> subintervals of [<i>a</i>, <i>b</i>] is
 * &Delta;&nbsp;=&nbsp;(<i>b</i>&nbsp;&minus;&nbsp;<i>a</i>)&nbsp;/&nbsp;<i>n</i>, then<br>
 * &nbsp;&nbsp;&nbsp;<i>i</i> = min( &#x230A; (<i>x</i> &minus; <i>a</i>) / &Delta; &#x230B;, <i>n</i>&minus;1)<br>
 * </p>
 * <p>
 * The mapping can be thought of as assigning an input value to a bin.
 * </p>
 */

public class LinearDiscretiser
	implements IDiscretiser
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The lower endpoint of the continuous interval. */
	private	double	lowerEndpoint;

	/** The upper endpoint of the continuous interval. */
	private	double	upperEndpoint;

	/** The number of equal-width subintervals of the continuous interval. */
	private	int		numSubintervals;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a function that maps continuous values on the interval [0, 1] to the indices of a
	 * specified number of subintervals of that interval.
	 *
	 * @param numSubintervals
	 *          the number of subintervals of the interval [0, 1].
	 */

	public LinearDiscretiser(int numSubintervals)
	{
		// Call alternative constructor
		this(0.0, 1.0, numSubintervals);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a function that maps continuous values on the interval that is defined by the specified
	 * endpoints to the indices of a specified number of subintervals of that interval.
	 *
	 * @param lowerEndpoint
	 *          the lower endpoint of the interval.
	 * @param upperEndpoint
	 *          the upper endpoint of the interval.
	 * @param numSubintervals
	 *          the number of subintervals of the interval [{@code lowerEndpoint}, {@code upperEndpoint}].
	 */

	public LinearDiscretiser(double lowerEndpoint,
							 double upperEndpoint,
							 int    numSubintervals)
	{
		// Validate arguments
		if (!Double.isFinite(lowerEndpoint))
			throw new IllegalArgumentException("Lower endpoint is not finite");
		if (!Double.isFinite(upperEndpoint))
			throw new IllegalArgumentException("Upper endpoint is not finite");
		if (lowerEndpoint >= upperEndpoint)
			throw new IllegalArgumentException("Endpoints are out of order");
		if (numSubintervals <= 0)
			throw new IllegalArgumentException("Number of subintervals is out of bounds: " + numSubintervals);

		// Initialise instance variables
		this.lowerEndpoint = lowerEndpoint;
		this.upperEndpoint = upperEndpoint;
		this.numSubintervals = numSubintervals;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IDiscretiser interface
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the subinterval of the domain of this function into which the specified value falls.  If the
	 * value is equal to the upper endpoint, it is mapped to <i>n</i>&minus;1, where <i>n</i> is the number of
	 * subintervals.
	 */

	@Override
	public int discretise(double value)
	{
		// Validate argument
		if ((value < lowerEndpoint) || (value > upperEndpoint))
			throw new IllegalArgumentException("Value out of bounds: " + value);

		// Normalise value
		value = (value - lowerEndpoint) / (upperEndpoint - lowerEndpoint);

		// Calculate discrete value and return it
		return Math.min((int)(value * (double)numSubintervals), numSubintervals - 1);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the result of clamping the specified value to the interval that is the domain of this discretising
	 * function.
	 *
	 * @param  value
	 *           the value that will be clamped.
	 * @return <ul>
	 *           <li>the lower endpoint of the interval, if {@code value} is less than the lower endpoint;</li>
	 *           <li>the upper endpoint of the interval, if {@code value} is greater than the upper endpoint;</li>
	 *           <li>{@code value} otherwise.</li>
	 *         </ul>
	 */

	public double clamp(double value)
	{
		return Math.min(Math.max(lowerEndpoint, value), upperEndpoint);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
