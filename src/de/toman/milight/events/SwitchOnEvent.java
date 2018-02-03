package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched on.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class SwitchOnEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = 2846331650737846201L;

	/**
	 * This constructor creates a new SwitchOnEvent referencing to a group of
	 * light that was switched on.
	 * 
	 * @param lights
	 *            is the group of lights that was switched on
	 */
	public SwitchOnEvent(Lights lights) {
		super(lights);
	}
}
