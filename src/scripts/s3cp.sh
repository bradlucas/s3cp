#!/bin/bash

echo $*

INSTALL_PATH=$HOME/work/s3cp
VERSION=0.1.9

java -jar $INSTALL_PATH/dist/s3cp-cmdline-$VERSION.jar $*
