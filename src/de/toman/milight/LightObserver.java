package de.toman.milight;

import java.awt.Color;

import de.toman.milight.events.ChangeBrightnessEvent;
import de.toman.milight.events.ChangeColorEvent;
import de.toman.milight.events.LightEvent;
import de.toman.milight.events.LightListener;

public class LightObserver {
	private Lights lights;

	private LightState currentState;

	private static final MilightColor INITIAL_COLOR = new MilightColor(
			Color.WHITE);
	private static final float INITIAL_BRIGHTNESS = 1f;
	private static final boolean INITIAL_WHITE_MODE = true;

	public LightObserver(Lights lights) {
		// call super constructor
		super();

		// set attributes
		this.lights = lights;
		currentState = new LightState(INITIAL_COLOR, INITIAL_BRIGHTNESS,
				INITIAL_WHITE_MODE);

		// add as listener
		lights.addLightListener(new LightListener() {
			public void lightsChanged(LightEvent event) {
				// ChangeColorEvent
				switch (event.getClass().getSimpleName()) {
				case "ChangeColorEvent":
					setCurrentState(new LightState(((ChangeColorEvent) event)
							.getColor(), currentState.getBrightness(),
							currentState.isWhiteMode()));
					break;
				// ChangeBrightnessEvent
				case "ChangeBrightnessEvent":
					setCurrentState(new LightState(currentState.getColor(),
							((ChangeBrightnessEvent) event).getBrightness(),
							currentState.isWhiteMode()));
					break;
				// TODO brightness should be handled different for white/colored
				// mode
				// ColoredModeEvent
				case "ColoredModeEvent":
					setCurrentState(new LightState(currentState.getColor(),
							currentState.getBrightness(), false));
					break;
				// WhiteModeEvent
				case "WhiteModeEvent":
					setCurrentState(new LightState(currentState.getColor(),
							currentState.getBrightness(), true));
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
