
package com.iinteractive.Bullpen;

import java.util.HashMap;

public interface Producer {
    void    configure   ( HashMap config ) throws Exception;
    void    initialize  ( String request ) throws Exception;
    void    reset       ();
    boolean hasNext     () throws Exception;
    String  next        () throws Exception;
    String  formatError ( Exception e );
}