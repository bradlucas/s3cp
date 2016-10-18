#!/bin/bash

echo $*

INSTALL_PATH=$HOME/work/github/s3cp
VERSION=1.0.1

java -jar $INSTALL_PATH/dist/s3cp-cmdline-$VERSION.jar $*
