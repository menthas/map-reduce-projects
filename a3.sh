#!/bin/bash

cd "$(dirname "$0")"

if [ ! -f ./build/libs/map-reduce-projects-all-1.0.jar ]; then
    gradle fatJar
fi

java -cp ./build/libs/map-reduce-projects-all-1.0.jar edu.neu.cs6240.a3.Main "$@"
