/*====================================================================*\

LocationChooserEvent.java

Class: location-chooser event.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.locationchooser;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventType;

//----------------------------------------------------------------------


// CLASS: LOCATION-CHOOSER EVENT


/**
 * This class implements an {@linkplain Event event} that relates to a location chooser.
 */

public class LocationChooserEvent
	extends Event
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** An event that signals that the user has chosen one or more locations in a location chooser. */
	public static final	EventType<LocationChooserEvent>	LOCATIONS_CHOSEN	= new EventType<>("LOCATIONS_CHOSEN");

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A list of the file-system locations that are associated with this event. */
	private	List<Path>	locations;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an event of the specified type and for the specified source.
	 *
	 * @param eventType
	 *          the type of the event.
	 * @param source
	 *          the source of the event.
	 * @param locations
	 *          the file-system locations that will be associated with the event, which may be {@code null}.
	 */

	public LocationChooserEvent(
		EventType<LocationChooserEvent>	eventType,
		LocationChooserPane				source,
		Collection<Path>				locations)
	{
		// Call superclass constructor
		super(source, null, eventType);

		// Initialise instance variables
		this.locations = (locations == null) ? Collections.emptyList() : new ArrayList<>(locations);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public LocationChooserPane getSource()
	{
		return (LocationChooserPane)source;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an unmodifiable list of the file-system locations that are associated with this event.
	 * @return an unmodifiable list of the file-system locations that are associated with this event.
	 */

	public List<Path> getLocations()
	{
		return Collections.unmodifiableList(locations);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
