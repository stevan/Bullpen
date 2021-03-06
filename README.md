# Bullpen

Bullpen is a very simple, but configurable Worker Queue based on ZeroMQ.
It allows multiple Workers to be connected to a single Server, which
itself can be connected to by multiple clients. It creates a ZeroMQ
network that looks something like this drawing below.

    +-----------+
    |   Worker  |
    |-----------|
    | REP | PUB |
    +-----+-----+  { ... $n workers }
       ^    ^
       |    |
       |    +--------+
       +-------+     |
               |     |
               v     v
            +------------------+
            | XREQ | SUB,{...} |
            |------------------|
            |      Server      |
            |------------------|
            |  XREP   |   PUB  |
            +---------+--------+
                ^         ^
                |         |
       +--------+         |
       |     +------------+
       |     |
       v     v
    +-----+-----+
    | REQ | SUB |
    |-----------|
    |   Client  |
    +-----------+  { ... $n clients }

## Logging

Note that we use the java.util.logging package for this, so you can configure
the logging output as needed. There is an example .properties file in the
conf/logging/ directory. Just pass in the value on the command line like this:

    java -Djava.util.logging.config.file=my-logging.properties ...

## See Also

[Bullpen-Client-Perl](https://github.com/stevan/Bullpen-Client-Perl)

## The Name

The name of this module (a set of workers that publish things through a
central hub to subscribers on the other end) is taken from the infamous
Bullpen of Marvel Comics.

* [1979 Bullpen](http://www.eliotrbrown.com/1979.php)
* [1975 Bullpen](http://www.flickr.com/photos/26425820@N06/sets/72157626341069312/)
* [Bullpen Bulletins](http://en.wikipedia.org/wiki/Bullpen_Bulletins)

## Dependencies

See the README.md in the lib/ directory, but basically we depend on
ZeroMQ and JSON-Simple.

## Copyright and License

Copyright (C) 2011 Infinity Interactive, Inc.

[http://www.iinteractive.com](http://www.iinteractive.com)

This library is free software; you can redistribute it and/or modify
it under the same terms as Perl (Artistic License).

