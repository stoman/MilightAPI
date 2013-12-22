package de.toman.milight;

import javax.sound.sampled.TargetDataLine;

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
}
