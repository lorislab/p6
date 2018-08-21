#!/bin/bash

docker-compose up -d

docker logs --follow p6
