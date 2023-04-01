package controller;

import common.ConcentrationProtocol;
import model.ConcentrationModel;
import view.ConcentrationGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class is the Controller layer
 * It is responsible for interacting with server and updating the model
 * @author Mayurreddy Sangepu
 */
public class ConcentrationController implements ConcentrationProtocol,Runnable {
    /**
     * Instance of model layer
     */
    private ConcentrationModel model;
    /**
     * Instance of Socket
     */
    private Socket clientSocket = null;

    /**
     * This constructor is responsible for initializing socket and model
     *
     * @param hostName server host name
     * @param portNumber server port number
     * @param model instance of model layer
     * @throws IOException
     */
    public ConcentrationController(String hostName, int portNumber, ConcentrationModel model) throws IOException {
        this.clientSocket = new Socket(hostName, portNumber);
        this.model = model;
        initiate();
    }

    /**
     * Initiates the connection with server and creates the board in model
     *
     * @throws IOException
     */
    private void initiate() throws IOException {

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String fromServer = in.readLine();
            if(fromServer!=null) {
                String[] list = fromServer.split(" ");
                if (list[0].equals(BOARD_DIM)) {
                    this.model.createBoard(Integer.parseInt(list[1]));
                }
            }
            startListener();
    }

    /**
     * creates and starts the listener thread responsible for
     * gathering responses from server
     */
    private void startListener()
    {
        new Thread(() -> run()).start();
    }

    /**
     * It reads the output from server and updates the model
     * according to the protocol(ConcentrationProtocol)
     */
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(this.clientSocket.getInputStream()));
        ) {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                String[] list = fromServer.split(" ");
                if (list.length > 0) {
                    if (list[0].equals(ERROR)) {
                        this.model.setStatus(ConcentrationModel.Status.ERROR);
                    } else if (list[0].equals(CARD)) {
                        this.model.revealCard(Integer.parseInt(list[1]), Integer.parseInt(list[2]), list[3]);

                    }else if (list[0].equals(MISMATCH)) {
                        this.model.revertCard(Integer.parseInt(list[1]), Integer.parseInt(list[2]),
                                Integer.parseInt(list[3]), Integer.parseInt(list[4]));
                    }else if (list[0].equals(MATCH)) {
                        this.model.updateMatchesMade();
                    }
                    else if (GAME_OVER.equals(list[0])) {
                        this.model.setStatus(ConcentrationModel.Status.GAME_OVER);
                        this.clientSocket.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method sends the REVEAL request to the server
     *
     * @param row row of the button
     * @param col column of the button
     * @param btt button node
     */
    public void sendRequest(int row, int col, ConcentrationGUI.cellButton btt)
    {
        try  {
            if(!this.model.isRevealed(row, col) && this.model.setCard(btt)) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(String.format(REVEAL_MSG, row, col));
            }

        } catch (IOException e) {System.exit(-1);}
    }


}
