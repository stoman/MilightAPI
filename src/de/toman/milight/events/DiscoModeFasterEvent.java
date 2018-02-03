package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when speed of the
 * group of lights' disco mode is increased.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class DiscoModeFasterEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = -6220516684279513834L;

	/**
	 * This constructor creates a new DiscoModeFasterEvent referencing to a
	 * group of light that was switched to a faster disco mode.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to a faster disco
	 *            mode
	 */
	public DiscoModeFasterEvent(Lights lights) {
		super(lights);
	}
}
