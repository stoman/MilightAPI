package de.toman.milight.events;

import de.toman.milight.Lights;
import de.toman.milight.MilightColor;

/**
 * An instance of this class is fired by Lights instances when the group of
 * lights is switched to another color.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class ChangeColorEvent extends LightEvent {

	/**
	 * A generated serial version uid.
	 */
	private static final long serialVersionUID = -3592677622394996355L;

	/**
	 * The new color for the group of lights
	 */
	private MilightColor color;

	/**
	 * This constructor creates a new ChangeColorEvent referencing to a group of
	 * light that was switched to a new color.
	 * 
	 * @param lights
	 *            is the group of lights that was switched to white mode
	 * @param color
	 *            is the new color
	 */
	public ChangeColorEvent(Lights lights, MilightColor color) {
		super(lights);
		this.color = color;
	}

	/**
	 * This function returns the new color for the group of lights.
	 * 
	 * @return the new color for the group of lights
	 */
	public MilightColor getColor() {
		return color;
	}
}
