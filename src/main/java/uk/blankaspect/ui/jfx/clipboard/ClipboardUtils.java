/*====================================================================*\

ClipboardUtils.java

Class: clipboard-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.clipboard;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.function.Predicate;

import javafx.scene.image.Image;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import uk.blankaspect.common.collection.CollectionUtils;

import uk.blankaspect.common.exception2.BaseException;

//----------------------------------------------------------------------


// CLASS: CLIPBOARD-RELATED UTILITY METHODS


/**
 * This class contains utility methods that are related to the JavaFX {@linkplain Clipboard clipboard}.
 */

public class ClipboardUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_COPY_TO_SYSTEM_CLIPBOARD	= "Failed to copy to the system clipboard.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ClipboardUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the system clipboard contains file-system locations.
	 *
	 * @return {@code true} if the system clipboard contains file-system locations.
	 */

	public static boolean hasLocations()
	{
		return hasLocations(Clipboard.getSystemClipboard());
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified clipboard contains file-system locations.
	 *
	 * @param  clipboard
	 *           the clipboard of interest.
	 * @return {@code true} if {@code clipboard} contains file-system locations.
	 */

	public static boolean hasLocations(
		Clipboard	clipboard)
	{
		return !CollectionUtils.isNullOrEmpty(clipboard.getFiles());
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of file-system locations from the system clipboard.
	 *
	 * @return a list of file-system locations from the system clipboard.
	 */

	public static List<Path> locations()
	{
		return locations(Clipboard.getSystemClipboard());
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of file-system locations from the specified clipboard.
	 *
	 * @param  clipboard
	 *           the clipboard from which a list of file-system locations will be returned.
	 * @return a list of file-system locations from {@code clipboard}.
	 */

	public static List<Path> locations(
		Clipboard	clipboard)
	{
		List<File> files = clipboard.getFiles();
		return (files == null) ? Collections.emptyList() : files.stream().map(File::toPath).toList();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the first file-system location from the specified clipboard.
	 *
	 * @param  clipboard
	 *           the clipboard from which the first file-system location will be returned.
	 * @return the first file-system location from {@code clipboard}, or {@code null} if there was no such location.
	 */

	public static Path firstLocation(
		Clipboard	clipboard)
	{
		List<File> files = clipboard.getFiles();
		return (files == null)
						? null
						: files.stream()
								.map(File::toPath)
								.findFirst()
								.orElse(null);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if any of the file-system locations from the specified clipboard matches the specified
	 * function.
	 *
	 * @param  clipboard
	 *           the clipboard whose file-system locations will be tested against {@code matcher}.
	 * @param  matcher
	 *           the function that will be applied to the file-system locations from {@code clipboard}.
	 * @return {@code true} if any of the file-system locations from {@code clipboard} matches {@code matcher}.
	 */

	public static boolean locationMatches(
		Clipboard		clipboard,
		Predicate<Path>	matcher)
	{
		List<File> files = clipboard.getFiles();
		return (files == null) ? false : files.stream().map(File::toPath).anyMatch(matcher);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the file-system locations from the specified clipboard that match the specified function.
	 *
	 * @param  clipboard
	 *           the clipboard whose file-system locations will be tested against {@code matcher}.
	 * @param  matcher
	 *           the function that will be applied to the file-system locations from {@code clipboard}.  If it is {@code
	 *           null}, all locations will match.
	 * @return a list of the file-system locations from {@code clipboard} that match {@code matcher}.
	 */

	public static List<Path> matchingLocations(
		Clipboard		clipboard,
		Predicate<Path>	matcher)
	{
		// Replace null matcher with 'match all'
		if (matcher == null)
			matcher = location -> true;

		// Create and return list of matching locations from clipboard
		List<File> files = clipboard.getFiles();
		return (files == null) ? Collections.emptyList()
							   : files.stream().map(File::toPath).filter(matcher).toList();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the first file-system location from the specified clipboard that matches the specified function.
	 *
	 * @param  clipboard
	 *           the clipboard whose file-system locations will be tested against {@code matcher}.
	 * @param  matcher
	 *           the function that will be applied to the file-system locations from {@code clipboard}.  If it is {@code
	 *           null}, all locations will match.
	 * @return the first file-system location from {@code clipboard} that matches {@code matcher}, or {@code null} if
	 *         there was no such location.
	 */

	public static Path firstMatchingLocation(
		Clipboard		clipboard,
		Predicate<Path>	matcher)
	{
		// Replace null matcher with 'match all'
		if (matcher == null)
			matcher = location -> true;

		// Return first matching location from clipboard
		List<File> files = clipboard.getFiles();
		return (files == null) ? null
							   : files.stream().map(File::toPath).filter(matcher).findFirst().orElse(null);
	}

	//------------------------------------------------------------------

	/**
	 * Puts the specified text onto the system clipboard.
	 *
	 * @param  text
	 *           the text that will be put onto the system clipboard.
	 * @return {@code true} if {@code text} was successfully put onto the system clipboard.
	 */

	public static boolean putText(
		String	text)
	{
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		return Clipboard.getSystemClipboard().setContent(content);
	}

	//------------------------------------------------------------------

	/**
	 * Attempts to put the specified text onto the system clipboard, and throws an exception if the attempt fails.
	 *
	 * @param  text
	 *           the text that will be put onto the system clipboard.
	 * @throws BaseException
	 *           if the attempt to put {@code text} onto the system clipboard failed.
	 */

	public static void putTextThrow(
		String	text)
		throws BaseException
	{
		if (!putText(text))
			throw new BaseException(ErrorMsg.FAILED_TO_COPY_TO_SYSTEM_CLIPBOARD);
	}

	//------------------------------------------------------------------

	/**
	 * Puts the specified image onto the system clipboard.
	 *
	 * @param  image
	 *           the image that will be put onto the system clipboard.
	 * @return {@code true} if {@code image} was successfully put onto the system clipboard.
	 */

	public static boolean putImage(
		Image	image)
	{
		ClipboardContent content = new ClipboardContent();
		content.putImage(image);
		return Clipboard.getSystemClipboard().setContent(content);
	}

	//------------------------------------------------------------------

	/**
	 * Attempts to put the specified image onto the system clipboard, and throws an exception if the attempt fails.
	 *
	 * @param  image
	 *           the image that will be put onto the system clipboard.
	 * @throws BaseException
	 *           if the attempt to put {@code image} onto the system clipboard failed.
	 */

	public static void putImageThrow(
		Image	image)
		throws BaseException
	{
		if (!putImage(image))
			throw new BaseException(ErrorMsg.FAILED_TO_COPY_TO_SYSTEM_CLIPBOARD);
	}

	//------------------------------------------------------------------

	/**
	 * Puts the specified file-system locations onto the system clipboard.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @return {@code true} if {@code locations} were successfully put onto the system clipboard.
	 */

	public static boolean putLocations(
		Iterable<? extends Path>	locations)
	{
		// Convert locations
		List<File> locations0 = new ArrayList<>();
		for (Path location : locations)
			locations0.add(location.toFile());

		// Put locations on clipboard
		ClipboardContent content = new ClipboardContent();
		content.putFiles(locations0);
		return Clipboard.getSystemClipboard().setContent(content);
	}

	//------------------------------------------------------------------

	/**
	 * Puts the specified file-system locations onto the system clipboard.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @return {@code true} if {@code locations} were successfully put onto the system clipboard.
	 */

	public static boolean putLocations(
		Path...	locations)
	{
		return putLocations(Arrays.asList(locations));
	}

	//------------------------------------------------------------------

	/**
	 * Attempts to put the specified file-system locations onto the system clipboard, and throws an exception if the
	 * attempt fails.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @throws BaseException
	 *           if the attempt to put {@code locations} onto the system clipboard failed.
	 */

	public static void putLocationsThrow(
		Iterable<? extends Path>	locations)
		throws BaseException
	{
		if (!putLocations(locations))
			throw new BaseException(ErrorMsg.FAILED_TO_COPY_TO_SYSTEM_CLIPBOARD);
	}

	//------------------------------------------------------------------

	/**
	 * Attempts to put the specified file-system locations onto the system clipboard, and throws an exception if the
	 * attempt fails.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @throws BaseException
	 *           if the attempt to put {@code locations} onto the system clipboard failed.
	 */

	public static void putLocationsThrow(
		Path...	locations)
		throws BaseException
	{
		putLocationsThrow(Arrays.asList(locations));
	}

	//------------------------------------------------------------------

	/**
	 * Puts the specified file-system locations onto the system clipboard as locations and text.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @return {@code true} if {@code locations} were successfully put onto the system clipboard as locations and text.
	 */

	public static boolean putLocationsAndText(
		Iterable<? extends Path>	locations)
	{
		// Convert locations
		List<File> locations0 = new ArrayList<>();
		for (Path location : locations)
			locations0.add(location.toFile());

		// Initialise clipboard content
		ClipboardContent content = new ClipboardContent();

		// Put locations on clipboard
		content.putFiles(locations0);

		// Convert locations to text
		int numLocations = locations0.size();
		StringBuilder buffer = new StringBuilder(numLocations * 128);
		for (Path location : locations)
		{
			buffer.append(location);
			if (numLocations > 1)
				buffer.append('\n');
		}

		// Put text on clipboard
		content.putString(buffer.toString());

		// Set content on clipboard and return result
		return Clipboard.getSystemClipboard().setContent(content);
	}

	//------------------------------------------------------------------

	/**
	 * Puts the specified file-system locations onto the system clipboard as locations and text.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @return {@code true} if {@code locations} were successfully put onto the system clipboard as locations and text.
	 */

	public static boolean putLocationsAndText(
		Path...	locations)
	{
		return putLocationsAndText(Arrays.asList(locations));
	}

	//------------------------------------------------------------------

	/**
	 * Attempts to put the specified file-system locations onto the system clipboard as locations and text, and throws
	 * an exception if the attempt fails.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @throws BaseException
	 *           if the attempt to put {@code locations} onto the system clipboard as locations and text failed.
	 */

	public static void putLocationsAndTextThrow(
		Iterable<? extends Path>	locations)
		throws BaseException
	{
		if (!putLocationsAndText(locations))
			throw new BaseException(ErrorMsg.FAILED_TO_COPY_TO_SYSTEM_CLIPBOARD);
	}

	//------------------------------------------------------------------

	/**
	 * Attempts to put the specified file-system locations onto the system clipboard as locations and text, and throws
	 * an exception if the attempt fails.
	 *
	 * @param  locations
	 *           the file-system locations that will be put onto the system clipboard.
	 * @throws BaseException
	 *           if the attempt to put {@code locations} onto the system clipboard as locations and text failed.
	 */

	public static void putLocationsAndTextThrow(
		Path...	locations)
		throws BaseException
	{
		putLocationsAndTextThrow(Arrays.asList(locations));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
