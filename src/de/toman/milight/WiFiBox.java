package de.toman.milight;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import de.toman.milight.events.ChangeBrightnessEvent;
import de.toman.milight.events.ChangeColorEvent;
import de.toman.milight.events.DiscoModeEvent;
import de.toman.milight.events.DiscoModeFasterEvent;
import de.toman.milight.events.DiscoModeSlowerEvent;
import de.toman.milight.events.LightEvent;
import de.toman.milight.events.LightListener;
import de.toman.milight.events.SwitchOffEvent;
import de.toman.milight.events.SwitchOnEvent;
import de.toman.milight.events.WhiteModeEvent;

/**
 * This class represents a MiLight WiFi box and is able to send commands to a
 * particular box.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class WiFiBox {
	/**
	 * The address of the WiFi box
	 */
	private InetAddress address;

	/**
	 * The port of the WiFi box
	 */
	private int port;

	/**
	 * The set of all listeners listening for all groups of lights connected to
	 * this WiFiBox.
	 */
	private Set<LightListener>[] lightListeners;

	/**
	 * The number of the currently active group
	 */
	private int activeGroup;

	/**
	 * The default port for unconfigured boxes.
	 */
	public static final int DEFAULT_PORT = 8899;

	/**
	 * The sleep time between both messages for switching lights to the white
	 * mode.
	 */
	public static final int MIN_SLEEP_BETWEEN_MESSAGES = 100;

	/**
	 * The command code for "RGBW COLOR LED ALL OFF".
	 */
	public static final int COMMAND_ALL_OFF = 0x41;

	/**
	 * The command code for "GROUP 1 ALL OFF".
	 */
	public static final int COMMAND_GROUP_1_OFF = 0x46;
	/**
	 * The command code for "GROUP 2 ALL OFF".
	 */
	public static final int COMMAND_GROUP_2_OFF = 0x48;
	/**
	 * The command code for "GROUP 3 ALL OFF".
	 */
	public static final int COMMAND_GROUP_3_OFF = 0x4A;
	/**
	 * The command code for "GROUP 4 ALL OFF".
	 */
	public static final int COMMAND_GROUP_4_OFF = 0x4C;
	/**
	 * The command code for "RGBW COLOR LED ALL ON".
	 */
	public static final int COMMAND_ALL_ON = 0x42;

	/**
	 * The command code for "GROUP 1 ALL ON".
	 */
	public static final int COMMAND_GROUP_1_ON = 0x45;

	/**
	 * The command code for "GROUP 2 ALL ON".
	 */
	public static final int COMMAND_GROUP_2_ON = 0x47;

	/**
	 * The command code for "GROUP 3 ALL ON".
	 */
	public static final int COMMAND_GROUP_3_ON = 0x49;

	/**
	 * The command code for "GROUP 4 ALL ON".
	 */
	public static final int COMMAND_GROUP_4_ON = 0x4B;

	/**
	 * The command code for "SET COLOR TO WHITE (GROUP ALL)". Send an "ON"
	 * command 100ms before.
	 */
	public static final int COMMAND_ALL_WHITE = 0xC2;

	/**
	 * The command code for "SET COLOR TO WHITE (GROUP 1)". Send an "ON" command
	 * 100ms before.
	 */
	public static final int COMMAND_GROUP_1_WHITE = 0xC5;

	/**
	 * The command code for "SET COLOR TO WHITE (GROUP 2)". Send an "ON" command
	 * 100ms before.
	 */
	public static final int COMMAND_GROUP_2_WHITE = 0xC7;

	/**
	 * The command code for "SET COLOR TO WHITE (GROUP 3)". Send an "ON" command
	 * 100ms before.
	 */
	public static final int COMMAND_GROUP_3_WHITE = 0xC9;

	/**
	 * The command code for "SET COLOR TO WHITE (GROUP 4)". Send an "ON" command
	 * 100ms before.
	 */
	public static final int COMMAND_GROUP_4_WHITE = 0xCB;

	/**
	 * The command code for "DISCO MODE".
	 */
	public static final int COMMAND_DISCO = 0x4D;

	/**
	 * The command code for "DISCO SPEED FASTER".
	 */
	public static final int COMMAND_DISCO_FASTER = 0x44;

	/**
	 * The command code for "DISCO SPEED SLOWER".
	 */
	public static final int COMMAND_DISCO_SLOWER = 0x43;

	/**
	 * The command code for "COLOR SETTING" (part of a two-byte command).
	 */

	public static final int COMMAND_COLOR = 0x40;
	/**
	 * The command code for "DIRECT BRIGHTNESS SETTING" (part of a two-byte
	 * command).
	 */
	public static final int COMMAND_BRIGHTNESS = 0x4E;

	/**
	 * A constructor creating a new instance of the WiFi box class.
	 * 
	 * @param address
	 *            is the address of the WiFi box
	 * @param port
	 *            is the port of the WiFi box (omit this if unsure)
	 */
	@SuppressWarnings("unchecked")
	public WiFiBox(InetAddress address, int port) {
		// super call
		super();

		// save attributes
		this.address = address;
		this.port = port;

		// create listener sets
		lightListeners = new HashSet[4];
		for (int i = 0; i < 4; i++) {
			lightListeners[i] = new HashSet<LightListener>();
		}
	}

	/**
	 * A constructor creating a new instance of the WiFi box class using the
	 * default port number.
	 * 
	 * @param address
	 *            is the address of the WiFi box
	 */
	public WiFiBox(InetAddress address) {
		this(address, DEFAULT_PORT);
	}

	/**
	 * A constructor creating a new instance of the WiFi box class. The address
	 * is resolved from a hostname or ip address.
	 * 
	 * @param host
	 *            is the host given as hostname such as "domain.tld" or string
	 *            repesentation of an ip address
	 * @param port
	 *            is the port of the WiFi box (omit this if unsure)
	 * @throws UnknownHostException
	 *             if the hostname could not be resolved
	 */
	public WiFiBox(String host, int port) throws UnknownHostException {
		this(InetAddress.getByName(host), port);
	}

	/**
	 * A constructor creating a new instance of the WiFi box class using the
	 * default port number. The address is resolved from a hostname or ip
	 * address.
	 * 
	 * @param host
	 *            is the host given as hostname such as "domain.tld" or string
	 *            repesentation of an ip address
	 * @throws UnknownHostException
	 *             if the hostname could not be resolved
	 */
	public WiFiBox(String host) throws UnknownHostException {
		this(host, DEFAULT_PORT);
	}

	/**
	 * Get the group of lights that is controlled by a given group number. The
	 * Lights instance may be used to control the groups of lights individually
	 * and mix different WiFi boxes.
	 * 
	 * @param group
	 *            is the number of the group at the WiFi box (between 1 and 4)
	 * @return the group of lights that is controled by the given group number
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	public Lights getLights(int group) throws IllegalArgumentException {
		// check group number
		if (1 > group || group > 4) {
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}

		// create new instance
		return new Lights(this, group);
	}

	/**
	 * This function sends an array of bytes to the WiFi box. The bytes should
	 * be a valid command, i.e. the array's length should be three.
	 * 
	 * @param messages
	 *            is an array of message codes to send
	 * @throws IllegalArgumentException
	 *             if the length of the array is not 3
	 * @throws IOException
	 *             if the message could not be sent
	 */
	private void sendMessage(byte[] messages) throws IOException {
		// check arguments
		if (messages.length != 3) {
			throw new IllegalArgumentException(
					"The message to send should consist of exactly 3 bytes.");
		}

		// notify listeners
		notifyLightListeners(messages);

		// send message
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(messages, messages.length,
				address, port);
		socket.send(packet);
		socket.close();

		// adjust currently active group of lights
		switch (messages[0]) {
		case COMMAND_GROUP_1_ON:
		case COMMAND_GROUP_1_OFF:
			activeGroup = 1;
			break;
		case COMMAND_GROUP_2_ON:
		case COMMAND_GROUP_2_OFF:
			activeGroup = 2;
			break;
		case COMMAND_GROUP_3_ON:
		case COMMAND_GROUP_3_OFF:
			activeGroup = 3;
			break;
		case COMMAND_GROUP_4_ON:
		case COMMAND_GROUP_4_OFF:
			activeGroup = 4;
			break;
		}
	}

	/**
	 * This function pads a one-byte message to a three-byte message by adding
	 * the default bytes 0x00 0x55.
	 * 
	 * @param message
	 *            is the message to pad
	 * @return is the padded message
	 */
	private byte[] padMessage(int message) {
		byte[] paddedMessage = { (byte) message, 0x55 & 0x00, 0x55 & 0x55 };
		return paddedMessage;
	}

	/**
	 * This function pads a two-byte message to a three-byte message by adding
	 * the default byte 0x55.
	 * 
	 * @param message1
	 *            is the first byte of the message to pad
	 * @param message2
	 *            is the second byte of the message to pad
	 * @return is the padded message
	 */
	private byte[] padMessage(int message1, int message2) {
		byte[] paddedMessage = { (byte) message1, (byte) message2, 0x55 & 0x55 };
		return paddedMessage;
	}

	/**
	 * This function constructs a three-byte command to switch on a given group
	 * of lights. This array is ready to be sent to the WiFi box.
	 * 
	 * @param group
	 *            is the group of lights to switch on
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @return the message array to send to the WiFi box
	 */
	private byte[] getSwitchOnCommand(int group)
			throws IllegalArgumentException {
		switch (group) {
		case 1:
			return padMessage(COMMAND_GROUP_1_ON);
		case 2:
			return padMessage(COMMAND_GROUP_2_ON);
		case 3:
			return padMessage(COMMAND_GROUP_3_ON);
		case 4:
			return padMessage(COMMAND_GROUP_4_ON);
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
	}

	/**
	 * This function constructs a three-byte command to switch off a given group
	 * of lights. This array is ready to be sent to the WiFi box.
	 * 
	 * @param group
	 *            is the group of lights to switch off
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @return the message array to send to the WiFi box
	 */
	private byte[] getSwitchOffCommand(int group)
			throws IllegalArgumentException {
		switch (group) {
		case 1:
			return padMessage(COMMAND_GROUP_1_OFF);
		case 2:
			return padMessage(COMMAND_GROUP_2_OFF);
		case 3:
			return padMessage(COMMAND_GROUP_3_OFF);
		case 4:
			return padMessage(COMMAND_GROUP_4_OFF);
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
	}

	/**
	 * This function constructs a three-byte command to switch a given group of
	 * lights to the white mode. This array is ready to be sent to the WiFi box.
	 * 
	 * @param group
	 *            is the group of lights to switch to the white mode
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 * @return the message array to send to the WiFi box
	 */
	private byte[] getWhiteModeCommand(int group)
			throws IllegalArgumentException {
		switch (group) {
		case 1:
			return padMessage(COMMAND_GROUP_1_WHITE);
		case 2:
			return padMessage(COMMAND_GROUP_2_WHITE);
		case 3:
			return padMessage(COMMAND_GROUP_3_WHITE);
		case 4:
			return padMessage(COMMAND_GROUP_4_WHITE);
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
	}

	/**
	 * This function constructs a three-byte command to change the hue of a
	 * light to a given color
	 * 
	 * @param value
	 *            the color value (between MilightColor.MIN_COLOR and
	 *            MilightColor.MAX_COLOR)
	 * @throws IllegalArgumentException
	 *             if the color value is not between MilightColor.MIN_COLOR and
	 *             MilightColor.MAX_COLOR
	 * @return the message array to send to the WiFi box
	 */
	private byte[] getColorCommand(int value) throws IllegalArgumentException {
		// check argument
		if (value < MilightColor.MIN_COLOR || value > MilightColor.MAX_COLOR) {
			throw new IllegalArgumentException(
					"The color value should be between MilightColor.MIN_COLOR and MilightColor.MAX_COLOR");
		}

		// send message to the WiFi box
		return padMessage(COMMAND_COLOR, value);
	}

	/**
	 * This function sends an one-byte control message to the WiFi box. The
	 * message is padded with 0x00 0x55 as given in the documentation.
	 * 
	 * @param message
	 *            is the message code to send
	 * @throws IOException
	 *             if the message could not be sent
	 */
	private void sendMessage(int message) throws IOException {
		// pad the message with 0x00 0x55
		byte[] paddedMessage = padMessage(message);

		// send the padded message
		sendMessage(paddedMessage);
	}

	/**
	 * This function sends a two-byte control message to the WiFi box. The
	 * message is padded with 0x55 as given in the documentation.
	 * 
	 * @param message1
	 *            is the first byte of the message to send
	 * @param message2
	 *            is the second byte of the message to send
	 * @throws IOException
	 *             if the message could not be sent
	 */
	private void sendMessage(int message1, int message2) throws IOException {
		// pad the message with 0x55
		byte[] paddedMessage = padMessage(message1, message2);

		// send the padded message
		sendMessage(paddedMessage);
	}

	/**
	 * This function sends multiple three-byte messages to the WiFi box. All
	 * elements of the message array should be byte arrays with three elements.
	 * Note that the messages are sent in a new thread. Therefore, you should
	 * not send other commands directly after executing this one. Also, there
	 * are no exceptions when sending messages fails since they occur in another
	 * thread.
	 * 
	 * @param messages
	 *            is the messages to send (in order)
	 * @param sleep
	 *            is the time to wait between two message in milliseconds
	 * @throws IllegalArgumentException
	 *             if some of the messages in the array don't consist of exactly
	 *             three bytes
	 */
	private void sendMultipleMessages(final byte[][] messages, final long sleep)
			throws IllegalArgumentException {
		// check arguments
		for (int i = 0; i < messages.length; i++) {
			if (messages[i].length != 3) {
				throw new IllegalArgumentException(
						"All messages should consist of three bytes.");
			}
		}

		// start new thread
		new Thread(new Runnable() {
			public void run() {
				try {
					for (byte[] message : messages) {
						WiFiBox.this.sendMessage(message);
						Thread.sleep(sleep);
					}
				} catch (IOException e) {
					// if the message could not be sent
				} catch (InterruptedException e) {
					// if the thread could not sleep
				}
			}
		}).start();
	}

	/**
	 * This function sends multiple one-byte messages to the WiFi box. All of
	 * the are padded with the corresponding bytes. Note that the messages are
	 * sent in a new thread. Therefore, you should not send other commands
	 * directly after executing this one. Also, there are no exceptions when
	 * sending messages fails since they occur in another thread.
	 * 
	 * @param messages
	 *            is the messages to send (in order)
	 * @param sleep
	 *            is the time to wait between two message in milliseconds
	 */
	private void sendMultipleMessages(final int[] messages, final long sleep) {
		// pad messages
		byte[][] paddedMessages = new byte[messages.length][3];
		for (int i = 0; i < messages.length; i++) {
			paddedMessages[i] = padMessage(messages[i]);
		}

		// send the padded messages
		sendMultipleMessages(paddedMessages, sleep);
	}

	/**
	 * Switch all lights off (all groups).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void off() throws IOException {
		sendMessage(COMMAND_ALL_OFF);
	}

	/**
	 * Switch all lights of a particular group off.
	 * 
	 * @param group
	 *            the group to switch of (between 1 and 4)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	public void off(int group) throws IOException, IllegalArgumentException {
		sendMessage(getSwitchOffCommand(group));
	}

	/**
	 * Switch all lights on (all groups).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void on() throws IOException {
		sendMessage(COMMAND_ALL_ON);
	}

	/**
	 * Switch all lights of a particular group on.
	 * 
	 * @param group
	 *            the group to switch of (between 1 and 4)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	public void on(int group) throws IOException, IllegalArgumentException {
		sendMessage(getSwitchOnCommand(group));
	}

	/**
	 * Switch all lights in all groups to the white mode. Note that the messages
	 * are sent in a new thread. Therefore, you should not send other commands
	 * directly after executing this one. Also, there are no exceptions when
	 * sending messages fails since they occur in another thread.
	 */
	public void white() {
		int[] messages = { COMMAND_ALL_ON, COMMAND_ALL_WHITE };
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Switch all lights in a particular group to the white mode. Note that the
	 * messages are sent in a new thread. Therefore, you should not send other
	 * commands directly after executing this one. Also, there are no exceptions
	 * when sending messages fails since they occur in another thread.
	 * 
	 * @param group
	 *            the group to switch of (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	public void white(int group) throws IllegalArgumentException {
		// create message array
		byte[][] messages = new byte[2][3];

		// switch on first
		messages[0] = getSwitchOnCommand(group);

		// switch to white mode
		messages[1] = getWhiteModeCommand(group);

		// send messages
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Trigger the disco mode for the active group of lights (the last one that
	 * was switched on, see {@link WiFiBox#getActiveGroup()}).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoMode() throws IOException {
		sendMessage(COMMAND_DISCO);
	}

	/**
	 * Triggers the disco mode for a particular group of lights. The lights will
	 * be switched on before to activate them.Note that the messages are sent in
	 * a new thread. Therefore, you should not send other commands directly
	 * after executing this one. Also, there are no exceptions when sending
	 * messages fails since they occur in another thread.
	 * 
	 * @param group
	 *            the group to switch of (between 1 and 4)
	 * @throws IllegalArgumentException
	 *             if the group number is not between 1 and 4
	 */
	public void discoMode(int group) throws IllegalArgumentException {
		// create message array
		byte[][] messages = new byte[2][3];

		// switch on first
		messages[0] = getSwitchOnCommand(group);

		// start disco mode
		messages[1] = padMessage(COMMAND_DISCO);

		// send messages
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Increase the disco mode's speed for the active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoModeFaster() throws IOException {
		sendMessage(COMMAND_DISCO_FASTER);
	}

	/**
	 * Decrease the disco mode's speed for the active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoModeSlower() throws IOException {
		sendMessage(COMMAND_DISCO_SLOWER);
	}

	/**
	 * Set the brightness value for the currently active group of lights (the
	 * last one that was switched on, see {@link WiFiBox#getActiveGroup()}).
	 * 
	 * @param value
	 *            is the brightness value to set (between
	 *            MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if the brightness value is not between
	 *             MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS
	 */
	public void brightness(int value) throws IOException,
			IllegalArgumentException {
		// check argument
		if (value < MilightColor.MIN_BRIGHTNESS
				|| value > MilightColor.MAX_BRIGHTNESS) {
			throw new IllegalArgumentException(
					"The brightness value should be between MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS");
		}

		// send message to the WiFi box
		sendMessage(COMMAND_BRIGHTNESS, value);
	}

	/**
	 * Set the brightness value for a given group of lights.
	 * 
	 * @param group
	 *            is the number of the group to set the brightness for
	 * @param value
	 *            is the brightness value to set (between
	 *            MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4 or the brightness value is
	 *             not between MilightColor.MIN_BRIGHTNESS and
	 *             MilightColor.MAX_BRIGHTNESS
	 */
	public void brightness(int group, int value) throws IOException,
			IllegalArgumentException {
		// check arguments
		if (value < MilightColor.MIN_BRIGHTNESS
				|| value > MilightColor.MAX_BRIGHTNESS) {
			throw new IllegalArgumentException(
					"The brightness value should be between MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS");
		}

		// create message array
		byte[][] messages = new byte[2][3];

		// switch on first
		messages[0] = getSwitchOnCommand(group);

		// adjust brightness
		messages[1] = padMessage(COMMAND_BRIGHTNESS, value);

		// send messages
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Set the color value for the currently active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}).
	 * 
	 * @param value
	 *            is the color value to set (between MilightColor.MIN_COLOR and
	 *            MilightColor.MAX_COLOR)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if the color value is not between MilightColor.MIN_COLOR and
	 *             MilightColor.MAX_COLOR
	 */
	public void color(int value) throws IOException, IllegalArgumentException {
		// send message to the WiFi box
		sendMessage(getColorCommand(value));
	}

	/**
	 * Set the color value for the currently active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}).
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
		if (color.isColoredMode() || forceColoredMode) {
			// colored mode
			color(color.getMilightHue());
		} else {
			// white mode
			white();
		}
	}

	/**
	 * Set the color value for the currently active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}). Colors
	 * with low saturation will be displayed in white mode for a better result.
	 * 
	 * @param color
	 *            is the color to set
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void color(MilightColor color) throws IOException {
		color(color, false);
	}

	/**
	 * Set the color value for the currently active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}).
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
		color(new MilightColor(color), forceColoredMode);
	}

	/**
	 * Set the color value for the currently active group of lights (the last
	 * one that was switched on, see {@link WiFiBox#getActiveGroup()}). Colors
	 * with low saturation will be displayed in white mode for a better result.
	 * 
	 * @param color
	 *            is the color to set
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void color(Color color) throws IOException {
		color(new MilightColor(color));
	}

	/**
	 * Set the color value for a given group of lights.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param value
	 *            is the color value to set (between MilightColor.MIN_COLOR and
	 *            MilightColor.MAX_COLOR)
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4 or the color value is not
	 *             between MilightColor.MIN_COLOR and MilightColor.MAX_COLOR
	 */
	public void color(int group, int value) throws IOException,
			IllegalArgumentException {
		// create message array
		byte[][] messages = new byte[2][3];

		// switch on first
		messages[0] = getSwitchOnCommand(group);

		// adjust color
		messages[1] = getColorCommand(value);

		// send messages
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Set the color value for a given group of lights.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to set
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void color(int group, MilightColor color, boolean forceColoredMode)
			throws IOException, IllegalArgumentException {
		if (color.isColoredMode() || forceColoredMode) {
			// colored mode
			color(group, color.getMilightHue());
		} else {
			// white mode
			white(group);
		}
	}

	/**
	 * Set the color value for a given group of lights. Colors with low
	 * saturation will be displayed in white mode for a better result.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to set
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void color(int group, MilightColor color) throws IOException,
			IllegalArgumentException {
		color(group, color, false);
	}

	/**
	 * Set the color value for a given group of lights.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to set
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void color(int group, Color color, boolean forceColoredMode)
			throws IOException, IllegalArgumentException {
		color(group, new MilightColor(color), forceColoredMode);
	}

	/**
	 * Set the color value for a given group of lights. Colors with low
	 * saturation will be displayed in white mode for a better result.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to set
	 * @throws IOException
	 *             if the message could not be sent
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void color(int group, Color color) throws IOException,
			IllegalArgumentException {
		color(group, new MilightColor(color));
	}

	/**
	 * Set the color and brightness values for the currently active group of
	 * lights (the last one that was switched on, see
	 * {@link WiFiBox#getActiveGroup()}). Both values are extracted from the
	 * color given to the function by transforming it to an HSB color.
	 * 
	 * @param color
	 *            is the color to extract hue and brightness from
	 */
	public void colorAndBrightness(MilightColor color) {
		// create message array
		byte[][] messages = new byte[2][3];

		// adjust color
		messages[0] = getColorCommand(color.getMilightHue());

		// adjust brightness
		messages[1] = padMessage(COMMAND_BRIGHTNESS,
				color.getMilightBrightness());

		// send messages
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Set the color and brightness values for the currently active group of
	 * lights (the last one that was switched on, see
	 * {@link WiFiBox#getActiveGroup()}). Both values are extracted from the
	 * color given to the function by transforming it to an HSB color.
	 * 
	 * @param color
	 *            is the color to extract hue and brightness from
	 */
	public void colorAndBrightness(Color color) {
		colorAndBrightness(new MilightColor(color));
	}

	/**
	 * Set the color and brightness values for a given group of lights. Both
	 * values are extracted from the color given to the function by transforming
	 * it to an HSB color.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to extract hue and brightness from
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void colorAndBrightness(int group, MilightColor color,
			boolean forceColoredMode) {
		// create message array
		byte[][] messages = new byte[3][3];

		// switch on first
		messages[0] = getSwitchOnCommand(group);

		// adjust color
		if (color.isColoredMode() || forceColoredMode) {
			// colored mode
			messages[1] = getColorCommand(color.getMilightHue());
		} else {
			// white mode
			messages[1] = getWhiteModeCommand(group);
		}

		// adjust brightness
		messages[2] = padMessage(COMMAND_BRIGHTNESS,
				color.getMilightBrightness());

		// send messages
		sendMultipleMessages(messages, MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Set the color and brightness values for a given group of lights. Both
	 * values are extracted from the color given to the function by transforming
	 * it to an HSB color. Colors with low saturation will be displayed in white
	 * mode for a better result.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to extract hue and brightness from
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void colorAndBrightness(int group, MilightColor color) {
		colorAndBrightness(group, color, false);
	}

	/**
	 * Set the color and brightness values for a given group of lights. Both
	 * values are extracted from the color given to the function by transforming
	 * it to an HSB color.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to extract hue and brightness from
	 * @param forceColoredMode
	 *            true if all colors should be displayed in colored mode, false
	 *            to use white mode for colors with low saturation and else
	 *            colored mode
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void colorAndBrightness(int group, Color color,
			boolean forceColoredMode) {
		colorAndBrightness(group, new MilightColor(color), forceColoredMode);
	}

	/**
	 * Set the color and brightness values for a given group of lights. Both
	 * values are extracted from the color given to the function by transforming
	 * it to an HSB color. Colors with low saturation will be displayed in white
	 * mode for a better result.
	 * 
	 * @param group
	 *            is the number of the group to set the color for
	 * @param color
	 *            is the color to extract hue and brightness from
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void colorAndBrightness(int group, Color color) {
		colorAndBrightness(group, new MilightColor(color));
	}

	/**
	 * Use this function to add a new listener one group of lights connected to
	 * the WiFiBox. Listeners will be notified when the group of lights is
	 * switched on or off, color or brightness change, white or disco mode is
	 * activated or disco mode is set faster or slower.
	 * 
	 * @param group
	 *            is the number of the group to add the listener to
	 * @param listener
	 *            is the listener to add
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void addLightListener(int group, LightListener listener) {
		// check group number
		if (1 > group || group > 4) {
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}

		// add listener
		lightListeners[group - 1].add(listener);
	}

	/**
	 * This function removes a listener from this WiFiBox which was added before
	 * by {@link WiFiBox#addLightListener(int, LightListener)}.
	 * 
	 * @param group
	 *            is the number of the group to remove the listener from
	 * @param listener
	 *            is the listener to remove
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	public void removeLightListener(int group, LightListener listener) {
		// check group number
		if (1 > group || group > 4) {
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}

		// remove listener
		lightListeners[group - 1].remove(listener);
	}

	/**
	 * This function sends a LightEvent to all listeners listening on a certain
	 * group of lights.
	 * 
	 * @param group
	 *            is the number of the group to notify
	 * @param event
	 *            is the LightEvent to send to all listeners
	 * @throws IllegalArgumentException
	 *             if group is not between 1 and 4
	 */
	private void notifyLightListeners(int group, LightEvent event) {
		// check group number
		if (1 > group || group > 4) {
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}

		// notify listeners
		for (LightListener listener : lightListeners[group - 1]) {
			listener.lightsChanged(event);
		}
	}

	/**
	 * This function sends a LightEvent to all listeners listening on a certain
	 * group of lights. The event's type and the group of lights receiving the
	 * message is obtained from the raw message sent to the WiFiBox.
	 * 
	 * @param message
	 *            is the raw message sent to the WiFiBox
	 */
	private void notifyLightListeners(byte[] message) {
		switch ((int) message[0]) {
		// switch off commands
		case COMMAND_ALL_OFF:
			for (int group = 1; group <= 4; group++) {
				notifyLightListeners(group,
						new SwitchOffEvent(getLights(group)));
			}
			break;
		case COMMAND_GROUP_1_OFF:
			notifyLightListeners(1, new SwitchOffEvent(getLights(1)));
			break;
		case COMMAND_GROUP_2_OFF:
			notifyLightListeners(2, new SwitchOffEvent(getLights(2)));
			break;
		case COMMAND_GROUP_3_OFF:
			notifyLightListeners(3, new SwitchOffEvent(getLights(3)));
			break;
		case COMMAND_GROUP_4_OFF:
			notifyLightListeners(4, new SwitchOffEvent(getLights(4)));
			break;
		// switch on commands
		case COMMAND_ALL_ON:
			for (int group = 1; group <= 4; group++) {
				notifyLightListeners(group, new SwitchOnEvent(getLights(group)));
			}
			break;
		case COMMAND_GROUP_1_ON:
			notifyLightListeners(1, new SwitchOnEvent(getLights(1)));
			break;
		case COMMAND_GROUP_2_ON:
			notifyLightListeners(2, new SwitchOnEvent(getLights(2)));
			break;
		case COMMAND_GROUP_3_ON:
			notifyLightListeners(3, new SwitchOnEvent(getLights(3)));
			break;
		case COMMAND_GROUP_4_ON:
			notifyLightListeners(4, new SwitchOnEvent(getLights(4)));
			break;
		// white mode commands
		case COMMAND_ALL_WHITE:
			for (int group = 1; group <= 4; group++) {
				notifyLightListeners(group,
						new WhiteModeEvent(getLights(group)));
			}
			break;
		case COMMAND_GROUP_1_WHITE:
			notifyLightListeners(1, new WhiteModeEvent(getLights(1)));
			break;
		case COMMAND_GROUP_2_WHITE:
			notifyLightListeners(2, new WhiteModeEvent(getLights(2)));
			break;
		case COMMAND_GROUP_3_WHITE:
			notifyLightListeners(3, new WhiteModeEvent(getLights(3)));
			break;
		case COMMAND_GROUP_4_WHITE:
			notifyLightListeners(4, new WhiteModeEvent(getLights(4)));
			break;
		// disco mode commands
		case COMMAND_DISCO:
			notifyLightListeners(getActiveGroup(), new DiscoModeEvent(
					getLights(getActiveGroup())));
			break;
		case COMMAND_DISCO_FASTER:
			notifyLightListeners(getActiveGroup(), new DiscoModeFasterEvent(
					getLights(getActiveGroup())));
			break;
		case COMMAND_DISCO_SLOWER:
			notifyLightListeners(getActiveGroup(), new DiscoModeSlowerEvent(
					getLights(getActiveGroup())));
			break;
		// change color commands
		case COMMAND_COLOR:
			notifyLightListeners(getActiveGroup(), new ChangeColorEvent(
					getLights(getActiveGroup()), null));// TODO find color
			break;
		// change brightness commands
		case COMMAND_BRIGHTNESS:
			notifyLightListeners(getActiveGroup(), new ChangeBrightnessEvent(
					getLights(getActiveGroup()), 0));// TODO find brightness
			break;
		}
	}

	/**
	 * This function returns the number of the currently active group of lights.
	 * 
	 * @return the number of the currently active group of lights
	 */
	public int getActiveGroup() {
		return activeGroup;
	}
}
