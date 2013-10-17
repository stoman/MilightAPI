package de.toman.milight;

import java.io.IOException;

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
	 * The current brightness level of the light (the last one that was send, it
	 * may be changed by another controller without getting notice).
	 */
	private int brightnessLevelCurrent;

	/**
	 * The brightness level the lights should have in the end.
	 */
	private int brightnessLevelGoal;

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
	 * This constructor creates a new timer that dims a group of lights during a
	 * longer time. The timer will not be started immediately, you need to call
	 * the function {@link Timer#start()} to run the timer.
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
		this.brightnessLevelCurrent = brightnessLevelStart;
		this.brightnessLevelGoal = brightnessLevelGoal;
		this.switchOff = switchOff;

		// initialize lights
		lights.on();
		lights.brightness(brightnessLevelStart);
	}

	/**
	 * This constructor creates a new timer that dims a group of lights during a
	 * longer time and switches the group of lights off in the end. The timer
	 * will not be started immediately, you need to call the function
	 * {@link Timer#start()} to run the timer.
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
	 * to run the timer.
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
			while (brightnessLevelCurrent != brightnessLevelGoal
					&& timeRemaining > 0 && stopped == false) {
				// compute next values
				int brightnessLevelNext = brightnessLevelCurrent
						+ (int) Math.signum(brightnessLevelGoal
								- brightnessLevelCurrent);
				long timeToSleep = timeRemaining
						/ (Math.abs(brightnessLevelGoal
								- brightnessLevelCurrent) + 1);

				// adjust attributes
				brightnessLevelCurrent = brightnessLevelNext;
				timeRemaining -= timeToSleep;

				// send commands
				lights.brightness(brightnessLevelCurrent);

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
}
