#!/bin/bash

echo "movie start script!!"

mplayer -ao alsa:device=iec958 %INSTALL_PATH/audio/start.wav&

/usr/local/bin/heyu off A1
/usr/local/bin/heyu off A2
/usr/local/bin/heyu on A3
/usr/local/bin/heyu off A4
/usr/local/bin/heyu on A5
