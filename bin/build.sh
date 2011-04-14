#!/bin/sh

javac -cp zmq.jar com/prg/*.java
javac -cp zmq.jar:. WorkerRunner.java
javac -cp zmq.jar:. ServerRunner.java