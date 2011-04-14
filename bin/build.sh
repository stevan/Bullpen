#!/bin/sh

rm -rf lib/*

javac -d lib/ -cp jars/zmq.jar:jars/json_simple.jar src/com/prg/*.java src/com/prg/ZeroMQ/*.java src/com/prg/ZeroMQ/test/*.java