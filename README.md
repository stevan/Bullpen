Bullpen
=======

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

## See Also

The name of this module (a set of workers that publish things through a
central hub to subscribers on the other end) is taken from the infamous
Bullpen of Marvel Comics fame.

* (1979 Bullpen)[http://www.eliotrbrown.com/1979.php]
* (1975 Bullpen)[http://www.flickr.com/photos/26425820@N06/sets/72157626341069312/]
* (Bullpen Bulletins)[http://en.wikipedia.org/wiki/Bullpen_Bulletins]

## Dependencies

See the README.md in the jars/ directory, but basically we depend on
ZeroMQ and JSON-Simple.

## Copyright and License

Copyright (C) 2011 Infinity Interactive, Inc.

(http://www.iinteractive.com)[http://www.iinteractive.com]

This library is free software; you can redistribute it and/or modify
it under the same terms as Perl (Artistic License).

