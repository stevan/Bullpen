
package com.prg.ZeroMQ;

import java.util.ArrayList;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Runner {

    public static void main (String[] args) throws Exception {

        if ( args.length != 2 ) {
            System.out.println("Usage: Runner <Server | Worker> <config-file>");
            System.exit(1);
        }

        String which_type  = args[0];
        String config_file = args[1];

        try {
            JSONObject config = readConfigFile( config_file );

            if ( which_type.equals("Server") ) {
                runServer( config );
            }
            else if ( which_type.equals("Worker") ) {
                runWorker( config );
            }
            else {
                System.out.println("You must specify either 'Server' or 'Worker' for the type of device to run.");
                System.out.println("Unknown device : '" + which_type + "'");
                throw new Exception();
            }

        } catch ( Exception e ) {
            System.out.println(e);
            System.exit(1);
            return;
        }

        System.exit(0);
    }

    private static void runServer ( JSONObject config ) {

        JSONArray addrs = (JSONArray) config.get("subscriber_addrs");
        ArrayList<String> subscriber_addrs = new ArrayList<String> ();
        for ( Object addr : addrs ) {
            subscriber_addrs.add( (String) addr );
        }

        Server server = new Server (
            (String) config.get("backend_addr"),
            (String) config.get("frontend_addr"),
            (String) config.get("publisher_addr"),
            subscriber_addrs
        );
        server.run();
    }

    // C'mon Java, you can't just
    // provide a basic one for me
    // ... *sigh*
    private static class ProducerClassLoader extends ClassLoader {
        public Class loadProducerClass ( String class_name ) throws ClassNotFoundException {
            return loadClass( class_name, true );
        }
    }

    private static void runWorker ( JSONObject config ) {

        String producer_class_name = (String) config.get("producer_class");

        Producer producer;
        try {
            ProducerClassLoader loader = new ProducerClassLoader ();
            Class producer_class = loader.loadProducerClass( producer_class_name );

            producer = (Producer) producer_class.newInstance();
            if ( config.containsKey("producer_config") ) {
                producer.configure( (HashMap) config.get("producer_config") );
            }

        } catch ( ClassNotFoundException e ) {
            System.out.println("Could not find the producer class (" + producer_class_name + ")");
            System.out.println(e);
            System.exit(1);
            return;
        } catch ( Exception e ) {
            System.out.println("Could not create an instance of the producer class (" + producer_class_name + ")");
            System.out.println(e);
            System.exit(1);
            return;
        }

        Worker worker = new Worker (
            (String) config.get("coordinator_addr"),
            (String) config.get("publisher_addr"),
            producer
        );
        worker.run();
    }

    private static JSONObject readConfigFile ( String config_file ) throws Exception {
        JSONObject config;
        try {
            JSONParser parser = new JSONParser();
            config = (JSONObject) parser.parse( new FileReader( config_file ) );
        }
        catch ( FileNotFoundException e ) {
            System.out.println("Could not find config_file=(" + config_file + ")");
            System.out.println(e);
            throw new Exception();
        }
        catch ( IOException e ) {
            System.out.println("Could not load config_file=(" + config_file + ")");
            System.out.println(e);
            throw new Exception();
        }
        catch ( Exception e ) {
            System.out.println("Failed to parse config file=(" + config_file + ")");
            System.out.println(e);
            throw new Exception();
        }
        return config;
    }

}