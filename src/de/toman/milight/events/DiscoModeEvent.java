package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched to disco mode (or next type of disco mode).
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class DiscoModeEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = -8210409878263179107L;

	/**
	 * This constructor creates a new DiscoModeEvent referencing to a group of
	 * light that was switched to disco mode (or next type of disco mode).
	 * 
	 * @param lights
	 *            is the group of lights that was switched to disco mode (or
	 *            next type of disco mode)
	 */
	public DiscoModeEvent(Lights lights) {
		super(lights);
	}
}
