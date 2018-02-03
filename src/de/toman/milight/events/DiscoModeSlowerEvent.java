package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when speed of the
 * group of lights' disco mode is decreased.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class DiscoModeSlowerEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = -7791981405252829327L;

	/**
	 * This constructor creates a new DiscoModeFasterEvent referencing to a
	 * group of light that was switched to a slower disco mode.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to a slower disco
	 *            mode
	 */
	public DiscoModeSlowerEvent(Lights lights) {
		super(lights);
	}
}
