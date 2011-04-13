#!perl

system('java', '-Djava.library.path=/usr/local/lib', '-cp', 'zmq.jar:.', 'Worker', $ARGV[0]);