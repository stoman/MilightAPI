package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched to colored mode.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class ColoredModeEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = -6998678404436416526L;

	/**
	 * This constructor creates a new ColoredModeEvent referencing to a group of
	 * light that was switched to colored mode.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to colored mode
	 */
	public ColoredModeEvent(Lights lights) {
		super(lights);
	}
}
