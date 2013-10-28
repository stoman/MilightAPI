package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched to white mode.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class WhiteModeEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = 8661165301152444406L;

	/**
	 * This constructor creates a new WhiteModeEvent referencing to a group of
	 * light that was switched to white mode.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to white mode
	 */
	public WhiteModeEvent(Lights lights) {
		super(lights);
	}
}
