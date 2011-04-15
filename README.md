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


## Dependencies

See the README.md in the jars/ directory, but basically we depend on
ZeroMQ and JSON-Simple.

## Copyright and License

Copyright (C) 2011 Infinity Interactive, Inc.

(http://www.iinteractive.com)

This library is free software; you can redistribute it and/or modify
it under the same terms as Perl (Artistic License).

