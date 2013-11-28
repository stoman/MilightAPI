package de.toman.milight;

import java.awt.Color;
import java.io.IOException;

public class LightState {
	/**
	 * The color which is currently display by the group of lights if the lights
	 * are in colored mode. Otherwise, this is the color used last when the
	 * colored mode was activated.
	 */
	private MilightColor color;

	/**
	 * True if the group of lights is in white mode, false if it uses the
	 * colored mode.
	 */
	private boolean whiteMode;

	/**
	 * The brightness level between 0 and 1 currently displayed by the group of
	 * lights if they are in white mode. Otherwise, it is the brightness level
	 * used last when the lights were in white mode. The brightness level for
	 * the colored mode is not contained in this variable but at
	 * {@link LightState#color}.
	 */
	private float brightness;

	/**
	 * This variable stores whther the group of lights is currently switched on.
	 */
	private boolean on;

	/**
	 * This constructor creates a new LightState instance which stores the state
	 * of a group of lights.
	 * 
	 * @param color
	 *            is the color last displayed in colored mode
	 * @param brightness
	 *            is the brightness value last displayed in white mode (between
	 *            0 and 1)
	 * @param whiteMode
	 *            is true if the lights are currently in white mode and false if
	 *            they are in colored mode
	 * @param on
	 *            is true if the group of lights is switched on
	 * 
	 * @throws IllegalArgumentException
	 *             if the brightness value is not between 0 and 1
	 */
	public LightState(MilightColor color, float brightness, boolean whiteMode,
			boolean on) {
		// super constructor
		super();

		// check arguments
		if (0 > brightness || brightness > 1) {
			throw new IllegalArgumentException(
					"The brightness value should be between 0 and 1");
		}

		// set attributes
		this.color = color;
		this.brightness = brightness;
		this.whiteMode = whiteMode;
		this.on = on;
	}

	/**
	 * This function returns the color which is currently display by the group
	 * of lights if the lights are in colored mode. Otherwise, this is the color
	 * used last when the colored mode was activated
	 * 
	 * @return the color displayed by the lights
	 * @see LightState#isWhiteMode()
	 */
	public MilightColor getColor() {
		return color;
	}

	/**
	 * This function returns whether the group of lights is in white mode.
	 * 
	 * @return true if the group of lights is in white mode, false if it uses
	 *         the colored mode
	 */
	public boolean isWhiteMode() {
		return whiteMode;
	}

	/**
	 * This function returns the brightness level between 0 and 1 currently
	 * displayed by the group of lights if they are in white mode. Otherwise, it
	 * is the brightness level used last when the lights were in white mode.
	 * 
	 * @return the brightness level between 0 and 1
	 * @see LightState#isWhiteMode()
	 */
	public float getBrightness() {
		return brightness;
	}

	/**
	 * This function returns whether the group of lights is switched on.
	 * 
	 * @return true if the group of lights is switched on
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * This function checks whether two LightStates represent the same state.
	 * 
	 * @param state
	 *            the state to compare to
	 * @return true if both LightStates represent the same state, false
	 *         otherwise
	 */
	public boolean equals(LightState state) {
		return color.equals(state.color) && whiteMode == state.whiteMode
				&& brightness == state.brightness && on == state.on;
	}

	/**
	 * This function restores the state represented by this instance to a group
	 * of lights. Depending on the state, the group of lights is switched to
	 * colored resp. white mode and brightness/hue are adjusted.
	 * 
	 * @param lights
	 *            is the group of lights to be adjusted
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void restore(Lights lights) throws IOException {
		// white mode
		if (whiteMode) {
			// create color
			MilightColor whiteColor = new MilightColor(Color.WHITE);
			whiteColor.setBrightness(brightness);

			// set color
			lights.white();
			lights.brightness(whiteColor.getMilightBrightness());
		}
		// colored mode
		else {
			lights.colorAndBrightness(color, true);
		}
	}
}
