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
	 * The command code for "RGBW COLOR LED ALL OFF".
	 */
	public static final byte COMMAND_ALL_OFF = 0x41;
	
	/**
	 * The command code for "RGBW COLOR LED ALL ON".
	 */
	public static final byte COMMAND_ALL_ON = 0x42;
	
	/**
	 * The command code for "DISCO SPEED SLOWER".
	 */
	public static final byte COMMAND_DISCO_SLOWER = 0x43;
	
	/**
	 * The command code for "DISCO SPEED FASTER".
	 */
	public static final byte COMMAND_DISCO_FASTER = 0x44;
	
	/**
	 * The command code for "GROUP 1 ALL ON".
	 */
	public static final byte COMMAND_GROUP_1_ON = 0x45;
	
	/**
	 * The command code for "GROUP 1 ALL OFF".
	 */
	public static final byte COMMAND_GROUP_1_OFF = 0x46;
	
	/**
	 * The command code for "GROUP 2 ALL ON".
	 */
	public static final byte COMMAND_GROUP_2_ON = 0x47;
	
	/**
	 * The command code for "GROUP 2 ALL OFF".
	 */
	public static final byte COMMAND_GROUP_2_OFF = 0x48;
	
	/**
	 * The command code for "GROUP 3 ALL ON".
	 */
	public static final byte COMMAND_GROUP_3_ON = 0x49;
	
	/**
	 * The command code for "GROUP 3 ALL OFF".
	 */
	public static final byte COMMAND_GROUP_3_OFF = 0x4A;
	
	/**
	 * The command code for "GROUP 4 ALL ON".
	 */
	public static final byte COMMAND_GROUP_4_ON = 0x4B;
	
	/**
	 * The command code for "GROUP 4 ALL OFF".
	 */
	public static final byte COMMAND_GROUP_4_OFF = 0x4C;
	
	/**
	 * The command code for "DISCO MODE".
	 */
	public static final byte COMMAND_DISCO = 0x4D;

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
	 * This function sends a one-byte control message to the WiFi box. The
	 * message is padded with 0x00 0x55 as given in the documentation.
	 * 
	 * @param message
	 *            is the message code to send
	 * @throws IOException
	 *             if the message could not be sent
	 */
	private void sendMessage(byte message) throws IOException {
		// pad the message with 0x00 0x55
		byte[] paddedMessage = { message, 0x55 & 0x00, 0x55 & 0x55 };

		// send the padded message
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(paddedMessage,
				paddedMessage.length, address, port);
		socket.send(packet);
		socket.close();
	}

	/**
	 * Switch all lights off (all groups).
	 * 
	 * @throws IOException if the message could not be sent 
	 */
	public void switchAllOff() throws IOException {
		sendMessage(COMMAND_ALL_OFF);
	}

	/**
	 * Switch all lights on (all groups).
	 * 
	 * @throws IOException if the message could not be sent 
	 */
	public void switchAllOn() throws IOException {
		sendMessage(COMMAND_ALL_ON);
	}

	/**
	 * Switch all lights of a particular group off.
	 * @param group the group to switch of (between 1 and 4)
	 * @throws IOException if the message could not be sent
	 * @throws IllegalArgumentException if the group number is not between 1 and 4
	 */
	public void switchGroupOff(int group) throws IOException, IllegalArgumentException {
		switch(group) {
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
			throw new IllegalArgumentException("The group number must be between 1 and 4");
		}
	}

	/**
	 * Switch all lights of a particular group on.
	 * @param group the group to switch of (between 1 and 4)
	 * @throws IOException if the message could not be sent
	 * @throws IllegalArgumentException if the group number is not between 1 and 4
	 */
	public void switchGroupOn(int group) throws IOException, IllegalArgumentException {
		switch(group) {
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
			throw new IllegalArgumentException("The group number must be between 1 and 4");
		}
	}

}
