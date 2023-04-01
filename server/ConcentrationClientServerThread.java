package server;

import common.ConcentrationException;
import common.ConcentrationProtocol;
import game.ConcentrationBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * this class helps to create multiple server instances
 *
 * @author Ajay Yadav
 * @author Mayur Reddy Sangepu
 */
public class ConcentrationClientServerThread extends Thread implements ConcentrationProtocol{
    /**
     *instance of server socket
     */
    private Socket socket = null;
    /**
     * dimension of the board
     */
    private int dim;
    /**
     * instance of server side game board
     */
    private ConcentrationBoard serverBoard;
    /**
     * sleep time of the thread
     */
    private static final int SLEEP_TIME = 1000;

    /**
     * constructor to initialize class variables
     * @param socket : server socket
     * @param dim : dimension of the game board
     */
    public ConcentrationClientServerThread(Socket socket,int dim) {
        super("server.ConcentrationClientServerThread");
        this.socket = socket;
        this.dim = dim;
    }

    /**
     *  ConcentrationClientServerThread run method to carry out thread responsibilities
     *
     *  It takes client request and sends response to the client
     *  until the client disconnects or the game is completed
     *
     *  It strictly follows the ConcentrationProtocol
     */
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String outputLine;
            this.serverBoard = new ConcentrationBoard(this.dim,true);
            outputLine = String.format(BOARD_DIM_MSG,this.serverBoard.getDIM());
            out.println(outputLine);
            runHelper(out,in);
            socket.close();
        } catch (IOException | ConcentrationException e) {
            System.exit(-1);
        }

    }
    /**
     * this method is the helper method for the run
     * @param out: Prints formatted representations of objects to a text-output stream
     * @param in: Reads text from a character-input stream, buffering characters so as to
     *            provide for the efficient reading of characters, arrays, and lines.
     * @throws IOException:Signals that an I/O exception of some sort has occurred.
     */
    private void runHelper(PrintWriter out,BufferedReader in ) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            String outputLine;
            try {
                String[] list = inputLine.split(" ");
                if(list.length==3 && list[0].equals(REVEAL)) {
                    outputLine = this.serverBoard.processInput(Integer.parseInt(list[1]),Integer.parseInt(list[2]));
                    out.println(outputLine);
                    if(this.serverBoard.getMatchStatus()!=null)
                    {
                        outputLine = this.serverBoard.getMatchStatus();
                        this.sleep(SLEEP_TIME);
                        out.println(outputLine);
                    }
                    if (this.serverBoard.gameOver()) {
                        outputLine = GAME_OVER_MSG;
                        out.println(outputLine);
                        break;
                    }
                }
                else{
                    outputLine = String.format(ERROR_MSG,"Invalid Arguments");
                    out.println(outputLine);
                }
            }catch(NumberFormatException ne)
            {
                outputLine = String.format(ERROR_MSG,"Invalid Coordinates");
                out.println(outputLine);
            }
            catch (ConcentrationException | InterruptedException ce)
            {
                outputLine = String.format(ERROR_MSG,ce.getMessage());
                out.println(outputLine);
            }
        }
    }
}
