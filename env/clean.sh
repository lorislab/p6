#!/bin/bash

docker container rm p6
docker container rm pgadmin
docker container rm postgresdb
docker volume rm env_dbvolume
