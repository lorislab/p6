#!/bin/bash

mvn clean install -DtestSkip=true -Dmaven.test.skip=true

docker build --tag lorislab/p6 .

