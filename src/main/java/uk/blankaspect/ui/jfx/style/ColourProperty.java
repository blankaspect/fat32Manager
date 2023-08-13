/*====================================================================*\

ColourProperty.java

Class: colour property.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssRuleSet;

import uk.blankaspect.common.tuple.StrKVPair;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

//----------------------------------------------------------------------


// CLASS: COLOUR PROPERTY


public class ColourProperty
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	FxProperty		fxProperty;
	private	String			colourKey;
	private	Color			colour;
	private	List<String>	selectors;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ColourProperty()
	{
	}

	//------------------------------------------------------------------

	private ColourProperty(
		FxProperty			fxProperty,
		String				colourKey,
		Color				colour,
		Collection<String>	selectors)
	{
		// Initialise instance variables
		this.fxProperty = fxProperty;
		this.colourKey = colourKey;
		this.colour = colour;
		this.selectors = new ArrayList<>(selectors);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Builder builder()
	{
		return new Builder();
	}

	//------------------------------------------------------------------

	public static ColourProperty of(
		FxProperty			fxProperty,
		String				colourKey,
		Collection<String>	selectors)
	{
		// Validate arguments
		if (fxProperty == null)
			throw new IllegalArgumentException("Null FX property");
		if (colourKey == null)
			throw new IllegalArgumentException("Null colour key");
		if (selectors == null)
			throw new IllegalArgumentException("Null selectors");
		if (selectors.isEmpty())
			throw new IllegalArgumentException("No selectors");

		// Create property and return it
		return new ColourProperty(fxProperty, colourKey, null, selectors);
	}

	//------------------------------------------------------------------

	public static ColourProperty of(
		FxProperty	fxProperty,
		String		colourKey,
		String...	selectors)
	{
		// Validate arguments
		if (fxProperty == null)
			throw new IllegalArgumentException("Null FX property");
		if (colourKey == null)
			throw new IllegalArgumentException("Null colour key");
		if (selectors == null)
			throw new IllegalArgumentException("Null selectors");
		if (selectors.length == 0)
			throw new IllegalArgumentException("No selectors");

		// Create property and return it
		return new ColourProperty(fxProperty, colourKey, null, Arrays.asList(selectors));
	}

	//------------------------------------------------------------------

	public static ColourProperty of(
		FxProperty			fxProperty,
		Color				colour,
		Collection<String>	selectors)
	{
		// Validate arguments
		if (fxProperty == null)
			throw new IllegalArgumentException("Null FX property");
		if (colour == null)
			throw new IllegalArgumentException("Null colour");
		if (selectors == null)
			throw new IllegalArgumentException("Null selectors");
		if (selectors.isEmpty())
			throw new IllegalArgumentException("No selectors");

		// Create property and return it
		return new ColourProperty(fxProperty, null, colour, selectors);
	}

	//------------------------------------------------------------------

	public static ColourProperty of(
		FxProperty	fxProperty,
		Color		colour,
		String...	selectors)
	{
		// Validate arguments
		if (fxProperty == null)
			throw new IllegalArgumentException("Null FX property");
		if (colour == null)
			throw new IllegalArgumentException("Null colour");
		if (selectors == null)
			throw new IllegalArgumentException("Null selectors");
		if (selectors.length == 0)
			throw new IllegalArgumentException("No selectors");

		// Create property and return it
		return new ColourProperty(fxProperty, null, colour, Arrays.asList(selectors));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof ColourProperty other)
				&& (fxProperty == other.fxProperty)
				&& Objects.equals(colourKey, other.colourKey)
				&& Objects.equals(colour, other.colour)
				&& selectors.equals(other.selectors);
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		int code = fxProperty.hashCode();
		code = code * 31 + Objects.hashCode(colourKey);
		code = code * 31 + Objects.hashCode(colour);
		code = selectors.hashCode();
		return code;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public FxProperty getFxProperty()
	{
		return fxProperty;
	}

	//------------------------------------------------------------------

	public String getColourKey()
	{
		return colourKey;
	}

	//------------------------------------------------------------------

	public Color getColour()
	{
		return colour;
	}

	//------------------------------------------------------------------

	public List<String> getSelectors()
	{
		return Collections.unmodifiableList(selectors);
	}

	//------------------------------------------------------------------

	public CssRuleSet toRuleSet(
		AbstractTheme	theme)
	{
		Color colour0 = (colour == null)
								? (colourKey == null)
										? null
										: theme.getColour(colourKey)
								: colour;
		return (colour0 == null)
					? null
					: CssRuleSet.of(selectors, StrKVPair.of(fxProperty.getName(), ColourUtils.colourToCssRgbaString(colour0)));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: COLOUR-PROPERTY BUILDER


	public static class Builder
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	ColourProperty	property;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Builder()
		{
			// Initialise instance variables
			property = new ColourProperty();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Builder fxProperty(
			FxProperty	fxProperty)
		{
			// Validate argument
			if (fxProperty == null)
				throw new IllegalArgumentException("Null FX Property");

			// Set FX property
			property.fxProperty = fxProperty;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		public Builder colourKey(
			String	colourKey)
		{
			// Validate argument
			if (colourKey == null)
				throw new IllegalArgumentException("Null colour key");

			// Set colour key
			property.colourKey = colourKey;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		public Builder colour(
			Color	colour)
		{
			// Validate argument
			if (colour == null)
				throw new IllegalArgumentException("Null colour");

			// Set colour
			property.colour = colour;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		public Builder selectors(
			String...	selectors)
		{
			// Validate argument
			if (selectors == null)
				throw new IllegalArgumentException("Null selectors");

			// Add selectors to list
			Collections.addAll(property.selectors, selectors);

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		public Builder selectors(
			Collection<String>	selectors)
		{
			// Validate argument
			if (selectors == null)
				throw new IllegalArgumentException("Null selectors");

			// Add selectors to list
			property.selectors.addAll(selectors);

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		public ColourProperty build()
		{
			return property;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
