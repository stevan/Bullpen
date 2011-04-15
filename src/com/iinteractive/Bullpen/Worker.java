
package com.iinteractive.Bullpen;

import org.zeromq.ZMQ;
import java.util.UUID;

public class Worker extends Core {

    private ZMQ.Socket coordinator;
    private ZMQ.Socket publisher;

    private Producer producer;

    public Worker ( String coordinator_addr,
                    String publisher_addr,
                    Producer _producer ) {

        initializeContext();

        logger.log("welcome to worker ...");

        initializeCoordinator( coordinator_addr );
        logger.log("coordinator connected to " + coordinator_addr);

        initializePublisher( publisher_addr );
        logger.log("publisher bound to " + publisher_addr);

        producer = _producer;
    }

    private String generateSubscriberKey () {
        return UUID.randomUUID().toString();
    }

    private void initializeCoordinator ( String address ) {
        coordinator = initializeSocket( ZMQ.REP, address );
    }

    private void initializePublisher ( String address ) {
        publisher = initializeSocket( ZMQ.PUB, address );
    }

    private void publishMessage ( String subscriber_key, String message ) {
        String pub_msg = subscriber_key + " " + message;
        logger.log("=> publishing message " + pub_msg);
        sendMessage( publisher, pub_msg );
    }

    public void run () {

        while (!Thread.currentThread().isInterrupted()) {

            String msg = receiveMessage( coordinator );
            logger.log("<= got value=(" + msg + ") on coodinator ...");

            producer.initialize( msg );

            String subscriber_key = generateSubscriberKey();
            logger.log("=> sending subscriber-key=(" + subscriber_key + ")");

            sendMessage( coordinator, subscriber_key );

            while ( producer.hasNext() ) {
                publishMessage( subscriber_key, producer.next() );
            }

            producer.reset();

            logger.log("publishing completed, sending empty value to subscriber ...");
            sendMessage( publisher, (subscriber_key + " ") );

        }

        coordinator.close();
        publisher.close();
        context.term();
    }
}