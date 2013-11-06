package de.toman.milight;

import java.awt.Color;

/**
 * This class extracts hue, saturation and brightness values from java.awt.Color
 * instances and transforms them to values that can be sent to the Milight WiFi
 * box.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class MilightColor {
	/**
	 * The color the instance should represent.
	 */
	private Color color;

	/**
	 * The minimum color value to send to the WiFi box.
	 */
	public static final int MIN_COLOR = 0x00;

	/**
	 * The maximum color value to send to the WiFi box.
	 */
	public static final int MAX_COLOR = 0xFF;

	/**
	 * The minimum brightness value to send to the WiFi box.
	 * 
	 * The documentation of the
	 * "LimitlessLED Technical Developer Opensource API" mentions that
	 * brightness values should be between 0x00 and 0x3B. However, in practice
	 * this does not hold. All available brightness levels are between 0x02 and
	 * 0x1B, outside this interval some arguments don't change the light at all,
	 * others give unexpected brightness values with the same result as the
	 * values between 0x02 and 0x1B.
	 */
	public static final int MIN_BRIGHTNESS = 0x02;

	/**
	 * The maximum brightness value to send to the WiFi box.
	 * 
	 * The documentation of the
	 * "LimitlessLED Technical Developer Opensource API" mentions that
	 * brightness values should be between 0x00 and 0x3B. However, in practice
	 * this does not hold. All available brightness levels are between 0x02 and
	 * 0x1B, outside this interval some arguments don't change the light at all,
	 * others give unexpected brightness values with the same result as the
	 * values between 0x02 and 0x1B.
	 */
	public static final int MAX_BRIGHTNESS = 0x1B;

	/**
	 * This threshold defines where the lights should change between white and
	 * colored mode. All saturation values under the threshold will be displayed
	 * in white mode, everything equal or above in colored mode.
	 */
	public static final float SATURATION_THRESHOLD = 0.5f;

	/**
	 * Use this constructor to generate a new MilightColor representing a
	 * specified color.
	 * 
	 * @param color
	 *            is the colorthe new instance should represent
	 */
	public MilightColor(Color color) {
		super();
		this.color = color;
	}

	/**
	 * This function returns the color the instance is representing.
	 * 
	 * @return is the color the instance is representing
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Use this function to set the color the instance is representing.
	 * 
	 * @param color
	 *            is the color the instance should be representing
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * This function extracts hue, saturation and brightness of the color the
	 * instance is representing. The values are returned as an array of size 3,
	 * each value is in the range betwen 0 and 1.
	 * 
	 * @return is an array consisting of the hue, saturation and brightness
	 *         values (in this order). All values are given in a range between 0
	 *         and 1
	 * @see java.awt.Color#RGBtoHSB(int, int, int, float[])
	 */
	public float[] getHSB() {
		return Color.RGBtoHSB(color.getRed(), color.getGreen(),
				color.getBlue(), null);
	}

	/**
	 * Extract the hue value from the color the instance is representing.
	 * 
	 * @return the hue value in a range between 0 and 1
	 * @see java.awt.Color#RGBtoHSB(int, int, int, float[])
	 */
	public float getHue() {
		return getHSB()[0];
	}

	/**
	 * Extract the saturation value from the color the instance is representing.
	 * 
	 * @return the saturation value in a range between 0 and 1
	 * @see java.awt.Color#RGBtoHSB(int, int, int, float[])
	 */
	public float getSaturation() {
		return getHSB()[1];
	}

	/**
	 * Extract the brightness value from the color the instance is representing.
	 * 
	 * @return the brightness value in a range between 0 and 1
	 * @see java.awt.Color#RGBtoHSB(int, int, int, float[])
	 */
	public float getBrightness() {
		return getHSB()[2];
	}

	/**
	 * This function extracts the hue value from the color the instance is
	 * representing and transforms it to a value that can be sent to the WiFi
	 * box. Therefore a linear change on the scale between 0 and 1 is applied
	 * setting 0 to 2/3, 1/3 to 1/3 and 2/3 to 0. Afterwards, the value is
	 * scaled to a maximum of MilightColor.MAX_COLOR.
	 * 
	 * @return the hue value to send to the WiFi box
	 */
	public int getMilightHue() {
		// transform value by a linear change on the scale between 0 and 1
		// setting 0 to 2/3, 1/3 to 1/3 and 2/3 to 0
		float milightHue = (5 / 3f - getHue()) % 1f;

		// scale the value
		return (int) (milightHue * MAX_COLOR);
	}

	/**
	 * This function extracts the brightness value from the color the instance
	 * is representing and transforms it to a value that can be sent to the WiFi
	 * box. Therefore the value is scaled to a scale between
	 * MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS.
	 * 
	 * @return the brightness value to send to the WiFi box
	 */
	public int getMilightBrightness() {
		// scale the value
		return MIN_BRIGHTNESS
				+ (int) (getBrightness() * (MAX_BRIGHTNESS - MIN_BRIGHTNESS));
	}

	/**
	 * This function computes whether a color should be displayed in white or
	 * colored mode. The white mode will be used if the saturation of the color
	 * is below {@link MilightColor#SATURATION_THRESHOLD}.
	 * 
	 * @return true if the white mode should be used, false if the colored mode
	 *         should be used
	 */
	public boolean isWhiteMode() {
		return getSaturation() < SATURATION_THRESHOLD;
	}

	/**
	 * This function computes whether a color should be displayed in white or
	 * colored mode. The white mode will be used if the saturation of the color
	 * is below {@link MilightColor#SATURATION_THRESHOLD}.
	 * 
	 * @return true if the colored mode should be used, false if the white mode
	 *         should be used
	 */
	public boolean isColoredMode() {
		return !isWhiteMode();
	}
}
