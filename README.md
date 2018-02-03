# MiLight API

This is an API for [MiLight light bulbs](http://www.milight.com/) written in
Java. Other brands of light bulbs are also compatible, for instance
EasyBulb, LimitlessLED, etc. With this API it is easy to switch lights on
and off, change color and brightness and other settings. Additionally, tools
like an ambient light effect, fading animations and music visualizations are
included. Only a WiFi box and some light bulbs are needed to run this API.

Pull requests as well as notifications about usages of this API are always
appreciated. Documentation of all classes is available at [GitHub
Pages](https://stoman.github.io/MilightAPI/de/toman/milight/package-summary.html).

## Basic Usage

Connect your WiFi box to the lights as described in the manual. Find the
local IP address of your box in the local network. This information can be
found at the interface of your router, for instance. Now you can run the
API on a machine connected to the same network.

To connect to your WiFi box create a
[`WiFiBox`](https://stoman.github.io/MilightAPI/de/toman/milight/WiFiBox.html)
object in your Java application:

```
String ip = "192.168.1.42"; //add your custom IP here
WiFiBox box = new WiFiBox(ip);
```

[Additional constructors with custom ports or other types of
addresses](https://stoman.github.io/MilightAPI/de/toman/milight/WiFiBox.html#constructor.summary)
are also available. Individual groups of lights can be created like this:

```
Lights groupA = box.getLights(1);
Lights groupB = new Lights(ip, 2);
```

All commands available on a remote control or an app connected to this box
are now available, for example:

```
//switch all lights on
box.on();
//switch group 4 of
box.off(4);
//switch group 2 to disco mode
box.discoMode(2);
//make the disco mode faster
box.discoModeFaster(2);
//switch the color of group 3 to blue
box.color(3, new MilightColor(Color.BLUE));
//dim group 1
box.brightness(1, MilightColor.MIN_BRIGHTNESS);
```

Many more commands are available, check the
[docs](https://stoman.github.io/MilightAPI/de/toman/milight/WiFiBox.html) to
read more. To display colors the lights change their brightness and hue as
well switch between white and color mode to resemble the chosen color as
close as possible.

## Ambient Light and Music Visualizer

Lights can be used to create an ambient light effect. This means the lights
will resemble the colors of the screen of your PC. Use this to lighten your
room fitting to a movie you are watching or similar.
[`AmbientLight`](https://stoman.github.io/MilightAPI/de/toman/milight/AmbientLight.html)
can be used like this:

```
AmbientLight ambient = new AmbientLight(groupA);//or some other Lights object
ambient.start();
```

The effect can be stopped with

```
ambient.stop();
```

More configuration is available and described in the documentation.

Similarly, a visualization of the music played on your machine can be
created. The source to read the music from needs to be available as a
[`TargetDataLine`](https://docs.oracle.com/javase/8/docs/api/javax/sound/sampled/TargetDataLine.html).
The function
[`MusicVisualizer.getDefaultLine()`](https://stoman.github.io/MilightAPI/de/toman/milight/MusicVisualizer.html#getDefaultLine--)
is available to find the most common data lines. The visualizer can be
started like this:

```
MusicVisualizer vis = new MusicVisualizer(groupB, MusicVisualizer.getDefaultLine());
vis.start();
```

The effect can be stopped with

```
vis.stop();
```

## Timer

There is a
[timer](https://stoman.github.io/MilightAPI/de/toman/milight/Timer.html)
available to fade from one color or brightness to another or switch the
lights off after a certain amount of time. Timers can be used like this:

```
Timer timer = new Timer(
	groupA, //the lights to control,
	10000, //animation time in milliseconds
	new MilightColor(Color.BLUE), //start color or brightness
	new MilightColor(Color.BLACK), //target color or brightness
	true //switch the lights off after the animation	
);
timer.start();
```

## Watching Events

All commands sent out by the API are available as events to make it possible
to react to commands sent out. Add a
[`LightListener`](https://stoman.github.io/MilightAPI/de/toman/milight/events/LightListener.html)
like this:

```
groupA.addLightListener(new LightListener() {
	public void lightsChanged(LightEvent e) {
		//work with the event
	}
});
```

The following events are available:

* [ChangeBrightnessEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/ChangeBrightnessEvent.html)
* [ChangeColorEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/ChangeColorEvent.html)
* [ColoredModeEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/ColoredModeEvent.html)
* [DiscoModeEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/DiscoModeEvent.html)
* [DiscoModeFasterEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/DiscoModeFasterEvent.html)
* [DiscoModeSlowerEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/DiscoModeSlowerEvent.html)
* [SwitchOffEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/SwitchOffEvent.html)
* [SwitchOnEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/SwitchOnEvent.html)
* [WhiteModeEvent](https://stoman.github.io/MilightAPI/de/toman/milight/events/WhiteModeEvent.html)

Similarly, there is a
[`TimerListener`](https://stoman.github.io/MilightAPI/de/toman/milight/events/TimerListener.html)
available to catch the event that the timer has finished its animation.

There is no way to read the current state of a light from the WiFi box.
However, we can track all commands sent by the API to compute the current
state. To automatically track the state of a light use the
[`LightState`](https://stoman.github.io/MilightAPI/de/toman/milight/LightState.html)
class. Note, that changes made by another remote control or app are not
noticed by this class.

## Author

This library is written by [Stefan Toman](https://github.com/stoman) and
available under the [MIT
license](https://github.com/stoman/MilightAPI/blob/develop/LICENSE).
