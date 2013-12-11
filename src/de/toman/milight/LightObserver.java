package de.toman.milight;

import java.awt.Color;
import java.io.IOException;
import java.util.EmptyStackException;
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
			new Color(Color.HSBtoRGB(0.0f, 1.0f, 1.0f)));

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
			long lastEvent = 0;

			public void lightsChanged(LightEvent event) {
				
				synchronized (states) {
					LightState currentState = getCurrentState();

					// remove last state if there was a call to this function in
					// the last second
					if (System.currentTimeMillis() - lastEvent < 3 * WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES) {
						states.pop();
					}
					lastEvent = System.currentTimeMillis();

					switch (event.getClass().getSimpleName()) {
					// ChangeColorEvent
					case "ChangeColorEvent":
						setCurrentState(new LightState(
								((ChangeColorEvent) event).getColor(),
								currentState.getBrightness(), currentState
										.isWhiteMode(), currentState.isOn()));
						break;
					// ChangeBrightnessEvent
					case "ChangeBrightnessEvent":
						if (currentState.isWhiteMode()) {
							// white mode
							setCurrentState(new LightState(currentState
									.getColor(),
									((ChangeBrightnessEvent) event)
											.getBrightness(), currentState
											.isWhiteMode(), currentState.isOn()));
						} else {
							// colored mode
							MilightColor color = currentState.getColor();
							color.setBrightness(((ChangeBrightnessEvent) event)
									.getBrightness());
							setCurrentState(new LightState(color, currentState
									.getBrightness(), currentState
									.isWhiteMode(), currentState.isOn()));

						}
						break;
					// ColoredModeEvent
					case "ColoredModeEvent":
						setCurrentState(new LightState(currentState.getColor(),
								currentState.getBrightness(), false,
								currentState.isOn()));
						break;
					// WhiteModeEvent
					case "WhiteModeEvent":
						setCurrentState(new LightState(currentState.getColor(),
								currentState.getBrightness(), true,
								currentState.isOn()));
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
			}
		});
	}

	/**
	 * This funtion returns the current state of the observed group of lights.
	 * 
	 * @return the current state of the observed group of lights
	 */
	public LightState getCurrentState() {
		return states.lastElement();
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
		if (states.isEmpty() || !state.equals(getCurrentState())) {
			states.add(state);
		}
	}

	/**
	 * This funtion returns the last state of the observed group of lights (i.e.
	 * the current state before the last command was sent to them).
	 * 
	 * @return the last state of the observed group of lights or null if there
	 *         is no known history
	 * @throws EmptyStackException
	 *             if the stack of states is empty
	 */
	public LightState getLastState() throws EmptyStackException {
		// check whether the stack is empty
		if (states.size() == 0) {
			throw new EmptyStackException();
		}

		// find return value
		return states.get(1);
	}

	/**
	 * This function removes the youngest state from the stack and restores it
	 * to the group of lights observerd by this instance.
	 * 
	 * @throws IOException
	 *             if the message to the WiFiBox could not be sent
	 * @throws EmptyStackException
	 *             if the stack of states is empty
	 */
	public void restore() throws IOException, EmptyStackException {
		// check whether the stack is empty
		if (states.size() == 0) {
			throw new EmptyStackException();
		}

		// remove first state
		LightState state = states.pop();

		// set state
		state.restore(lights);
	}

	/**
	 * This function returns the group of lights observed.
	 * 
	 * @return the group of lights observed
	 */
	public Lights getLights() {
		return lights;
	}
	
	/**
	 * This function describes the objet as a string. Use this for debugging.
	 * 
	 * @returns a string description of the instance
	 */
	public String toString() {
		String ret = "[LightObserver for: "+lights.toString()+", states: [";
		for (LightState state: states) {
			ret += "\n\t"+state.toString();
		}
		ret += "\n]]";
		return ret;
	}
}
