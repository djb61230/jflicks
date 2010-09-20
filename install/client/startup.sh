#!/bin/bash

cd %INSTALL_PATH/client
rm -rf felix-cache
mkdir felix-cache
%JAVA_HOME/bin/java -jar bin/felix.jar
