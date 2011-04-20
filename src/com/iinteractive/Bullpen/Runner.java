
package com.iinteractive.Bullpen;

import java.util.*;
import java.util.logging.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Runner {

    private static Logger logger = Logger.getLogger( Runner.class.getName() );

    public static void main (String[] args) throws Exception {

        if ( args.length != 2 ) {
            System.out.println("Usage: Runner <Server | Worker> <config-file>");
            System.exit(1);
        }

        String which_type  = args[0];
        String config_file = args[1];

        if (which_type != "Server" || which_type != "Worker") {
            System.out.println("You must specify either 'Server' or 'Worker' for the type of device to run.");
            System.out.println("Unknown device : '" + which_type + "'");
            System.exit(1);
        }

        try {
            JSONObject config = readConfigFile( config_file );

            if ( which_type.equals("Server") ) {
                runServer( config );
            }
            else if ( which_type.equals("Worker") ) {
                runWorker( config );
            }

        } catch ( Exception e ) {
            System.out.println(e);
            System.exit(1);
            return;
        }

        System.exit(0);
    }

    private static void runServer ( HashMap config ) {

        logger.log(Level.CONFIG, "Configuring Server ...");

        HashMap frontend_config = (HashMap) config.get("frontend");
        HashMap backend_config = (HashMap) config.get("backend");

        ArrayList addrs = (ArrayList) backend_config.get("subscriber_addrs");
        ArrayList<String> subscriber_addrs = new ArrayList<String> ();
        for ( Object addr : addrs ) {
            subscriber_addrs.add( (String) addr );
        }

        Server server = new Server (
            (String) backend_config.get("coordinator_addr"),
            (String) frontend_config.get("coordinator_addr"),
            (String) frontend_config.get("publisher_addr"),
            subscriber_addrs
        );

        logger.log(Level.CONFIG, "Server configured, ... running");
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

    private static void runWorker ( HashMap config ) {

        logger.log(Level.CONFIG, "Configuring Worker ...");

        String producer_class_name = (String) config.get("producer_class");

        logger.log(Level.CONFIG, "Worker is using '" + producer_class_name + "' Producer");

        Producer producer;
        try {
            logger.log(Level.CONFIG, "Loading producer class '" + producer_class_name + "'");
            ProducerClassLoader loader = new ProducerClassLoader ();
            Class producer_class = loader.loadProducerClass( producer_class_name );
            logger.log(Level.CONFIG, "Producer class '" + producer_class_name + "' loaded");

            producer = (Producer) producer_class.newInstance();
            if ( config.containsKey("producer_config") ) {
                logger.log(Level.CONFIG, "Configuring producer class '" + producer_class_name + "'");
                producer.configure( (HashMap) config.get("producer_config") );
            }

        } catch ( ClassNotFoundException e ) {
            logger.log(Level.SEVERE, "Could not find the producer class (" + producer_class_name + ")");
            logger.log(Level.SEVERE, e.toString());
            System.exit(1);
            return;
        } catch ( Exception e ) {
            logger.log(Level.SEVERE, "Could not create an instance of the producer class (" + producer_class_name + ")");
            logger.log(Level.SEVERE, e.toString());
            System.exit(1);
            return;
        }

        Worker worker = new Worker (
            (String) config.get("coordinator_addr"),
            (String) config.get("publisher_addr"),
            producer
        );

        logger.log(Level.CONFIG, "Worker configured, ... running");
        worker.run();
    }

    private static JSONObject readConfigFile ( String config_file ) throws Exception {
        JSONObject config;
        try {
            JSONParser parser = new JSONParser();
            config = (JSONObject) parser.parse( new FileReader( config_file ) );
        }
        catch ( FileNotFoundException e ) {
            logger.log(Level.SEVERE, "Could not find config_file=(" + config_file + ")");
            logger.log(Level.SEVERE, e.toString());
            throw new Exception();
        }
        catch ( IOException e ) {
            logger.log(Level.SEVERE, "Could not load config_file=(" + config_file + ")");
            logger.log(Level.SEVERE, e.toString());
            throw new Exception();
        }
        catch ( Exception e ) {
            logger.log(Level.SEVERE, "Failed to parse config file=(" + config_file + ")");
            logger.log(Level.SEVERE, e.toString());
            throw new Exception();
        }
        return config;
    }

}