
import com.prg.*;

public class WorkerRunner {

    public static class SimpleMessageProducer implements MessageProducer {

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

    public static void main (String[] args) throws Exception {

        if ( args.length == 0 ) {
            throw new Exception ("You must specify the publisher port");
        }

        Worker worker = new Worker ( "tcp://127.0.0.1", "5555", args[0], new SimpleMessageProducer () );
        worker.run();
    }
}