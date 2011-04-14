
package com.prg.ZeroMQ.test;

import java.util.HashMap;
import com.prg.*;
import com.prg.ZeroMQ.*;

public class SimpleMessageProducer implements Producer {

    private int num_messages = 100;
    private int message_counter = 0;
    private Logger logger;

    public SimpleMessageProducer () {
        logger = new Logger ();
    }

    public SimpleMessageProducer ( int _num_messages ) {
        this();
        num_messages = _num_messages;
    }

    public void configure ( HashMap config ) {
        if ( config.containsKey("num_messages") ) {
            num_messages = ((Long) config.get("num_messages")).intValue();
        }
    }

    public void initialize ( String request ) {
        logger.log("got request=(" + request + ")");
    }

    public void reset () {
        message_counter = 0;
    }

    public boolean hasNext () {
        return message_counter < num_messages;
    }

    public String next () {
        message_counter++;
        return "[" + message_counter + "]";
    }
}