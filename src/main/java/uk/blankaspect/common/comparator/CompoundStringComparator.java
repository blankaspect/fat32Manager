/*====================================================================*\

CompoundStringComparator.java

Class: factory methods for comparators of compound strings.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.comparator;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Comparator;
import java.util.List;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// CLASS: FACTORY METHODS FOR COMPARATORS OF COMPOUND STRINGS


/**
 * This class contains factory methods for comparators of compound strings.
 */

public class CompoundStringComparator
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private CompoundStringComparator()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Comparator<String> respectCase(
		char	separator)
	{
		return (str1, str2) ->
		{
			// Split first string into its components
			List<String> components1 = StringUtils.split(str1, separator);
			int numComponents1 = components1.size();

			// Split second string into its components
			List<String> components2 = StringUtils.split(str2, separator);
			int numComponents2 = components2.size();

			// Compare components of strings
			int numComponents = Math.min(numComponents1, numComponents2);
			for (int i = 0; i < numComponents; i++)
			{
				int result = components1.get(i).compareTo(components2.get(i));
				if (result != 0)
					return result;
			}

			// If no difference in common prefix, compare number of components
			return Integer.compare(numComponents1, numComponents2);
		};
	}

	//------------------------------------------------------------------

	public static Comparator<String> ignoreCase(
		char	separator)
	{
		return (str1, str2) ->
		{
			// Split first string into its components
			List<String> components1 = StringUtils.split(str1, separator);
			int numComponents1 = components1.size();

			// Split second string into its components
			List<String> components2 = StringUtils.split(str2, separator);
			int numComponents2 = components2.size();

			// Compare components of strings, ignoring letter case
			int numComponents = Math.min(numComponents1, numComponents2);
			for (int i = 0; i < numComponents; i++)
			{
				int result = components1.get(i).compareToIgnoreCase(components2.get(i));
				if (result != 0)
					return result;
			}

			// If no difference in common prefix, compare number of components
			return Integer.compare(numComponents1, numComponents2);
		};
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
