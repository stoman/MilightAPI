package de.toman.milight.events;

import java.util.EventListener;

/**
 * This interface should be implemented by a listeners for events from the
 * de.toman.milight.events package. The
 * {@link LightListener#lightsChanged(LightEvent)} function must be implemented
 * to react on events.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public interface LightListener extends EventListener {

	/**
	 * This function needs to be implemented to react on events. The same
	 * function wil be called for every type of events. To handle each of them
	 * on their own switch depending on the class of the event instance, for
	 * instance {@link SwitchOnEvent} or {@link ChangeColorEvent}.
	 * 
	 * @param e
	 *            is the event that occured
	 */
	public void lightsChanged(LightEvent e);
}
