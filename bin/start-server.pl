#!perl

system(
    'java',
    '-Djava.library.path=/usr/local/lib',
    '-cp', 'jars/zmq.jar:jars/json_simple.jar:lib',
    'com.iinteractive.Bullpen.Runner',
    'Server',
    $ARGV[0]
);