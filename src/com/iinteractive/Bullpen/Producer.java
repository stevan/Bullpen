
package com.iinteractive.Bullpen;

import java.util.HashMap;

public interface Producer {
    void    configure ( HashMap config );
    void    initialize ( String request );
    void    reset      ();
    boolean hasNext    ();
    String  next       ();
}