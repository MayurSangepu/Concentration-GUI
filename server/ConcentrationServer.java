package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This class is responsible for sending the input to the
 * ConcentrationClientServerThread because of which the multiple
 * clients can use the server.
 *
 * @author Ajay Yadav
 * @author Mayur Reddy Sangepu
 */
public class ConcentrationServer {

    /**
     * In this method the args input,processes it and passes it to the
     * ConcentrationClientServerThread object.
     * @param args: consists of port number and board dimension
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java server.ConcentrationServer <port number> <board dimension>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new ConcentrationClientServerThread(serverSocket.accept(), dimension).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }

    }
}
