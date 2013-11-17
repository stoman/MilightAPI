package de.toman.milight;

import java.awt.Color;

import de.toman.milight.events.ChangeBrightnessEvent;
import de.toman.milight.events.ChangeColorEvent;
import de.toman.milight.events.LightEvent;
import de.toman.milight.events.LightListener;

/**
 * This class stores the current state of a given group of lights and adjusts
 * this state each time it changes. It listens to the LightEvent instances fired
 * and is therefore only notified if commands are send to the group of lights
 * via this API and not via remote control or other controllers.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class LightObserver {
	private Lights lights;

	private LightState currentState;

	private static final MilightColor INITIAL_COLOR = new MilightColor(
			Color.WHITE);
	private static final float INITIAL_BRIGHTNESS = 1f;
	private static final boolean INITIAL_WHITE_MODE = true;
	private static final boolean INITIAL_ON = false;

	public LightObserver(Lights lights) {
		// call super constructor
		super();

		// set attributes
		this.lights = lights;
		currentState = new LightState(INITIAL_COLOR, INITIAL_BRIGHTNESS,
				INITIAL_WHITE_MODE, INITIAL_ON);

		// add as listener
		lights.addLightListener(new LightListener() {
			public void lightsChanged(LightEvent event) {
				// ChangeColorEvent
				switch (event.getClass().getSimpleName()) {
				case "ChangeColorEvent":
					setCurrentState(new LightState(((ChangeColorEvent) event)
							.getColor(), currentState.getBrightness(),
							currentState.isWhiteMode(), currentState.isOn()));
					break;
				// ChangeBrightnessEvent
				case "ChangeBrightnessEvent":
					if (currentState.isWhiteMode()) {
						// white mode
						setCurrentState(new LightState(
								currentState.getColor(),
								((ChangeBrightnessEvent) event).getBrightness(),
								currentState.isWhiteMode(), currentState.isOn()));
					} else {
						// colored mode
						MilightColor color = currentState.getColor();
						color.setBrightness(((ChangeBrightnessEvent) event)
								.getBrightness());
						setCurrentState(new LightState(color, currentState
								.getBrightness(), currentState.isWhiteMode(),
								currentState.isOn()));

					}
					break;
				// ColoredModeEvent
				case "ColoredModeEvent":
					setCurrentState(new LightState(currentState.getColor(),
							currentState.getBrightness(), false, currentState
									.isOn()));
					break;
				// WhiteModeEvent
				case "WhiteModeEvent":
					setCurrentState(new LightState(currentState.getColor(),
							currentState.getBrightness(), true, currentState
									.isOn()));
					break;
				// SwitchOnEvent
				case "SwitchOnEvent":
					setCurrentState(new LightState(currentState.getColor(),
							currentState.getBrightness(), currentState
									.isWhiteMode(), true));
					break;
				// SwitchOffEvent
				case "SwitchOffEvent":
					setCurrentState(new LightState(currentState.getColor(),
							currentState.getBrightness(), currentState
									.isWhiteMode(), false));
					break;
				}
			}
		});
	}

	/**
	 * @return the currentState
	 */
	public LightState getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState
	 *            the currentState to set
	 */
	private void setCurrentState(LightState currentState) {
		this.currentState = currentState;
	}

	/**
	 * @return the lights
	 */
	public Lights getLights() {
		return lights;
	}
}
