#!/bin/sh

docker build . -t web-wizard-chamber
docker run --rm --name=web-wizard-chamber -p 8088:8080 web-wizard-chamber
