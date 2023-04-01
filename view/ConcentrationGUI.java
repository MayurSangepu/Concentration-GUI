package view;

import controller.ConcentrationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ConcentrationModel;
import model.Observer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * This file is responsible for the view and controller layer
 *
 * @author Mayurreddy Sangepu
 */

public class ConcentrationGUI extends Application implements Observer<ConcentrationModel> {
    /**
     * the font size of the label
     */
    private final static int LABEL_FONT_SIZE = 20;
    /**
     *    declaring default image
     */
    private static Image default_image;
     /**
     *     declaring first label1
     */
    private Label label1;
     /**
     *     declaring second label2
      */
    private Label label2;
     /**
     *    declaring third label3
      */
    private Label label3;
     /**
     *     declaring gridPane layout
      */
    private GridPane gridPane;
     /**
     *     declaring model instance
      */
    private ConcentrationModel model;

    /**
     * declaring controller instance
     */
    private ConcentrationController controller;

    /**
     * initialize model and the default image
     * to be placed on the board
     */
    public  ConcentrationGUI(){
        default_image = new Image(getClass().getResourceAsStream(
                "pokeball.png"));
        this.model = new ConcentrationModel();
    }

    /**
     * this is the main method responsible for
     * launching the application
     *
     * @param args: arguments taken from the user
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * It initializes the connection i.e.,
     * this method adds the current object
     * to the observer and creates controller instance
     */
    public void init(){
        this.model.addObserver(this);
        List<String> args = getParameters().getRaw();
        if(args.size()!=2)
        {
            System.err.println(
                    "Usage: java ConcentrationClient <host name> <port number>");
            System.exit(1);
        }
        String hostName = args.get(0);
        try {
            int portNumber = Integer.parseInt(args.get(1));
            this.controller = new ConcentrationController(hostName,portNumber,this.model);
        }catch (UnknownHostException ue) {
            System.err.println("Invalid host " + hostName);
            System.exit(1);
        }catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }catch (NumberFormatException ne) {
            System.err.println("Invalid Arguments");
            System.exit(1);
        }
    }

    /**
     * this method creates a grid pane layout on
     * the scene and sets the event action on button
     * click
     *
     * @return gridPane : grid pane layout is returned.
     */
    private GridPane makeGridPane(){
        this.gridPane = new GridPane();

        for (int row = 0; row< this.model.getDim(); ++row) {
            for (int col = 0; col< this.model.getDim(); ++col) {
                cellButton button = new cellButton(default_image);
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(event -> this.controller.sendRequest(finalRow,finalCol,button));
                this.gridPane.add(button, col, row);
            }
        }
        return gridPane;
    }

    /**
     * class that represents button on the grid
     */
    public class cellButton extends Button {
        /**
         * Create the button with the image based on the players.
         *
         * @param card Image of pokeball
         */
        public cellButton(Image card) {
            this.setGraphic(new ImageView(card));
        }
    }

    /**
     * Returns Image based on the card value.
     * @param card Card value
     * @return returns image to be updated
     */
    public Image getImage(ConcentrationModel.Card card)
    {
        Image image = switch (card) {
            case A -> new Image(getClass().getResourceAsStream(
                    "abra.png"));
            case B -> new Image(getClass().getResourceAsStream(
                    "bulbasaur.png"));
            case C -> new Image(getClass().getResourceAsStream(
                    "charizard.png"));
            case D -> new Image(getClass().getResourceAsStream(
                    "diglett.png"));
            case E -> new Image(getClass().getResourceAsStream(
                    "golbat.png"));
            case F -> new Image(getClass().getResourceAsStream(
                    "golem.png"));
            case G -> new Image(getClass().getResourceAsStream(
                    "jigglypuff.png"));
            case H -> new Image(getClass().getResourceAsStream(
                    "magikarp.png"));
            case I -> new Image(getClass().getResourceAsStream(
                    "meowth.png"));
            case J -> new Image(getClass().getResourceAsStream(
                    "mewtwo.png"));
            case K -> new Image(getClass().getResourceAsStream(
                    "natu.png"));
            case L -> new Image(getClass().getResourceAsStream(
                    "pidgey.png"));
            case M -> new Image(getClass().getResourceAsStream(
                    "pikachu.png"));
            case N -> new Image(getClass().getResourceAsStream(
                    "squirtle.png"));
            case O -> new Image(getClass().getResourceAsStream(
                    "poliwag.png"));
            case P -> new Image(getClass().getResourceAsStream(
                    "psyduck.png"));
            case Q -> new Image(getClass().getResourceAsStream(
                    "rattata.png"));
            case R -> new Image(getClass().getResourceAsStream(
                    "slowpoke.png"));
            case S -> new Image(getClass().getResourceAsStream(
                    "snorlak.png"));
            default -> default_image;
        };

        return image;
    }

    /**
     * this method is responsible for setting layout,labels basically
     * all gui related initialization and stage show.
     * @param stage: the stage object where all the components will be showed
     */
    @Override
    public void start(Stage stage)  {

        BorderPane borderPane = new BorderPane();
        Label label = new Label("Concentration GUI");
        label.setStyle("-fx-font: " + LABEL_FONT_SIZE + " arial;");
        borderPane.setTop(label);
        BorderPane.setAlignment(label, Pos.CENTER);
        this.gridPane = makeGridPane();
        borderPane.setCenter(this.gridPane);
        BorderPane innerPane = new BorderPane();
        this.label1 = new Label("Moves: "+this.model.getMovesMade());
        this.label2 = new Label("Matches: "+this.model.getMatchesMade());
        this.label3 = new Label(""+this.model.getGameStatus());
        innerPane.setLeft(this.label1);
        innerPane.setCenter(this.label2);
        innerPane.setRight(this.label3);
        borderPane.setBottom(innerPane);
        Scene scene = new Scene(borderPane);
        stage.setTitle("ConcentrationGUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     * Refreshes the buttons on the grid
     *
     * @param card the button and image to be updated
     */
    private void refresh(ConcentrationModel.CardUpdate card) {
        if(card!=null && card.getButton()!=null) {
            cellButton btt = card.getButton();
            btt.setGraphic(new ImageView(getImage(card.getCard())));
        }
        this.label1.setText("Moves: "+this.model.getMovesMade());
        this.label2.setText("Matches: "+this.model.getMatchesMade());
        this.label3.setText(""+this.model.getGameStatus());
    }

    /**
     * The update for the GUI , updates the user view.
     *
     * @param model: the board on which updates take place
     * @param card: the button and image to be updated
     */
    @Override
    public void update(ConcentrationModel model, ConcentrationModel.CardUpdate card) {

        if(Platform.isFxApplicationThread())
        {
            this.refresh(card);
        }
        else{
            Platform.runLater(() -> this.refresh(card));
        }

    }
}
