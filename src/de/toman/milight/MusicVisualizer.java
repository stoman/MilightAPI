package de.toman.milight;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer.Info;

/**
 * With this class you can visualize your music using some group of lights. The
 * lights will change their color and brightness as the music does.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class MusicVisualizer {
	/**
	 * The group of lights controlled by this visualizer
	 */
	private Lights lights;

	/**
	 * The line to read the music input from.
	 */
	private TargetDataLine line;

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
}
