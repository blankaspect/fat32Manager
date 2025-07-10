/*====================================================================*\

CompoundPathnameLabel.java

Class: compound pathname label.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.label;

//----------------------------------------------------------------------


// IMPORTS


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.scene.text.Font;

import uk.blankaspect.common.message.MessageConstants;

//----------------------------------------------------------------------


// CLASS: COMPOUND PATHNAME LABEL


/**
 * This class implements a compound label comprising two horizontally adjacent labels: a <i>prefix</i> label and a
 * <i>pathname</i> label.  The latter is intended to represent a file-system location, and any truncation of a pathname
 * whose width exceeds the width of the label occurs in the centre of the pathname.
 * <p>
 * The text of the two component labels cannot be set separately: the combined text that is set on the compound label is
 * split into a prefix and pathname at the first occurrence of a configurable {@linkplain #setSeparator(String) regular
 * expression}.
 * </p>
 */

public class CompoundPathnameLabel
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default value of the regular expression that is used to split the text of a label into a prefix and a
		pathname. */
	public static final		String	DEFAULT_SEPARATOR	= MessageConstants.SEPARATOR;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The regular expression that is used to split the text of this label into a prefix and a pathname. */
	private	String					separator;

	/** The combined text of this label. */
	private	SimpleStringProperty	text;

	/** The label of the prefix component. */
	private	Label					prefixLabel;

	/** The label of the pathname component. */
	private	Label					pathnameLabel;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an empty compound pathname label.
	 */

	public CompoundPathnameLabel()
	{
		// Call alternative constructor
		this(null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a compound pathname label with the specified combined text.
	 *
	 * @param text
	 *          the combined text of the compound label.
	 */

	public CompoundPathnameLabel(
		String	text)
	{
		// Initialise instance variables
		separator = DEFAULT_SEPARATOR;
		this.text = new SimpleStringProperty(text);

		// Create prefix label
		prefixLabel = new Label();
		prefixLabel.setMinWidth(Region.USE_PREF_SIZE);

		// Create pathname label
		pathnameLabel = new Label();
		pathnameLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
		HBox.setHgrow(pathnameLabel, Priority.ALWAYS);

		// Add labels to this container
		getChildren().addAll(prefixLabel, pathnameLabel);

		// Update prefix and pathname when text changes
		this.text.addListener((observable, oldText, newText) -> updateLabels(newText));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the label of the prefix component.
	 *
	 * @return the label of the prefix component.
	 */

	public Label getPrefixLabel()
	{
		return prefixLabel;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the label of the pathname component.
	 *
	 * @return the label of the pathname component.
	 */

	public Label getPathnameLabel()
	{
		return pathnameLabel;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the combined text of this compound label.
	 *
	 * @return the combined text of this compound label.
	 */

	public String getText()
	{
		return text.get();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the combined text of this compound label.  The text is split into a prefix and pathname at the first
	 * occurrence of the {@linkplain #setSeparator(String) separator}.
	 *
	 * @param text
	 *          the value to which the combined text of this compound label will be set.
	 */

	public void setText(
		String	text)
	{
		this.text.set(text);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the combined text of this compound label.
	 *
	 * @return the combined text of this compound label.
	 */

	public StringProperty textProperty()
	{
		return text;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the regular expression that is used to split the text of this label into a prefix and a pathname.
	 *
	 * @return the regular expression that is used to split the text of this label into a prefix and a pathname.
	 */

	public String getSeparator()
	{
		return separator;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the regular expression that is used to split the text of this label into a prefix and a pathname.
	 *
	 * @param  separator
	 *           the regular expression that will be used to split the text of this label into a prefix and a pathname.
	 * @throws IllegalArgumentException
	 *           if {@code separator} is {@code null}.
	 */

	public void setSeparator(
		String	separator)
	{
		// Validate argument
		if (separator == null)
			throw new IllegalArgumentException("Null separator");

		// Update instance variable
		this.separator = separator;

		// Update labels
		updateLabels(text.get());
	}

	//------------------------------------------------------------------

	/**
	 * Sets the fonts of the component labels to the specified value.
	 *
	 * @param font
	 *          the value to which the fonts of the component labels will be set.
	 */

	public void setFont(
		Font	font)
	{
		prefixLabel.setFont(font);
		pathnameLabel.setFont(font);
	}

	//------------------------------------------------------------------

	/**
	 * Updates the prefix and pathname labels with the specified combined text.
	 *
	 * @param text
	 *          the combined text that will be split into a prefix and pathname to update the respective labels.
	 */

	private void updateLabels(
		String	text)
	{
		if (text == null)
		{
			prefixLabel.setText(null);
			pathnameLabel.setText(null);
		}
		else
		{
			String[] strs = text.split(separator, 2);
			prefixLabel.setText(strs[0]);
			pathnameLabel.setText((strs.length < 2) ? null : strs[1]);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
