IPFire is a linux-based router distribution.

This project provides a client-side tray icon showing the state of the current internet connection (download/upload KB/s).

See the original forum thread for more details: http://forum.ipfire.org/index.php?topic=3594.0



### requirements: ###
  1. installed Java Runtime Edition (JRE) version 1.6 -> download e.g. from http://java.com/, but it is commonly installed already (see troubleshooting)
  1. an operating system supporting a system tray (Windows, Linux with Gnome etc.)

### troubleshooting: ###
  * Windows7 / Vista: tray icon does not show up: click on the small "up" arrow and enable the icon
  * doubleclicking IPFireTray-1.x.jar does not start the program: see requirements! _.jar_ files should be opened by the "java" or "javaw" command
  * icon is yellow all the time: check your connection settings in the settings.properties file, or you are currently not connected


### how it works: ###
Similar to the IPFire admin web interface, the program reads the file _"/cgi-bin/speed.cgi"_ to gather the necessary data.
This is why it (currently) needs the admin login data, without changing the vanilla IPFire interface.
Depending on the configured refresh interval the small tray icon is continuously recalculated and the icon refreshes.

### tested with the following systems: ###
  * Windows7 x64, Sun JRE 1.6
  * Ubuntu 10.10 x64, OpenJDK