#!/bin/bash

echo "theater open script!!"

mplayer -ao alsa:device=iec958 %INSTALL_PATH/audio/welcome.wav&

/usr/local/bin/heyu on A1
/usr/local/bin/heyu on A2
/usr/local/bin/heyu on A3
/usr/local/bin/heyu off A4
/usr/local/bin/heyu on A5
