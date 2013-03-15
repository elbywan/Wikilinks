#!/bin/bash

rm -R bin
mkdir bin
javac -d ./bin -encoding UTF-8 ./src/graph/*.java ./src/main/*.java ./src/options/*.java ./src/parsing/*.java ./src/requests/*.java ./src/testing/*.java ./src/utils/*.java ./src/gui/*.java