package de.toman.milight;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * With this class you can adjust your lights in a way that they have the same
 * color and brightness as your screen. The screen is captured regularly, some
 * pixels get extracted, the average color is computed and then sent to the
 * group of lights. This is nice when watching movies and supports their
 * athmosphere. Use the {@link AmbientLight#start()} function to run this class
 * in a seperate thread.
 * 
 * @author Stefan Toman (toman@tum.de)
 */
public class AmbientLight implements Runnable {
	/**
	 * The group of lights to be controlled by the ambient light.
	 */
	private Lights lights;

	/**
	 * The time before adjusting the color of the light again when it was just
	 * set in milli seconds. This value should be at least
	 * 3*WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES since three messages are sent in
	 * every iteration (selection of the right group, set hue value, set
	 * brightness value).
	 */
	private long sleepInterval;

	/**
	 * The number of pixels to take into account in horizontal direction. The
	 * pixels are distributed with equal distances over the screen with no
	 * pixels being read on the left or right border of the screen. The
	 * algorithm computes the arithmetic mean of the colors at these pixels and
	 * sends the result to the group of lights.
	 */
	private int nx;

	/**
	 * The number of pixels to take into account in vertical direction. The
	 * pixels are distributed with equal distances over the screen with no
	 * pixels being read on the top or bottom border of the screen. The
	 * algorithm computes the arithmetic mean of the colors at these pixels and
	 * sends the result to the group of lights.
	 */
	private int ny;

	/**
	 * True if the thread should stop and not send any more commands.
	 */
	private boolean stopped;

	/**
	 * This constructor creates a new ambient light. Use the
	 * {@link AmbientLight#start()} function to run it in a seperate thread.
	 * 
	 * @param lights
	 *            is the group of lights to control
	 * @param nx
	 *            is the number of pixels to be extracted in each loop in
	 *            horizontal direction
	 * @param ny
	 *            is the number of pixels to be extracted in each loop in
	 *            vertical direction
	 * @param sleepInterval
	 *            is the time to sleep between to screen captures in milli
	 *            seconds
	 */
	public AmbientLight(Lights lights, int nx, int ny, long sleepInterval) {
		super();
		this.lights = lights;
		this.nx = nx;
		this.ny = ny;
		this.sleepInterval = sleepInterval;
	}

	/**
	 * This constructor creates a new ambient light capturing the screen as
	 * often as possible. Use the {@link AmbientLight#start()} function to run
	 * it in a seperate thread.
	 * 
	 * @param lights
	 *            is the group of lights to control
	 * @param nx
	 *            is the number of pixels to be extracted in each loop in
	 *            horizontal direction
	 * @param ny
	 *            is the number of pixels to be extracted in each loop in
	 *            vertical direction
	 */
	public AmbientLight(Lights lights, int nx, int ny) {
		// sleep for 3*WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES ms since there are
		// three messages send in each loop (select the group of lights, set hue
		// value, set brightness value)
		this(lights, nx, ny, 3 * WiFiBox.MIN_SLEEP_BETWEEN_MESSAGES);
	}

	/**
	 * This constructor creates a new ambient light capturing the screen as
	 * often as possible and extracting 9 pixels (a 3x3 grid) in each loop. Use
	 * the {@link AmbientLight#start()} function to run it in a seperate thread.
	 * 
	 * @param lights
	 *            is the group of lights to control
	 */
	public AmbientLight(Lights lights) {
		this(lights, 3, 3);
	}

	/**
	 * This functions runs a loop to set the ambient light until the
	 * {@link AmbientLight#stop()} function is called. In each iteration a
	 * capture of the screen is made, some pixels according to the
	 * {@link AmbientLight#nx} and {@link AmbientLight#ny} values are extracted,
	 * the average of these colors is computed and then this color is sent to
	 * the controlled group of lights. Use {@link AmbientLight#start()} to run
	 * this function in a seperate thread.
	 */
	public void run() {
		try {
			Robot robot = new Robot();
			while (!stopped) {
				// capture a new image
				BufferedImage image = robot.createScreenCapture(new Rectangle(
						Toolkit.getDefaultToolkit().getScreenSize()));

				// extract some pixels and compute the average of the colors
				Color color = averageColor(extractColors(image, nx, ny));

				// send the color to the lights
				lights.colorAndBrightness(color);

				// wait for next iteration
				Thread.sleep(sleepInterval);
			}
		} catch (AWTException e) {
			// exception? => stop
			stop();
		} catch (InterruptedException e) {
			// exception? => stop
			stop();
		}
	}

	/**
	 * This function extracts the colors of some pixels from a BufferedImage.
	 * The pixels are computed with equal distances with no pixels being on the
	 * border of the image. The set of all hese colors is returned.
	 * 
	 * @param image
	 *            is the image where the colors should be extracted
	 * @param nx
	 *            is the number of pixels to be read in horizontal direction
	 * @param ny
	 *            is the number of pixels to be read in vertical direction
	 * @return a set containing the extracted colors
	 */
	public static Set<Color> extractColors(BufferedImage image, int nx, int ny) {
		// initialize return value
		Set<Color> ret = new HashSet<Color>();

		// extract pixels
		for (int x = 1; x <= nx; x++) {
			for (int y = 1; y <= ny; y++) {
				ret.add(new Color(image.getRGB((x * (image.getWidth() - 1))
						/ (nx + 1), (y * (image.getHeight() - 1)) / (ny + 1))));
			}
		}

		// return
		return ret;
	}

	/**
	 * This function computes the average of a set of colors. To do this, the
	 * arithmetic mean value is computed for red, green and blue components
	 * individually.
	 * 
	 * @param colors
	 *            is the set of colors to compute the average of
	 * @return the average color
	 */
	public static Color averageColor(Set<Color> colors) {
		// initialize variables
		long r = 0, g = 0, b = 0;
		int n = 0;

		// compute sums of values
		for (Color color : colors) {
			r += color.getRed();
			g += color.getGreen();
			b += color.getBlue();
			n++;
		}

		// return average color
		return new Color(r / 256f / n, g / 256f / n, b / 256f / n);
	}

	/**
	 * This function stops the thread running the ambient light. The thread can
	 * be resumed by calling start again.
	 * 
	 * @see AmbientLight#start()
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * This function starts the ambient light in a new thread.
	 */
	public void start() {
		stopped = false;
		new Thread(this).start();
	}
}
