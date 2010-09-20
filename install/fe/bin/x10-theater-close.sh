#!/bin/bash

echo "theater close script!!"

mplayer -ao alsa:device=iec958 %INSTALL_PATH/audio/close.wav&

/usr/local/bin/heyu off A1
/usr/local/bin/heyu on A2
/usr/local/bin/heyu off A3
/usr/local/bin/heyu on A4
/usr/local/bin/heyu off A5
