package de.toman.milight;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class represents a group of LED light bulbs that are connected to a WiFi
 * box using the same group. These groups can be controlled individually, it is
 * also possible to mix groups belonging to different WiFi boxes.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class Lights {
	/**
	 * The WiFi box controlling this group of lights.
	 */
	private WiFiBox wifiBox;

	/**
	 * The number of the group at the WiFi box (between 1 and 4).
	 */
	private int group;

	/**
	 * This constructor creates a new group of lights for a given WiFi box.
	 * 
	 * @param wifiBox
	 *            is the box controlling the group of lights
	 * @param group
	 *            is the number of the group at the WiFi box (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	public Lights(WiFiBox wifiBox, int group) throws IllegalArgumentException {
		super();
		this.wifiBox = wifiBox;
		setGroup(group);
	}

	/**
	 * This constructor creates a new group of lights and a connection to a WiFi
	 * box. If there is already a group of lights and you need to control
	 * another group connected to the same WiFi box then use
	 * light.getWiFiBox().getLights(group) instead to resuse the connection.
	 * 
	 * @param address
	 *            is the address of the WiFi box
	 * @param port
	 *            is the port of the WiFi box (omit this if unsure)
	 * @param group
	 *            is the number of the group at the WiFi box (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @see Lights#getWifiBox() to get the WiFi box connected to group of lights
	 * @see WiFiBox#getLights(int) to get another group of lights for the same
	 *      WiFi box
	 */
	public Lights(InetAddress address, int port, int group) {
		super();
		wifiBox = new WiFiBox(address, port);
		setGroup(group);
	}

	/**
	 * This constructor creates a new group of lights and a connection to a WiFi
	 * box using the default port. If there is already a group of lights and you
	 * need to control another group connected to the same WiFi box then use
	 * light.getWiFiBox().getLights(group) instead to resuse the connection.
	 * 
	 * @param address
	 *            is the address of the WiFi box listening on the default port
	 * @param group
	 *            is the number of the group at the WiFi box (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @see Lights#getWifiBox() to get the WiFi box connected to group of lights
	 * @see WiFiBox#getLights(int) to get another group of lights for the same
	 *      WiFi box
	 */
	public Lights(InetAddress address, int group) {
		super();
		wifiBox = new WiFiBox(address);
		setGroup(group);
	}

	/**
	 * This constructor creates a new group of lights and a connection to a WiFi
	 * box. If there is already a group of lights and you need to control
	 * another group connected to the same WiFi box then use
	 * light.getWiFiBox().getLights(group) instead to resuse the connection.
	 * 
	 * @param host
	 *            is the host given as hostname such as "domain.tld" or string
	 *            repesentation of an ip address
	 * @param port
	 *            is the port of the WiFi box (omit this if unsure)
	 * @param group
	 *            is the number of the group at the WiFi box (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @throws UnknownHostException
	 *             if the hostname could not be resolved
	 * @see Lights#getWifiBox() to get the WiFi box connected to group of lights
	 * @see WiFiBox#getLights(int) to get another group of lights for the same
	 *      WiFi box
	 */
	public Lights(String host, int port, int group) throws UnknownHostException {
		super();
		wifiBox = new WiFiBox(host, port);
		setGroup(group);
	}

	/**
	 * This constructor creates a new group of lights and a connection to a WiFi
	 * box using the default port. If there is already a group of lights and you
	 * need to control another group connected to the same WiFi box then use
	 * light.getWiFiBox().getLights(group) instead to resuse the connection.
	 * 
	 * @param host
	 *            is the host given as hostname such as "domain.tld" or string
	 *            repesentation of an ip address
	 * @param group
	 *            is the number of the group at the WiFi box (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @throws UnknownHostException
	 *             if the hostname could not be resolved
	 * @see Lights#getWifiBox() to get the WiFi box connected to group of lights
	 * @see WiFiBox#getLights(int) to get another group of lights for the same
	 *      WiFi box
	 */
	public Lights(String host, int group) throws UnknownHostException {
		super();
		wifiBox = new WiFiBox(host);
		setGroup(group);
	}

	/**
	 * Get the WiFi box used to control this group of lights.
	 * 
	 * @return the WiFi box used to control this group of lights
	 */
	public WiFiBox getWifiBox() {
		return wifiBox;
	}

	/**
	 * Get the number of the group of lights at the WiFi box.
	 * 
	 * @return the number of the group of lights at the WiFi box
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * Sets a new group number used at the WiFi box. Since each instance of this
	 * class should represent only one group of lights this function should not
	 * be used outside constructors and is therefore private.
	 * 
	 * @param group
	 *            is the group number to set
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	private void setGroup(int group) throws IllegalArgumentException {
		// check group number
		if (1 > group || group > 4) {
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}

		// set attributes
		this.group = group;
	}

	/**
	 * Switch the group of lights on and restore the last state.
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void on() throws IOException {
		wifiBox.on(group);
	}

	/**
	 * Switch the group of lights off.
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void off() throws IOException {
		wifiBox.off(group);
	}

	/**
	 * Switch the group of lights to the wight mode and restore the last
	 * brightness value.
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void white() throws IOException {
		wifiBox.white(group);
	}

	/**
	 * Switch the group of lights to the disco mode.
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoMode() throws IOException {
		wifiBox.discoMode(group);
	}

	/**
	 * Increase the disco mode's speed.
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoModeFaster() throws IOException {
		wifiBox.discoModeFaster();
	}

	/**
	 * Decrease the disco mode's speed.
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoModeSlower() throws IOException {
		wifiBox.discoModeSlower();
	}

	/**
	 * Set the brightness value for the group of lights.
	 * 
	 * @param value
	 *            is the brightness value to set (between WiFiBox.MIN_BRIGHTNESS
	 *            and WiFiBox.MAX_BRIGHTNESS)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if the brightness value is not between WiFiBox.MIN_BRIGHTNESS
	 *             and WiFiBox.MAX_BRIGHTNESS
	 */
	public void brightness(int value) throws IOException,
			IllegalArgumentException {
		wifiBox.brightness(group, value);
	}

	/**
	 * Set the color value for the group of lights.
	 * 
	 * @param value
	 *            is the color value to set (between WiFiBox.MIN_COLOR and
	 *            WiFiBox.MAX_COLOR)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if the color value is not between WiFiBox.MIN_COLOR and
	 *             WiFiBox.MAX_COLOR
	 */
	public void color(int value) throws IOException, IllegalArgumentException {
		wifiBox.color(group, value);
	}

	/**
	 * Set the color value for the group of lights.
	 * 
	 * @param color
	 *            is the color to set
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void color(MilightColor color, boolean forceColoredMode)
			throws IOException {
		wifiBox.color(group, color, forceColoredMode);
	}

	/**
	 * Set the color value for the group of lights. Colors with low saturation
	 * will be displayed in white mode for a better result.
	 * 
	 * @param color
	 *            is the color to set
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void color(MilightColor color) throws IOException {
		wifiBox.color(group, color);
	}

	/**
	 * Set the color value for the group of lights.
	 * 
	 * @param color
	 *            is the color to set
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void color(Color color, boolean forceColoredMode) throws IOException {
		wifiBox.color(group, color, forceColoredMode);
	}

	/**
	 * Set the color value for the group of lights. Colors with low saturation
	 * will be displayed in white mode for a better result.
	 * 
	 * @param color
	 *            is the color to set
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void color(Color color) throws IOException {
		wifiBox.color(group, color);
	}

	/**
	 * Set the color and brightness values for the group of lights. Both values
	 * are extracted from the color given to the function by transforming it to
	 * an HSB color.
	 * 
	 * @param color
	 *            is the color to extract hue and brightness from
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 */
	public void colorAndBrightness(MilightColor color, boolean forceColoredMode) {
		wifiBox.colorAndBrightness(group, color, forceColoredMode);
	}

	/**
	 * Set the color and brightness values for the group of lights. Both values
	 * are extracted from the color given to the function by transforming it to
	 * an HSB color. Colors with low saturation will be displayed in white mode
	 * for a better result.
	 * 
	 * @param color
	 *            is the color to extract hue and brightness from
	 */
	public void colorAndBrightness(MilightColor color) {
		wifiBox.colorAndBrightness(group, color);
	}

	/**
	 * Set the color and brightness values for the group of lights. Both values
	 * are extracted from the color given to the function by transforming it to
	 * an HSB color.
	 * 
	 * @param color
	 *            is the color to extract hue and brightness from
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 */
	public void colorAndBrightness(Color color, boolean forceColoredMode) {
		wifiBox.colorAndBrightness(group, color, forceColoredMode);
	}

	/**
	 * Set the color and brightness values for the group of lights. Both values
	 * are extracted from the color given to the function by transforming it to
	 * an HSB color. Colors with low saturation will be displayed in white mode
	 * for a better result.
	 * 
	 * @param color
	 *            is the color to extract hue and brightness from
	 */
	public void colorAndBrightness(Color color) {
		wifiBox.colorAndBrightness(group, color);
	}

	/**
	 * This function makes the light blink in a given color as a notification.
	 * The messages will be sent in a new thread.
	 * 
	 * @param color
	 *            is the color to blink in (white mode in the mean time)
	 * @param times
	 *            is the number of times to blink
	 * @param colorTime
	 *            is the time to stay in colored mode in milliseconds
	 * @param whiteTime
	 *            is the time to stay in white mode (between blinking) in
	 *            milliseconds
	 * @throws IllegalArgumentException
	 *             if the time in colored or white mode is not at least
	 *             2*WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES or the times variable is
	 *             non-positive
	 */
	public void blink(final Color color, final int times, final long colorTime,
			final long whiteTime) throws IllegalArgumentException {
		// check arguments
		if (colorTime < 2 * WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES) {
			throw new IllegalArgumentException(
					"The time to stay in colored mode should be at least 2*WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES.");
		}
		if (whiteTime < 2 * WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES) {
			throw new IllegalArgumentException(
					"The time to stay in white mode should be at least 2*WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES.");
		}
		if (times <= 0) {
			throw new IllegalArgumentException(
					"The number of times to blink should be at least one.");
		}

		// run in a new thread
		new Thread(new Runnable() {
			public void run() {
				try {
					for (int i = 0; i < times; i++) {
						// colored mode
						color(color, true);
						Thread.sleep(colorTime);

						// white mode
						white();
						Thread.sleep(whiteTime);
					}
				} catch (IOException e) {
					// exception while sending the messages
					return;
				} catch (InterruptedException e) {
					// exception while sleeping
					return;
				}
			}
		}).start();
	}

	/**
	 * This function makes the light blink in a given color as a notification.
	 * The each phase of the blinking will last one second. The messages will be
	 * sent in a new thread.
	 * 
	 * @param color
	 *            is the color to blink in (white mode in the mean time)
	 * @param times
	 *            is the number of times to blink
	 * @throws IllegalArgumentException
	 *             if the times variable is non-positive
	 */
	public void blink(final Color color, final int times)
			throws IllegalArgumentException {
		blink(color, times, 1000, 1000);
	}

	/**
	 * This function makes the light blink in a given color three times as a
	 * notification. The each phase of the blinking will last one second. The
	 * messages will be sent in a new thread.
	 * 
	 * @param color
	 *            is the color to blink in (white mode in the mean time)
	 */
	public void blink(final Color color) throws IllegalArgumentException {
		blink(color, 3);
	}
}
