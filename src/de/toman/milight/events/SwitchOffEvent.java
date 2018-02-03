package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched off.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class SwitchOffEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = 5269730863079157980L;

	/**
	 * This constructor creates a new SwitchOffEvent referencing to a group of
	 * light that was switched off.
	 * 
	 * @param lights
	 *            is the group of lights that was switched off
	 */
	public SwitchOffEvent(Lights lights) {
		super(lights);
	}
}
