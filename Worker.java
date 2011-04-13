
import org.zeromq.ZMQ;
import java.util.UUID;

public class Worker {

    static String coord_addr = "tcp://127.0.0.1:5555";
    static String publisher_addr;

    public static void log ( String msg ) {
        System.err.println( msg );
    }

    public static String receiveMessage ( ZMQ.Socket s ) {
        byte[] msg = s.recv(0);
        return new String( msg, 0, msg.length );
    }

    public static void sendMessage ( ZMQ.Socket s, String str ) {
        byte[] msg = str.getBytes();
        s.send( msg, 0 );
    }

    private static String generateSubscriberKey () {
        return UUID.randomUUID().toString();
    }

    public static void main (String[] args) throws Exception {

        if ( args.length == 0 ) {
            throw new Exception ("You must specify the publisher port");
        }

        log("Welcome to the worker ...");

        publisher_addr = "tcp://127.0.0.1:" + args[0];

        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket coordinator = context.socket(ZMQ.REP);
        coordinator.connect(coord_addr);

        log("coordinator connected to " + coord_addr);

        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind(publisher_addr);

        log("publisher bound to " + publisher_addr);

        while (true) {

            String msg = receiveMessage(coordinator);
            log("<= got value=(" + msg + ") on coodinator ...");

            String subscriber_key = generateSubscriberKey();
            log("=> sending subscriber-key=(" + subscriber_key + ")");

            sendMessage( coordinator, subscriber_key );

            for ( int i = 0; i < 10; i++ ) {
                String pub_msg = subscriber_key + " [ " + i + " ]";
                log("=> publishing message " + pub_msg);

                sendMessage( publisher, pub_msg );
            }

            log("publishing completed, sending empty value to subscriber ...");
            sendMessage( publisher, (subscriber_key + " ") );
        }

    }
}