package de.toman.milight;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.toman.milight.events.TimerListener;

/**
 * This class dims a group of lights over time from one brightness level to
 * another in a smooth way. It supports the brightness value to get bigger or
 * smaller and can switch the lights of in the end of the animation. The
 * animation can run over a long time and is carried out in a seperate thread.
 * Use the {@link Timer#start()} function to run this class in a seperate
 * thread.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class Timer implements Runnable {
	/**
	 * The lights that should be controlled.
	 */
	private Lights lights;

	/**
	 * The time until the sleep mode should end in milliseconds.
	 */
	private long timeRemaining;

	/**
	 * The current color of the light (the last one that was send, it may be
	 * changed by another controller without getting notice).
	 */
	private MilightColor colorCurrent;

	/**
	 * The color the lights should have in the end.
	 */
	private MilightColor colorGoal;

	/**
	 * True if the lights should be switched off when the countdown has
	 * finished.
	 */
	private boolean switchOff;

	/**
	 * True if the thread should stop and not send any more commands.
	 */
	private boolean stopped;

	/**
	 * The set of all TimerListeners observing this instance.
	 */
	private Set<TimerListener> timerListeners;

	/**
	 * The amount of time to sleep between two adjustments in milliseconds.
	 */
	private int sleepPerCycle;

	/**
	 * The default amount of time to sleep between two adjustments in
	 * milliseconds.
	 */
	public static final int SLEEP_PER_CYCLE_DEFAULT = 5000;

	/**
	 * This constructor creates a new timer that changes the color and
	 * brightness of a group of lights during a longer time. The timer will not
	 * be started immediately, you need to call the function
	 * {@link Timer#start()} to run the timer. The amount of time to sleep
	 * between two adjustments is set to {@link Timer#SLEEP_PER_CYCLE_DEFAULT}.
	 * You can change it with {@link Timer#setSleepPerCycle(int)}.
	 * 
	 * @param lights
	 *            is the group of lights to dim
	 * @param time
	 *            is the overall time until the timer should finish in
	 *            milliseconds
	 * @param colorStart
	 *            is color to start with
	 * @param colorGoal
	 *            is color the group of lights should have in the end
	 * @param switchOff
	 *            is true if the lights should be switched off after the
	 *            animation ends
	 * @throws IOException
	 *             if the message to the WiFi box could not be sent
	 */
	public Timer(Lights lights, long time, MilightColor colorStart,
			MilightColor colorGoal, boolean switchOff) throws IOException {
		super();

		// set attributes
		this.lights = lights;
		this.timeRemaining = time;
		this.colorCurrent = colorStart;
		this.colorGoal = colorGoal;
		this.switchOff = switchOff;
		this.timerListeners = new HashSet<TimerListener>();
		this.sleepPerCycle = SLEEP_PER_CYCLE_DEFAULT;

		// initialize lights
		lights.on();
		lights.colorAndBrightness(colorStart);
	}

	/**
	 * This constructor creates a new timer that dims a group of lights during a
	 * longer time. The timer will not be started immediately, you need to call
	 * the function {@link Timer#start()} to run the timer. The amount of time
	 * to sleep between two adjustments is set to
	 * {@link Timer#SLEEP_PER_CYCLE_DEFAULT}. You can change it with
	 * {@link Timer#setSleepPerCycle(int)}.
	 * 
	 * @param lights
	 *            is the group of lights to dim
	 * @param time
	 *            is the overall time until the timer should finish in
	 *            milliseconds
	 * @param brightnessLevelStart
	 *            is the brightness level to start with (between
	 *            MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS)
	 * @param brightnessLevelGoal
	 *            is the brightness level the group of lights should have in the
	 *            end (between MilightColor.MIN_BRIGHTNESS and
	 *            MilightColor.MAX_BRIGHTNESS)
	 * @param switchOff
	 *            is true if the lights should be switched off after the
	 *            animation ends
	 * @throws IOException
	 *             if the message to the WiFi box could not be sent
	 * @throws IllegalArgumentException
	 *             if the brightness levels to start with or reach in the end
	 *             are not between MilightColor.MIN_BRIGHTNESS and
	 *             MilightColor.MAX_BRIGHTNESS
	 */
	public Timer(Lights lights, long time, int brightnessLevelStart,
			int brightnessLevelGoal, boolean switchOff) throws IOException,
			IllegalArgumentException {
		super();

		// check arguments
		if (brightnessLevelStart < MilightColor.MIN_BRIGHTNESS
				|| brightnessLevelStart > MilightColor.MAX_BRIGHTNESS) {
			throw new IllegalArgumentException(
					"The brightness level to start with should be between MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS");
		}
		if (brightnessLevelGoal < MilightColor.MIN_BRIGHTNESS
				|| brightnessLevelGoal > MilightColor.MAX_BRIGHTNESS) {
			throw new IllegalArgumentException(
					"The brightness level to reach in the end should be between MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS");
		}

		// set attributes
		this.lights = lights;
		this.timeRemaining = time;
		this.colorCurrent = new MilightColor(Color.WHITE);
		this.colorCurrent.setMilightBrightness(brightnessLevelStart);
		this.colorGoal = new MilightColor(Color.WHITE);
		this.colorGoal.setMilightBrightness(brightnessLevelGoal);
		this.switchOff = switchOff;
		this.timerListeners = new HashSet<TimerListener>();
		this.sleepPerCycle = SLEEP_PER_CYCLE_DEFAULT;

		// initialize lights
		lights.on();
		lights.brightness(brightnessLevelStart);
	}

	/**
	 * This constructor creates a new timer that dims a group of lights during a
	 * longer time and switches the group of lights off in the end. The timer
	 * will not be started immediately, you need to call the function
	 * {@link Timer#start()} to run the timer. The amount of time to sleep
	 * between two adjustments is set to {@link Timer#SLEEP_PER_CYCLE_DEFAULT}.
	 * You can change it with {@link Timer#setSleepPerCycle(int)}.
	 * 
	 * @param lights
	 *            is the group of lights to dim
	 * @param time
	 *            is the overall time until the timer should finish in
	 *            milliseconds
	 * @param brightnessLevelStart
	 *            is the brightness level to start with (between
	 *            MilightColor.MIN_BRIGHTNESS and MilightColor.MAX_BRIGHTNESS)
	 * @param brightnessLevelGoal
	 *            is the brightness level the group of lights should have in the
	 *            end (between MilightColor.MIN_BRIGHTNESS and
	 *            MilightColor.MAX_BRIGHTNESS)
	 * @throws IOException
	 *             if the message to the WiFi box could not be sent
	 * @throws IllegalArgumentException
	 *             if the brightness levels to start with or reach in the end
	 *             are not between MilightColor.MIN_BRIGHTNESS and
	 *             MilightColor.MAX_BRIGHTNESS
	 */
	public Timer(Lights lights, long time, int brightnessLevelStart,
			int brightnessLevelGoal) throws IOException,
			IllegalArgumentException {
		this(lights, time, brightnessLevelStart, brightnessLevelGoal, true);
	}

	/**
	 * This constructor creates a new timer that dims a group of lights from
	 * full brightness until switched off in the end. The timer will not be
	 * started immediately, you need to call the function {@link Timer#start()}
	 * to run the timer. The amount of time to sleep between two adjustments is
	 * set to {@link Timer#SLEEP_PER_CYCLE_DEFAULT}. You can change it with
	 * {@link Timer#setSleepPerCycle(int)}.
	 * 
	 * @param lights
	 *            is the group of lights to dim
	 * @param time
	 *            is the overall time until the timer should finish in
	 *            milliseconds
	 * @throws IOException
	 *             if the message to the WiFi box could not be sent
	 */
	public Timer(Lights lights, long time) throws IOException,
			IllegalArgumentException {
		this(lights, time, MilightColor.MAX_BRIGHTNESS,
				MilightColor.MIN_BRIGHTNESS, true);
	}

	/**
	 * This function runs the timer with the settings given in the constructor
	 * resp. setters. Use the function start to run this in a seperate thread.
	 * 
	 * @see Timer#start()
	 */
	@Override
	public void run() {
		try {
			while (colorCurrent != colorGoal && timeRemaining > 0
					&& stopped == false) {
				// compute next values
				long timeToSleep = Math.min(sleepPerCycle, timeRemaining);
				MilightColor color = colorCurrent.getTransition(colorGoal,
						timeRemaining / timeToSleep);

				// adjust attributes
				colorCurrent = color;
				timeRemaining -= timeToSleep;

				// send commands
				lights.colorAndBrightness(color);

				// sleep
				Thread.sleep(timeToSleep);
			}

			if (switchOff) {
				lights.off();
			}
		} catch (IOException e) {
			// exception while communicating with the lights
			stop();
		} catch (InterruptedException e) {
			// exception while sleeping
			stop();
		} finally {
			notifyTimerListeners();
		}

	}

	/**
	 * This function stops the thread running the timer. The timer can be
	 * resumed where it was interrupted by calling start again.
	 * 
	 * @see Timer#start()
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * This function starts the timer in a new thread.
	 */
	public void start() {
		stopped = false;
		new Thread(this).start();
	}

	/**
	 * Set the amount of time to sleep between two cycles.
	 * 
	 * @param sleepPerCycle
	 *            is the amount of time to sleep between two cycles in
	 *            milliseconds.
	 */
	public void setSleepPerCycle(int sleepPerCycle) {
		this.sleepPerCycle = sleepPerCycle;
	}

	/**
	 * Use this function to add a new listener to the timer. Listeners will be
	 * notified when the timer has finished its animation.
	 * 
	 * @param listener
	 *            is the listener to add
	 */
	public void addTimerListener(TimerListener listener) {
		timerListeners.add(listener);
	}

	/**
	 * This function removes a listener from this timer which was added before
	 * by {@link Timer#addTimerListener(TimerListener)}.
	 * 
	 * @param listener
	 *            is the listener to remove
	 */
	public void removeTimerListener(TimerListener listener) {
		timerListeners.remove(listener);
	}

	/**
	 * This function notifies all TimerListeners listening on this group of
	 * lights.
	 */
	private void notifyTimerListeners() {
		for (TimerListener listener : timerListeners) {
			listener.timerReady();
		}
	}
}
