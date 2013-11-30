package de.toman.milight.events;

import de.toman.milight.Timer;

/**
 * This is an interface for listening on a {@link Timer} instance and getting
 * notified when the timer has finished its animation. Instances of classes
 * implementing this interface may be added by
 * {@link Timer#addTimerListener(TimerListener)}.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public interface TimerListener {

	/**
	 * This function is called when the timer has finished its animation.
	 */
	public void timerReady();
}
