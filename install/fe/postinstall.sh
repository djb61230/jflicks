#!/bin/bash

cd %INSTALL_PATH
%JAVA_HOME/bin/java -jar bin/jflicks-install.jar -installpath %INSTALL_PATH -autoname JFlicks_FE.desktop -remote %lircrc $@
