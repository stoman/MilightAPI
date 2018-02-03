package de.toman.milight;

import java.math.BigInteger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;

/**
 * With this class you can visualize your music using some group of lights. The
 * lights will change their color and brightness as the music does.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class MusicVisualizer implements Runnable {
	/**
	 * The group of lights controlled by this visualizer
	 */
	private Lights lights;

	/**
	 * The line to read the music input from.
	 */
	private TargetDataLine line;

	/**
	 * True if the thread should stop and not send any more commands.
	 */
	private boolean stopped;

	/**
	 * This constructor creates a new visualizer.
	 * 
	 * @param lights
	 *            the group of lights to be controlled
	 * @param line
	 *            the line to read the music input from
	 */
	public MusicVisualizer(Lights lights, TargetDataLine line) {
		super();
		this.lights = lights;
		this.line = line;
	}

	/**
	 * This function returns the group of lights controlled by this visualizer.
	 * 
	 * @return the group of lights controlled by this visualizer
	 */
	public Lights getLights() {
		return lights;
	}

	/**
	 * This function returns the line to read the music input from.
	 * 
	 * @return the line to read the music input from
	 */
	public TargetDataLine getLine() {
		return line;
	}

	/**
	 * This function returns the default line to listen on. It is searching for
	 * a mixer called "Primary Sound Capture Driver" or returns some line if
	 * none is found with this none. If there is no available line at all it
	 * will return null.
	 * 
	 * @return the line or null if no line is available
	 */
	public static TargetDataLine getDefaultLine() {
		// search for "Primary Sound Capture Driver"
		for (Info mixerInfo : AudioSystem.getMixerInfo()) {
			javax.sound.sampled.Line.Info[] lineInfos = AudioSystem.getMixer(
					mixerInfo).getTargetLineInfo();
			try {
				if (lineInfos.length > 0
						&& mixerInfo.getName().equals(
								"Primary Sound Capture Driver")) {
					return (TargetDataLine) AudioSystem.getLine(lineInfos[0]);
				}
			} catch (LineUnavailableException e) {
				// line unavailable -> ignore mixer
			}
		}

		// search for some other mixer
		for (Info mixerInfo : AudioSystem.getMixerInfo()) {
			javax.sound.sampled.Line.Info[] lineInfos = AudioSystem.getMixer(
					mixerInfo).getTargetLineInfo();
			try {
				if (lineInfos.length > 0) {
					return (TargetDataLine) AudioSystem.getLine(lineInfos[0]);
				}
			} catch (LineUnavailableException e) {
				// line unavailable -> ignore mixer
			}
		}

		// no line found at all
		return null;
	}

	/**
	 * This function converts a raw array of bytes to an integer value. It will
	 * be converted in the corresponding way to the
	 * {@link AudioFormat#isBigEndian()} value of the
	 * {@link MusicVisualizer#line} attribute.
	 * 
	 * @param data
	 *            is an array containing the values to be converted
	 * @param offset
	 *            is the number of bytes to skip
	 * @param length
	 *            is the number of bytes to read
	 * @return is the integer represented by the byte array
	 */
	public BigInteger convertByteArrayToInteger(byte[] data, int offset,
			int length) {
		// create variables
		BigInteger ret = BigInteger.ZERO;
		BigInteger power = BigInteger.valueOf(256);

		// read all parts
		for (int i = 0; i < length; i++) {
			ret = ret
					.multiply(power)
					.add(BigInteger
							.valueOf(data[line.getFormat().isBigEndian() ? (i + offset)
									: (offset + length - 1 - i)] & 0xff));
		}

		// return
		return ret;
	}

	/**
	 * This function converts a raw array of bytes to an integer value. It will
	 * be converted in the corresponding way to the
	 * {@link AudioFormat#isBigEndian()} value of the
	 * {@link MusicVisualizer#line} attribute.
	 * 
	 * @param data
	 *            is the array of values to be converted
	 * @return is the integer represented by the byte array
	 */
	public BigInteger convertByteArrayToInteger(byte[] data) {
		return convertByteArrayToInteger(data, 0, data.length);
	}

	/**
	 * This function runs the visualizer with the settings given in the constructor
	 * resp. setters. Use the function start to run this in a seperate thread.
	 * 
	 * @see MusicVisualizer#start()
	 */
	@Override
	public void run() {
		try {
			while (stopped == false) {
				// TODO

				// sleep
				Thread.sleep(100);
			}
			/*
			 * } catch (IOException e) { // exception while communicating with
			 * the lights
			 * stop();
			 */
		} catch (InterruptedException e) {
			// exception while sleeping
			stop();
		}
	}

	/**
	 * This function stops the thread running the visualizer. The visualizer can
	 * be resumed where it was interrupted by calling start again.
	 * 
	 * @see MusicVisualizer#start()
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * This function starts the visualizer in a new thread.
	 */
	public void start() {
		stopped = false;
		new Thread(this).start();
	}
}
