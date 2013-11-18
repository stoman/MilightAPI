package de.toman.milight;

/**
 * This class represents a group of lights that remembers its current state and
 * is able to restore it later one. Since the lights don't send any messages
 * this works only if there is no other instance of this class, mobile app,
 * remote control etc. which sends commands to the group of lights.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class StatefulLights extends Lights {
	
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
	public StatefulLights(WiFiBox wifiBox, int group)
			throws IllegalArgumentException {
		super(wifiBox, group);
	}
}
