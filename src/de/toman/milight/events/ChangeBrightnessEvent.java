package de.toman.milight.events;

import de.toman.milight.Lights;
import de.toman.milight.MilightColor;

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
	 * The new brightness level (between MiLightColor.MIN_BRIGHTNESS and
	 * MiLightColor.MAX_BRIGHTNESS) for the group of lights
	 */
	private int brightness;

	/**
	 * This constructor creates a new ChangeBrightnessEvent referencing to a
	 * group of light that was switched to a new brightness level.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to white mode
	 * @param brightness
	 *            is the new new brightness level (between
	 *            MiLightColor.MIN_BRIGHTNESS and MiLightColor.MAX_BRIGHTNESS)
	 * @throws IllegalArgumentException
	 *             if the new brightness level is not between
	 *             MiLightColor.MIN_BRIGHTNESS and MiLightColor.MAX_BRIGHTNESS
	 */
	public ChangeBrightnessEvent(Lights lights, int brightness) {
		// super constructor call
		super(lights);

		// check attributes
		if (MilightColor.MIN_BRIGHTNESS > brightness
				|| MilightColor.MAX_BRIGHTNESS < brightness) {
			throw new IllegalArgumentException(
					"The new brightness value should be between MiLightColor.MIN_BRIGHTNESS and MiLightColor.MAX_BRIGHTNESS.");
		}

		// set attributes
		this.brightness = brightness;
	}

	/**
	 * This function returns the new new brightness level (between
	 * MiLightColor.MIN_BRIGHTNESS and MiLightColor.MAX_BRIGHTNESS) for the
	 * group of lights.
	 * 
	 * @return the new new brightness level (between MiLightColor.MIN_BRIGHTNESS
	 *         and MiLightColor.MAX_BRIGHTNESS) for the group of lights
	 */
	public int getBrightness() {
		return brightness;
	}
}
