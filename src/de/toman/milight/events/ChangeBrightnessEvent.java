package de.toman.milight.events;

import de.toman.milight.Lights;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched to another brightness level.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class ChangeBrightnessEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = 4188719481159414618L;

	/**
	 * The new brightness level (between 0 and 1) for the group of lights
	 */
	private float brightness;

	/**
	 * This constructor creates a new ChangeBrightnessEvent referencing to a
	 * group of light that was switched to a new brightness level.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to white mode
	 * @param brightness
	 *            is the new new brightness level (0 and 1)
	 * @throws IllegalArgumentException
	 *             if the new brightness level is not between 0 and 1
	 */
	public ChangeBrightnessEvent(Lights lights, float brightness) {
		// super constructor call
		super(lights);

		// check attributes
		if (0 > brightness || 1 < brightness) {
			throw new IllegalArgumentException(
					"The new brightness value should be between 0 and 1.");
		}

		// set attributes
		this.brightness = brightness;
	}

	/**
	 * This function returns the new new brightness level (between 0 and 1) for
	 * the group of lights.
	 * 
	 * @return the new new brightness level (between 0 and 1) for the group of
	 *         lights
	 */
	public float getBrightness() {
		return brightness;
	}
}
