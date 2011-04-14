#!perl

system('java', '-Djava.library.path=/usr/local/lib', '-cp', 'zmq.jar:.', 'WorkerRunner', $ARGV[0]);