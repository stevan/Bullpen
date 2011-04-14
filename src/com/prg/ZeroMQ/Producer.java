
package com.prg;

public interface MessageProducer {
    void    initialize ( String request );
    void    reset      ();
    boolean hasNext    ();
    String  next       ();
}