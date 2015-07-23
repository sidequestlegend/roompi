https://www.raspberrypi.org/forums/viewtopic.php?f=37&t=116051&p=791030&sid=2fbddc9e9dc17e5dcd31ae07651f6ce3#p791030

Here we have a video of the android app remote controlling the roompi. Basic functionality currently but I hope to add HUD features making use of the variety of sensors in the Roomba.

http://youtu.be/MkhVH4AGfqw

Here we have some photos of the actual outfit. Will probably always be a work in progress. 

https://www.flickr.com/photos/134112902@N03/shares/UeMW6P

Features:
* Auto detection on most networks using UDP broadcast magic packet
* MJPEG streaming from the raspicam to the android device - possibly other ways to do this but latency was hard to tie down. 
* 640x480 @ 25fps. I hope to improve on this with better wifi equipment. 
* Completely wireless, even the PI+camera module is powered directly from the roomba battery (not using the SCI port but a direct line. regulated down to 5v with this - http://shop.pimoroni.com/products/mopi-mobile-pi-power but im sure any voltage regulator would do.)
* Control all cleaning modes of the roomba aswell as docking. 
* Virtual Joystick for making use of the awesomely precise actuators in the roomba. 
* Play music on the roomba! I currently have the app programmed to play several super mario tunes.
