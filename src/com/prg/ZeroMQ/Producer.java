
package com.prg.ZeroMQ;

public interface Producer {
    void    initialize ( String request );
    void    reset      ();
    boolean hasNext    ();
    String  next       ();
}