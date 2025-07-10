/*====================================================================*\

Tabulator.java

Class: text-tabulation methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.text;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: TEXT-TABULATION METHODS


public class Tabulator
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Tabulator()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Result tabulate(
		int														numColumns,
		boolean[]												rightAligned,
		int[]													gaps,
		Iterator<? extends Iterator<? extends CharSequence>>	rowSource)
	{
		// Create list of rows
		List<CharSequence[]> rows = new ArrayList<>();
		while (rowSource.hasNext())
		{
			// Initialise cell values
			CharSequence[] cellValues = new CharSequence[numColumns];

			// Populate cell values
			int index = 0;
			Iterator<? extends CharSequence> it = rowSource.next();
			while ((index < numColumns) && it.hasNext())
				cellValues[index++] = it.next();

			// Add cell values to list of rows
			rows.add(cellValues);
		}

		// Tabulate rows
		return tabulate(numColumns, rightAligned, gaps, rows);
	}

	//------------------------------------------------------------------

	public static Result tabulate(
		int									numColumns,
		boolean[]							rightAligned,
		int[]								gaps,
		Iterable<? extends CharSequence[]>	rows)
	{
		// Validate arguments
		if (numColumns < 0)
			throw new IllegalArgumentException("Number of columns out of bounds: " + numColumns);
		if (rows == null)
			throw new IllegalArgumentException("Null rows");

		// Calculate maximum widths of columns
		int[] maxWidths = new int[numColumns];
		for (CharSequence[] cellValues : rows)
		{
			if (cellValues != null)
			{
				int numCellValues = Math.min(cellValues.length, numColumns);
				for (int i = 0; i < numCellValues; i++)
				{
					CharSequence cellValue = cellValues[i];
					if (cellValue != null)
					{
						int length = cellValue.length();
						if (maxWidths[i] < length)
							maxWidths[i] = length;
					}
				}
			}
		}

		// Calculate maximum number of spaces
		int maxNumSpaces = 0;
		for (int i = 0; i < numColumns; i++)
			maxNumSpaces = Math.max(maxNumSpaces, maxWidths[i]);

		// Create array of 'right aligned' flags
		boolean[] rightAligned0 = new boolean[numColumns];
		if (rightAligned != null)
			System.arraycopy(rightAligned, 0, rightAligned0, 0, Math.min(rightAligned.length, numColumns));

		// Create array of gaps between columns
		int[] gaps0 = new int[numColumns];
		Arrays.fill(gaps0, 1);
		if ((gaps != null) && (numColumns > 0))
			System.arraycopy(gaps, 0, gaps0, 1, Math.min(gaps.length, numColumns - 1));

		// Update maximum number of spaces from gaps
		for (int i = 0; i < numColumns; i++)
			maxNumSpaces = Math.max(maxNumSpaces, gaps0[i]);

		// Create string of spaces
		String spaces = " ".repeat(maxNumSpaces);

		// Initialise buffer for tabulated text
		StringBuilder buffer = new StringBuilder(1024);

		// Tabulate text
		int maxLineLength = 0;
		for (CharSequence[] cellValues : rows)
		{
			int startIndex = buffer.length();
			if (cellValues != null)
			{
				// Append cell values
				int endIndex = startIndex;
				int numCellValues = Math.min(cellValues.length, numColumns);
				for (int i = 0; i < numCellValues; i++)
				{
					// Append gap between columns
					int gap = gaps0[i];
					if ((i > 0) && (i < numCellValues) && (gap > 0))
						buffer.append(spaces, 0, gap);

					// Get cell value
					CharSequence cellValue = cellValues[i];
					if (cellValue == null)
						cellValue = "";

					// If column is right-aligned, add padding
					int padding = maxWidths[i] - cellValue.length();
					if (rightAligned0[i] && (padding > 0))
						buffer.append(spaces, 0, padding);

					// Append cell value
					buffer.append(cellValue);
					for (int j = 0; j < cellValue.length(); j++)
					{
						if (!Character.isWhitespace(cellValue.charAt(j)))
						{
							endIndex = buffer.length();
							break;
						}
					}

					// If column is left-aligned, add padding
					if (!rightAligned0[i] && (padding > 0))
						buffer.append(spaces, 0, padding);
				}

				// Remove trailing spaces
				buffer.setLength(endIndex);
			}

			// Update maximum line length
			maxLineLength = Math.max(maxLineLength, buffer.length() - startIndex);

			// Append end-of-line character
			buffer.append('\n');
		}

		// Return tabulated text and maximum line length
		return new Result(buffer.toString(), maxLineLength);
	}

	//------------------------------------------------------------------

	public static Result tabulate(
		int					numColumns,
		boolean[]			rightAligned,
		int[]				gaps,
		CharSequence[]...	rows)
	{
		return tabulate(numColumns, rightAligned, gaps, List.of(rows));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: RESULT OF TABULATION


	public record Result(
		String	text,
		int		maxLineLength)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
