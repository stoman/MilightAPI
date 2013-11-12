package de.toman.milight;

public class LightState {
	/**
	 * The color which is currently display by the group of lights if the lights
	 * are in colored mode. Otherwise, this is the color used last when the
	 * colored mode was activated. Hue and brightness will be extracted from
	 * here.
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
}
