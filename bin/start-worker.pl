#!perl

system(
    'java',
    '-Djava.library.path=/usr/local/lib',
    '-cp', 'lib/zmq.jar:lib/json_simple.jar:build',
    'com.iinteractive.Bullpen.Runner',
    'Worker',
    $ARGV[0]
);