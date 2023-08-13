/*====================================================================*\

ChangeNotifier.java

Class: change notifier.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.observer;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

//----------------------------------------------------------------------


// CLASS: CHANGE NOTIFIER


/**
 * This class provides a means of notifying registered listeners that an associated state has changed.
 *
 * @param <T>
 *         the type of the values of the properties that may be set on a notifier.
 */

public class ChangeNotifier<T>
	implements Observable
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A map of the properties of this notifier. */
	private	Map<String, T>				properties;

	/** A list of the listeners to this notifier. */
	private	List<InvalidationListener>	listeners;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a change notifier.
	 */

	public ChangeNotifier()
	{
		// Initialise instance variables
		listeners = new ArrayList<>();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a change notifier and adds the specified key&ndash;value pair to its map of properties.
	 *
	 * @param key
	 *          the key of the key&ndash;value pair that will be added to the notifier's map of properties.
	 * @param value
	 *          the value of the key&ndash;value pair that will be added to the notifier's map of properties.
	 */

	public ChangeNotifier(
		String	key,
		T		value)
	{
		// Call alternative constructor
		this();

		// Add property to map
		addProperty(key, value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Observable interface
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void addListener(
		InvalidationListener	listener)
	{
		addListener(listener, false);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void removeListener(
		InvalidationListener	listener)
	{
		removeListener(listener, false);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the map of properties contains an entry with the specified key.
	 *
	 * @param  key
	 *           the key of interest.
	 * @return {@code true} if the map of properties contains an entry whose key is <i>key</i>.
	 */

	public boolean hasProperty(
		String	key)
	{
		return (properties != null) && properties.containsKey(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value that is associated with the specified key in the map of properties.
	 *
	 * @param  key
	 *           the key whose associated value is required.
	 * @return the value that is associated with <i>key</i> in the map of properties, or {@code null} if there is no
	 *         such property.
	 */

	public T getPropertyValue(
		String	key)
	{
		return (properties == null) ? null : properties.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Adds an entry with the specified key and value to the map of properties.  If the map already contains an entry
	 * with the specified key, the value of the entry will be replaced by the specified value.
	 *
	 * @param key
	 *          the key of the key&ndash;value pair that will be added to the map of properties.
	 * @param value
	 *          the value of the key&ndash;value pair that will be added to the map of properties.
	 */

	public void addProperty(
		String	key,
		T		value)
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException("Null key");

		// Create map of properties if necessary
		if (properties == null)
			properties = new HashMap<>();

		// Add entry to map
		properties.put(key, value);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the entry with the specified key from the map of properties, if the map contains such a property.
	 *
	 * @param key
	 *          the key whose entry will be removed from the map of properties.
	 */

	public void removeProperty(
		String	key)
	{
		if (properties != null)
			properties.remove(key);
	}

	//------------------------------------------------------------------

	/**
	 * Notifies all listeners that the state that is associated with this notifier has changed.
	 */

	public void notifyChange()
	{
		// Create copy of list of listeners
		List<InvalidationListener> listeners0 = new ArrayList<>(listeners);

		// Notify listeners
		for (int i = listeners0.size() - 1; i >= 0; i--)
			listeners0.get(i).invalidated(this);
	}

	//------------------------------------------------------------------

	/**
	 * Adds an entry with the specified key and value to the map of properties and notifies all listeners that the state
	 * that is associated with this notifier has changed.  If the map already contains an entry with the specified key,
	 * the value of the entry will be replaced by the specified value.
	 *
	 * @param key
	 *          the key of the key&ndash;value pair that will be added to the map of properties.
	 * @param value
	 *          the value of the key&ndash;value pair that will be added to the map of properties.
	 */

	public void notifyChange(
		String	key,
		T		value)
	{
		// Add property to map
		addProperty(key, value);

		// Create copy of list of listeners
		List<InvalidationListener> listeners0 = new ArrayList<>(listeners);

		// Notify listeners
		for (int i = listeners0.size() - 1; i >= 0; i--)
			listeners0.get(i).invalidated(this);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified listener to this notifier.
	 *
	 * @param listener
	 *          the listener that will be added.
	 * @param notifyListener
	 *          if {@code true}, {@code listener} will be invalidated after it is added.
	 */

	public void addListener(
		InvalidationListener	listener,
		boolean					notifyListener)
	{
		// Add listener
		listeners.add(listener);

		// Notify listener
		if (notifyListener)
			listener.invalidated(this);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the specified listener from this notifier.
	 *
	 * @param listener
	 *          the listener that will be removed.
	 * @param notifyListener
	 *          if {@code true}, {@code listener} will be invalidated after it is removed.
	 */

	public void removeListener(
		InvalidationListener	listener,
		boolean					notifyListener)
	{
		// Remove listener
		listeners.remove(listener);

		// Notify listener
		if (notifyListener)
			listener.invalidated(this);
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the listeners from this notifier.
	 */

	public void clearListeners()
	{
		clearListeners(false);
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the listeners from this notifier.
	 *
	 * @param notifyListeners
	 *          if {@code true}, the removed listeners will be invalidated.
	 */

	public void clearListeners(
		boolean	notifyListeners)
	{
		// Create list of old listeners
		List<InvalidationListener> listeners0 = new ArrayList<>(listeners);

		// Remove all listeners
		listeners.clear();

		// Notify old listeners
		if (notifyListeners)
		{
			for (int i = listeners0.size() - 1; i >= 0; i--)
				listeners0.get(i).invalidated(this);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
