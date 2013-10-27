package de.toman.milight.events;

import java.util.EventObject;

import de.toman.milight.Lights;

/**
 * This class is the base class for all events that occur when sending commands
 * to a group of lights. Possible events include switching the lights on or off,
 * changing the color or brightness, configuring disco mode etc. All these
 * events are represented by subclasses.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public abstract class LightEvent extends EventObject {
	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = -628470788207177166L;

	/**
	 * This constructor creates a new LightEvent referencing to a group of light
	 * that was changed.
	 * 
	 * @param lights
	 *            is the group of lights that changed
	 */
	public LightEvent(Lights lights) {
		super(lights);
	}
}
