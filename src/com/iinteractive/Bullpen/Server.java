
package com.iinteractive.Bullpen;

import org.zeromq.ZMQ;
import java.util.logging.*;
import java.util.ArrayList;

public class Server extends Core {

    private ZMQ.Socket frontend;
    private ZMQ.Socket backend;
    private ZMQ.Socket publisher;
    private ZMQ.Socket subscriber;
    private ZMQ.Poller poller;

    public Server ( String backend_addr,
                    String frontend_addr,
                    String publisher_addr,
                    ArrayList<String> subscriber_addrs ) {

        initializeContext();

        logger.log(Level.INFO, "Welcome to server ...");

        initializeFrontend( frontend_addr );
        logger.log(Level.INFO, "Frontend connected to " + frontend_addr);

        initializeBackend( backend_addr );
        logger.log(Level.INFO, "Backend connected to " + backend_addr);

        initializePublisher( publisher_addr );
        logger.log(Level.INFO, "Publisher bound to " + publisher_addr);

        initializeSubscribers( subscriber_addrs );
        logger.log(Level.INFO, subscriber_addrs.size() + " subscribers bound to: " + subscriber_addrs.toString() );

        initializePoller();
        logger.log(Level.INFO, "Poller initialized ...");
    }

    private void initializeFrontend ( String address ) {
        frontend = initializeSocket( ZMQ.XREP, address );
    }

    private void initializeBackend ( String address ) {
        backend = initializeSocket( ZMQ.XREQ, address );
    }

    private void initializePublisher ( String address ) {
        publisher = initializeSocket( ZMQ.PUB, address );
    }

    private void initializeSubscribers ( ArrayList<String> addresses ) {
        subscriber = context.socket( ZMQ.SUB );
        for ( String address : addresses ) {
            subscriber.connect( address );
        }
        // subscribe to everything ...
        subscriber.subscribe("".getBytes());
    }

    private void initializePoller () {
        poller = context.poller(2);
        poller.register( frontend,   ZMQ.Poller.POLLIN );
        poller.register( backend,    ZMQ.Poller.POLLIN );
        poller.register( subscriber, ZMQ.Poller.POLLIN );
    }

    public void run () {
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll();

            if (poller.pollin( 0 )) {
                logger.log(Level.INFO, "got event on frontend channel");
                passThrough( frontend, backend );
            }

            if (poller.pollin( 1 )) {
                logger.log(Level.INFO, "got event on backend channel");
                passThrough( backend, frontend );
            }

            if (poller.pollin( 2 )) {
                logger.log(Level.INFO, "got event on a subscriber channel");
                // NOTE:
                // don't use the pass-through here
                // it kinda seems to stall on the
                // SENDMORE stuff, and we know that
                // these are just simple one level
                // messages anyway.
                // - SL
                byte[] message = subscriber.recv( 0 );
                publisher.send( message, 0 );
            }
        }
    }
}









