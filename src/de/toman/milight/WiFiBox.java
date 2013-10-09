package de.toman.milight;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
	 * The default port for unconfigured boxes.
	 */
	public static final int DEFAULT_PORT = 8899;

	/**
	 * The sleep time between both messages for switching lights to the white
	 * mode.
	 */
	public static final int DEFAULT_SLEEP_BETWEEN_MESSAGES = 100;

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
	 * A constructor creating a new instance of the WiFi box class.
	 * 
	 * @param address
	 *            is the address of the WiFi box
	 * @param port
	 *            is the port of the WiFi box (omit this if unsure)
	 */
	public WiFiBox(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
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
	 * This function sends a one-int control message to the WiFi box. The
	 * message is padded with 0x00 0x55 as given in the documentation.
	 * 
	 * @param message
	 *            is the message code to send
	 * @throws IOException
	 *             if the message could not be sent
	 */
	private void sendMessage(int message) throws IOException {
		// pad the message with 0x00 0x55
		byte[] paddedMessage = { (byte) message, 0x55 & 0x00, 0x55 & 0x55 };

		// send the padded message
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(paddedMessage,
				paddedMessage.length, address, port);
		socket.send(packet);
		socket.close();
	}

	/**
	 * This function sends multiple one-int messages to the WiFi box. All of the
	 * are padded with the corresponding ints. Note that the messages are sent
	 * in a new thread. Therefore, you should not send other commands directly
	 * after executing this one. Also, there are no exceptions when sending
	 * messages fails since they occur in another thread.
	 * 
	 * @param messages
	 *            is the messages to send (in order)
	 * @param sleep
	 *            is the time to wait between two message in milliseconds
	 */
	private void sendMultipleMessages(final int[] messages, final long sleep) {
		new Thread(new Runnable() {
			public void run() {
				try {
					for (int message : messages) {
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
	public void off(int group) throws IOException,
			IllegalArgumentException {
		switch (group) {
		case 1:
			sendMessage(COMMAND_GROUP_1_OFF);
			break;
		case 2:
			sendMessage(COMMAND_GROUP_2_OFF);
			break;
		case 3:
			sendMessage(COMMAND_GROUP_3_OFF);
			break;
		case 4:
			sendMessage(COMMAND_GROUP_4_OFF);
			break;
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
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
		switch (group) {
		case 1:
			sendMessage(COMMAND_GROUP_1_ON);
			break;
		case 2:
			sendMessage(COMMAND_GROUP_2_ON);
			break;
		case 3:
			sendMessage(COMMAND_GROUP_3_ON);
			break;
		case 4:
			sendMessage(COMMAND_GROUP_4_ON);
			break;
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
	}

	/**
	 * Switch all lights in all groups to the white mode. Note that the messages
	 * are sent in a new thread. Therefore, you should not send other commands
	 * directly after executing this one. Also, there are no exceptions when
	 * sending messages fails since they occur in another thread.
	 */
	public void white() {
		int[] messages = { COMMAND_ALL_ON, COMMAND_ALL_WHITE };
		sendMultipleMessages(messages, DEFAULT_SLEEP_BETWEEN_MESSAGES);
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
		int[] messages = new int[2];
		switch (group) {
		case 1:
			messages[0] = COMMAND_GROUP_1_ON;
			messages[1] = COMMAND_GROUP_1_WHITE;
			break;
		case 2:
			messages[0] = COMMAND_GROUP_2_ON;
			messages[1] = COMMAND_GROUP_2_WHITE;
			break;
		case 3:
			messages[0] = COMMAND_GROUP_3_ON;
			messages[1] = COMMAND_GROUP_3_WHITE;
			break;
		case 4:
			messages[0] = COMMAND_GROUP_4_ON;
			messages[1] = COMMAND_GROUP_4_WHITE;
			break;
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
		sendMultipleMessages(messages, DEFAULT_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Trigger the disco mode for the active group of lights (the last one that
	 * was switched on).
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
		int[] messages = { 0, COMMAND_DISCO };
		switch (group) {
		case 1:
			messages[0] = COMMAND_GROUP_1_ON;
			break;
		case 2:
			messages[0] = COMMAND_GROUP_2_ON;
			break;
		case 3:
			messages[0] = COMMAND_GROUP_3_ON;
			break;
		case 4:
			messages[0] = COMMAND_GROUP_4_ON;
			break;
		default:
			throw new IllegalArgumentException(
					"The group number must be between 1 and 4");
		}
		sendMultipleMessages(messages, DEFAULT_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * Increase the disco mode's speed for the active group of lights (the last
	 * one that was switched on).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoModeFaster() throws IOException {
		sendMessage(COMMAND_DISCO_FASTER);
	}

	/**
	 * Decrease the disco mode's speed for the active group of lights (the last
	 * one that was switched on).
	 * 
	 * @throws IOException
	 *             if the message could not be sent
	 */
	public void discoModeSlower() throws IOException {
		sendMessage(COMMAND_DISCO_SLOWER);
	}

}
