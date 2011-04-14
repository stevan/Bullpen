
package com.prg;

public interface MessageProducer {

    void initialize ( String request );

    boolean hasNext();
    String next();
}