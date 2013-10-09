package de.toman.milight;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class represents a MiLight WiFi box and is able to send commands to a
 * specific box.
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
	 *             if the message could not be send
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
	 * @return true if the message was send successfully
	 */
	public boolean switchAllOff() {
		try {
			sendMessage((byte) 0x41);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Switch all lights on (all groups).
	 * 
	 * @return true if the message was send successfully
	 */
	public boolean switchAllOn() {
		try {
			sendMessage((byte) 0x42);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
