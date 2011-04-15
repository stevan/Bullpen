#!/bin/sh

rm -rf lib/*

javac -d lib/ -cp jars/zmq.jar:jars/json_simple.jar src/com/iinteractive/Bullpen/*.java src/com/iinteractive/Bullpen/test/*.java
