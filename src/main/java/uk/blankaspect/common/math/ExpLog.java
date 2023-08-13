/*====================================================================*\

ExpLog.java

Class: exponential/logarithmic functions.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.math;

import uk.blankaspect.common.function.IFunction1;

//----------------------------------------------------------------------


// CLASS: EXPONENTIAL/LOGARITHMIC FUNCTIONS


/**
 * This class provides methods that apply two types of exponential/logarithmic function to a single variable.  The
 * domain and codomain of a function are the interval [0, 1].  The two types of function are referred to as
 * <i>type A</i> and <i>type B</i>.
 * <p>
 * <b>Type A</b>:<br>
 * &nbsp;&nbsp; <i>f</i>(<i>x</i>) = (<i>a</i><sup><i>x</i></sup> &minus; 1) / (<i>a</i> &minus; 1) ,
 * <i>x</i> &isin; [0, 1]
 * </p>
 * <p>
 * <b>Type B</b>:<br>
 * &nbsp;&nbsp; <i>f</i>(<i>y</i>) = (<i>a</i><sup><i>y</i></sup> &minus; 1) / (<i>a</i> &minus; 1) ,
 * <i>y</i> &isin; [0, 1] &nbsp; &equiv; &nbsp; <i>f</i>(<i>x</i>) = ln ((<i>a</i> + 1) / 2) / ln <i>a</i> ,
 * <i>x</i> &isin; [0, 1]
 * </p>
 * <p style="margin-bottom: 0.25em;">
 * Each type of function has two variants:
 * </p>
 * <ul style="margin-top: 0.25em;">
 *   <li><b>00-11</b> (growth) : when <i>x</i> = 0, <i>y</i> = 0; when <i>x</i> = 1, <i>y</i> = 1.</li>
 *   <li><b>01-10</b> (decay) : when <i>x</i> = 0, <i>y</i> = 1; when <i>x</i> = 1, <i>y</i> = 0.</li>
 * </ul>
 * <p style="margin-bottom: 0.25em;">
 * All four kinds of function have a single parameter: the value of the dependent variable, <i>y</i>, when the value of
 * the independent variable, <i>x</i>, is 0.5, which is referred to as the mid-<i>y</i> value or <i>yMid</i>.
 * </p>
 * <ul style="margin-top: 0.25em;">
 *   <li>If <i>yMid</i> < 0.5, the function is exponential.</li>
 *   <li>If <i>yMid</i> = 0.5, the function is linear.</li>
 *   <li>If <i>yMid</i> > 0.5, the function is logarithmic.</li>
 * </ul>
 */

