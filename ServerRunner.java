
import com.prg.*;

public class ServerRunner {

    public static void main (String[] args) throws Exception {

        if ( args.length == 0 ) {
            throw new Exception ("You must specify at least one subscriber port");
        }

        Server server = new Server (
            "tcp://127.0.0.1",
            "5555",
            "6666",
            "7777",
            args
        );
        server.run();
    }
}





