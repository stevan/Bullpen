
package com.iinteractive.Bullpen;

import java.util.logging.*;
import org.zeromq.ZMQ;

public class Core {

    protected Logger logger = Logger.getLogger("com.iinteractive.Bullpen");
    protected ZMQ.Context context;

    public Core () {}

    // context handling

    protected void initializeContext () {
        context = ZMQ.context(1);
    }

    protected ZMQ.Socket initializeSocket ( int socket_type, String address ) {
        ZMQ.Socket socket = context.socket( socket_type );
        switch ( socket_type ) {
            case ZMQ.REQ:
                socket.connect(address);
                break;
            case ZMQ.REP:
                socket.connect(address);
                break;

            case ZMQ.XREQ:
                socket.bind(address);
                break;
            case ZMQ.XREP:
                socket.bind(address);
                break;

            case ZMQ.PUB:
                socket.bind(address);
                break;
            case ZMQ.SUB:
                socket.connect(address);
                break;
        }
        return socket;
    }

    // message handling

    protected String receiveMessage ( ZMQ.Socket s ) {
        byte[] msg = s.recv(0);
        return new String( msg, 0, msg.length );
    }

    protected void sendMessage ( ZMQ.Socket s, String str ) {
        byte[] msg = str.getBytes();
        s.send( msg, 0 );
    }

    protected void passThrough ( ZMQ.Socket in, ZMQ.Socket out ) {
        while ( true ) {
            byte[] message = in.recv(0);
            boolean more = in.hasReceiveMore();
            out.send( message, more ? ZMQ.SNDMORE : 0 );
            if ( !more ) {
                break;
            }
        }
    }

}