public class ExpLog
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** For a type-A function, the minimum value of <i>y</i> when <i>x</i> = 0.5. */
	private static final	double	MIN_Y_MID_A	= 0.001;

	/** For a type-A function, the maximum value of <i>y</i> when <i>x</i> = 0.5. */
	private static final	double	MAX_Y_MID_A	= 1.0 - MIN_Y_MID_A;

	/** For a type-B function, the minimum value of <i>y</i> when <i>x</i> = 0.5. */
	private static final	double	MIN_Y_MID_B	= 0.02;

	/** For a type-B function, the maximum value of <i>y</i> when <i>x</i> = 0.5. */
	private static final	double	MAX_Y_MID_B	= 1.0 - MIN_Y_MID_B;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The kind of this function. */
	private	FunctionKind	functionKind;

	/** The value of <i>y</i> when <i>x</i> = 0.5. */
	private	double			yMid;

	/** Flag: {@code true} if this function is linear (ie, {@link #yMid} == 0.5). */
	private	boolean			linear;

	/** The coefficient <i>a</i> of this function. */
	private	double			a;

	/** The coefficient <i>b</i> of this function. */
	private	double			b;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an evaluator of the specified kind of exponential/logarithmic function of a single
	 * variable on the interval [0, 1].
	 *
	 * @param functionKind
	 *          the kind of function.
	 * @param yMid
	 *          the desired value of the dependent variable, <i>y</i>, when the value of the independent variable,
	 *          <i>x</i>, is 0.5.
	 */

	public ExpLog(
		FunctionKind	functionKind,
		double			yMid)
	{
		// Validate arguments
		if (functionKind == null)
			throw new IllegalArgumentException("Null function kind");
		if ((yMid < functionKind.minYMid) || (yMid > functionKind.maxYMid))
			throw new IllegalArgumentException("yMid out of bounds: " + yMid);

		// Initialise instance variables
		this.functionKind = functionKind;
		this.yMid = yMid;
		linear = (yMid == 0.5);
		if (!linear)
		{
			// Case: type-A function
			if ((functionKind == FunctionKind.A_00_11) || (functionKind == FunctionKind.A_01_10))
			{
				// Initialise coefficients
				a = (1.0 / yMid) - 1.0;
				a *= a;
				b = 1.0 / (a - 1.0);
			}

			// Case: type-B function
			else
			{
				// Invert yMid if it is greater than 0.5
				if (yMid > 0.5)
					yMid = 1.0 - yMid;

				// Initialise coefficient
				a = findCoeffTypeB(yMid);
				b = 1.0 / Math.log(a);
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a type-A exponential/logarithmic growth function with the specified
	 * mid-<i>y</i> value (the value of <i>y</i> when <i>x</i> = 0.5).
	 *
	 * @param  yMid
	 *           the desired value of <i>y</i> when <i>x</i> is 0.5.
	 * @return a type-A exponential/logarithmic growth function whose mid-<i>y</i> value is {@code yMid}.
	 */

	public static ExpLog a_00_11(
		double	yMid)
	{
		return new ExpLog(FunctionKind.A_00_11, yMid);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a type-A exponential/logarithmic decay function with the specified
	 * mid-<i>y</i> value (the value of <i>y</i> when <i>x</i> = 0.5).
	 *
	 * @param  yMid
	 *           the desired value of <i>y</i> when <i>x</i> is 0.5.
	 * @return a type-A exponential/logarithmic decay function whose mid-<i>y</i> value is {@code yMid}.
	 */

	public static ExpLog a_01_10(
		double	yMid)
	{
		return new ExpLog(FunctionKind.A_01_10, yMid);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a type-B exponential/logarithmic growth function with the specified
	 * mid-<i>y</i> value (the value of <i>y</i> when <i>x</i> = 0.5).
	 *
	 * @param  yMid
	 *           the desired value of <i>y</i> when <i>x</i> is 0.5.
	 * @return a type-B exponential/logarithmic growth function whose mid-<i>y</i> value is {@code yMid}.
	 */

	public static ExpLog b_00_11(
		double	yMid)
	{
		return new ExpLog(FunctionKind.B_00_11, yMid);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a type-B exponential/logarithmic decay function with the specified
	 * mid-<i>y</i> value (the value of <i>y</i> when <i>x</i> = 0.5).
	 *
	 * @param  yMid
	 *           the desired value of <i>y</i> when <i>x</i> is 0.5.
	 * @return a type-B exponential/logarithmic decay function whose mid-<i>y</i> value is {@code yMid}.
	 */

	public static ExpLog b_01_10(
		double	yMid)
	{
		return new ExpLog(FunctionKind.B_01_10, yMid);
	}

	//------------------------------------------------------------------

	/**
	 * Finds the coefficient of a type-B function with the specified mid-<i>y</i> value, and returns the result.  The
	 * method uses the Illinois variant of <i>regula falsi</i> (the method of false position) to find the root of the
	 * following function:
	 * <p style="margin-left: 1.5em;">
	 * <i>f</i>(<i>x</i>) = ln ((<i>x</i> + 1) / 2) / ln <i>x</i> &minus; <i>yMid</i>
	 * </p>
	 *
	 * @param  yMid
	 *           the value of <i>y</i> when <i>x</i> is 0.5.
	 * @return the coefficient of a type-B function whose mid-<i>y</i> value is {@code yMid}.
	 */

	private static double findCoeffTypeB(
		double	yMid)
	{
		// Initialise variables
		double prevX = Double.NaN;
		int convergenceCount = 0;
		int side = 0;

		// Define function
		IFunction1<Double, Double> f = x -> Math.log(0.5 * (x + 1.0)) / Math.log(x) - yMid;

		// Initialise endpoints of bracketing interval
		double x0 = 0.0;
		double x1 = 1.0 - 2.0 * Math.ulp(1.0);

		// Evaluate function at endpoints
		double fx0 = f.invoke(x0);
		double fx1 = f.invoke(x1);

		// Iterate until result of function converges
		while (true)
		{
			// Calculate x-intercept of secant through (x0, f(x0)) and (x1, f(x1))
			double x = (fx1 * x0 - fx0 * x1) / (fx1 - fx0);

			// Test for convergence
			if (Double.isFinite(prevX) && (Math.abs(x - prevX) < 2.0 * Math.ulp(x)))
			{
				if (++convergenceCount == 2)
					return x;
			}
			else
				convergenceCount = 0;
			prevX = x;

			// Evaluate function at x-intercept of secant
			double fx = f.invoke(x);

			// Update endpoints and f(x) of endpoints (Illinois variant)
			if (fx0 * fx > 0.0)
			{
				x0 = x;
				fx0 = fx;
				if (side > 0)
					fx1 *= 0.5;
				side = 1;
			}
			else if (fx1 * fx > 0.0)
			{
				x1 = x;
				fx1 = fx;
				if (side < 0)
					fx0 *= 0.5;
				side = -1;
			}
			else
				side = 0;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Applies this function to the specified value and returns the result.
	 *
	 * @param  x
	 *           the value, on the interval [0, 1], to which this function will be applied.
	 * @return the result of applying this function to the value {@code x}.
	 */

	public double apply(
		double	x)
	{
		switch (functionKind)
		{
			case A_00_11:
				return linear ? x : Math.min(Math.max(0.0, b * (Math.pow(a, x) - 1.0)), 1.0);

			case A_01_10:
				return linear ? 1.0 - x : Math.min(Math.max(0.0, b * (Math.pow(a, (1.0 - x)) - 1.0)), 1.0);

			case B_00_11:
				return linear ? x
							  : Math.min(Math.max(0.0, (yMid < 0.5)
															? Math.log((a - 1.0) * x + 1.0) * b
															: 1.0 - Math.log((a - 1.0) * (1.0 - x) + 1.0) * b),
										 1.0);

			case B_01_10:
				return linear ? 1.0 - x
							  : Math.min(Math.max(0.0, (yMid < 0.5)
															? Math.log((a - 1.0) * (1.0 - x) + 1.0) * b
															: 1.0 - Math.log((a - 1.0) * x + 1.0) * b),
										 1.0);
		}

		return Double.NaN;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: KIND OF FUNCTION


	/**
	 * This is an enumeration of the kinds of function.
	 */

	public enum FunctionKind
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/**
		 * A type-A exponential/logarithmic growth function: when <i>x</i> = 0, <i>y</i> = 0; when <i>x</i> = 1,
		 * <i>y</i> = 1.
		 */

		A_00_11
		(
			MIN_Y_MID_A, MAX_Y_MID_A
		),

		/**
		 * A type-A exponential/logarithmic decay function: when <i>x</i> = 0, <i>y</i> = 1; when <i>x</i> = 1,
		 * <i>y</i> = 0.
		 */

		A_01_10
		(
			MIN_Y_MID_A, MAX_Y_MID_A
		),

		/**
		 * A type-B exponential/logarithmic growth function: when <i>x</i> = 0, <i>y</i> = 0; when <i>x</i> = 1,
		 * <i>y</i> = 1.
		 */

		B_00_11
		(
			MIN_Y_MID_B, MAX_Y_MID_B
		),

		/**
		 * A type-B exponential/logarithmic decay function: when <i>x</i> = 0, <i>y</i> = 1; when <i>x</i> = 1,
		 * <i>y</i> = 0.
		 */

		B_01_10
		(
			MIN_Y_MID_B, MAX_Y_MID_B
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The minimum mid-<i>y</i> value of this kind of function. */
		private	double	minYMid;

		/** The maximum mid-<i>y</i> value of this kind of function. */
		private	double	maxYMid;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an enumeration constant for a kind of function.
		 *
		 * @param minYMid
		 *          the minimum mid-<i>y</i> value of the kind of function.
		 * @param maxYMid
		 *          the maximum mid-<i>y</i> value of the kind of function.
		 */

		private FunctionKind(
			double	minYMid,
			double	maxYMid)
		{
			// Initialise instance variables
			this.minYMid = minYMid;
			this.maxYMid = maxYMid;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the minimum mid-<i>y</i> value of this kind of function.
		 *
		 * @return the minimum mid-<i>y</i> value of this kind of function.
		 */

		public double getMinYMid()
		{
			return minYMid;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the maximum mid-<i>y</i> value of this kind of function.
		 *
		 * @return the maximum mid-<i>y</i> value of this kind of function.
		 */

		public double getMaxYMid()
		{
			return maxYMid;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
