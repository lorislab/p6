#!/bin/bash

mvn clean install -DskipTests=true -Dmaven.javadoc.skip=true

docker build --tag lorislab/p6 .

