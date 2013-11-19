package de.toman.milight;

import java.awt.Color;
import java.util.Stack;

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
	/**
	 * The group of lights to be observed.
	 */
	private Lights lights;

	/**
	 * A stack of states the group of lights had, beginning with the current
	 * state.
	 */
	private Stack<LightState> states;

	/**
	 * The color to use when the current color is unknown.
	 */
	private static final MilightColor INITIAL_COLOR = new MilightColor(
			Color.WHITE);

	/**
	 * The brightness value to use when the current brightness value is unknown.
	 */
	private static final float INITIAL_BRIGHTNESS = 1f;

	/**
	 * The flag to use when it is unknwon whether the group of lights is in
	 * colored or white mode.
	 */
	private static final boolean INITIAL_WHITE_MODE = true;

	/**
	 * The flag to use when it is unknown whether the group of lights is
	 * switched on.
	 */
	private static final boolean INITIAL_ON = false;

	/**
	 * This constructor creates a new LightOberserver storing the current and
	 * last state of a given group of lights. The state will be recorded
	 * autmotically, you don't need to call another command than this one.
	 * 
	 * @param lights
	 *            the group of lights to observe
	 */
	public LightObserver(Lights lights) {
		// call super constructor
		super();

		// set attributes
		this.lights = lights;
		states = new Stack<LightState>();
		states.add(new LightState(INITIAL_COLOR, INITIAL_BRIGHTNESS,
				INITIAL_WHITE_MODE, INITIAL_ON));

		// add as listener
		lights.addLightListener(new LightListener() {
			public void lightsChanged(LightEvent event) {
				// ChangeColorEvent
				switch (event.getClass().getSimpleName()) {
				case "ChangeColorEvent":
					setCurrentState(new LightState(((ChangeColorEvent) event)
							.getColor(), states.firstElement().getBrightness(),
							states.firstElement().isWhiteMode(), states
									.firstElement().isOn()));
					break;
				// ChangeBrightnessEvent
				case "ChangeBrightnessEvent":
					if (states.firstElement().isWhiteMode()) {
						// white mode
						setCurrentState(new LightState(states.firstElement()
								.getColor(), ((ChangeBrightnessEvent) event)
								.getBrightness(), states.firstElement()
								.isWhiteMode(), states.firstElement().isOn()));
					} else {
						// colored mode
						MilightColor color = states.firstElement().getColor();
						color.setBrightness(((ChangeBrightnessEvent) event)
								.getBrightness());
						setCurrentState(new LightState(color, states
								.firstElement().getBrightness(), states
								.firstElement().isWhiteMode(), states
								.firstElement().isOn()));

					}
					break;
				// ColoredModeEvent
				case "ColoredModeEvent":
					setCurrentState(new LightState(states.firstElement()
							.getColor(), states.firstElement().getBrightness(),
							false, states.firstElement().isOn()));
					break;
				// WhiteModeEvent
				case "WhiteModeEvent":
					setCurrentState(new LightState(states.firstElement()
							.getColor(), states.firstElement().getBrightness(),
							true, states.firstElement().isOn()));
					break;
				// SwitchOnEvent
				case "SwitchOnEvent":
					setCurrentState(new LightState(states.firstElement()
							.getColor(), states.firstElement().getBrightness(),
							states.firstElement().isWhiteMode(), true));
					break;
				// SwitchOffEvent
				case "SwitchOffEvent":
					setCurrentState(new LightState(states.firstElement()
							.getColor(), states.firstElement().getBrightness(),
							states.firstElement().isWhiteMode(), false));
					break;
				}
			}
		});
	}

	/**
	 * This funtion returns the current state of the observed group of lights.
	 * 
	 * @return the current state of the observed group of lights
	 */
	public LightState getCurrentState() {
		return states.firstElement();
	}

	/**
	 * This function returns a stack of all known states, beginning with the
	 * current one.
	 * 
	 * @return a stack of all known states, beginning with the current one.
	 */
	public Stack<LightState> getStates() {
		return states;
	}

	/**
	 * This private function sets a new current state and backups the last one
	 * into {@link LightObserver#lastState}.
	 * 
	 * @param states
	 *            .firstElement() the new state of the group of lights
	 */
	private void setCurrentState(LightState state) {
		states.add(state);
	}

	/**
	 * This funtion returns the last state of the observed group of lights (i.e.
	 * the current state before the last command was sent to them).
	 * 
	 * @return the last state of the observed group of lights or null if there
	 *         is no known history
	 */
	public LightState getLastState() {
		return states.size() > 1 ? states.get(1) : null;
	}

	/**
	 * This function returns the group of lights observed.
	 * 
	 * @return the group of lights observed
	 */
	public Lights getLights() {
		return lights;
	}
}
