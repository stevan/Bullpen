
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import java.util.ArrayList;

public class Server {

    static String backend_addr   = "tcp://127.0.0.1:5555";
    static String frontend_addr  = "tcp://127.0.0.1:6666";
    static String publisher_addr = "tcp://127.0.0.1:7777";

    public static void log ( String msg ) {
        System.err.println( msg );
    }

    public static void main (String[] args) throws Exception {

        if ( args.length == 0 ) {
            throw new Exception ("You must specify at least one subscriber port");
        }

        log("Welcome to the server ...");

        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket frontend = context.socket(ZMQ.XREP);
        frontend.bind(frontend_addr);

        log("frontend bound to " + frontend_addr);

        ZMQ.Socket backend = context.socket(ZMQ.XREQ);
        backend.bind(backend_addr);

        log("backend bound to " + backend_addr);

        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind(publisher_addr);

        log("publisher bound to " + publisher_addr);

        ArrayList<ZMQ.Socket> subscribers = new ArrayList<ZMQ.Socket>();

        for ( int i = 0; i < args.length; i++ ) {
            String subscriber_addr = "tcp://127.0.0.1:" + args[i];

            ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
            subscriber.connect( subscriber_addr );

            log("subscriber connected to " + subscriber_addr);

            // subscribe to everything ...
            subscriber.subscribe("".getBytes());

            subscribers.add( subscriber );
        }

        Poller items = context.poller(2);
        items.register(frontend, Poller.POLLIN);
        items.register(backend, Poller.POLLIN);
        for ( int i = 0; i < subscribers.size(); i++ ) {
            items.register( subscribers.get(i), Poller.POLLIN );
        }

        while (true) {
            items.poll();

            if (items.pollin(0)) {
                while (true) {
                    byte[] message = frontend.recv(0);
                    boolean more = frontend.hasReceiveMore();
                    backend.send(message, more ? ZMQ.SNDMORE : 0);
                    if (!more) {
                        break;
                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    byte[] message = backend.recv(0);
                    boolean more = backend.hasReceiveMore();
                    frontend.send(message, more ? ZMQ.SNDMORE : 0);
                    if (!more) {
                        break;
                    }
                }
            }

            for ( int i = 0; i < subscribers.size(); i++ ) {
                if (items.pollin( 2 + i )) {
                    log("got an event from subscriber " + (2 + i) + " ...");
                    ZMQ.Socket subscriber = subscribers.get(i);
                    while (true) {
                        byte[] message = subscriber.recv(0);
                        boolean more = subscriber.hasReceiveMore();
                        log("publishing message => " + new String( message, 0, message.length ));
                        publisher.send(message, more ? ZMQ.SNDMORE : 0);
                        if (!more) {
                            break;
                        }
                    }
                }
            }

        }

    }
}





