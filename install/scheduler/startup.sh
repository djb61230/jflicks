#!/bin/bash

cd @@INSTALL_DIR@@
rm -rf felix-cache
mkdir felix-cache
java -jar bin/felix.jar
