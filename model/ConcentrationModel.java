package model;

import view.ConcentrationGUI;

import java.util.*;

/**
 * This class represents the model layer and
 * is responsible for updating the view
 *
 * @author Mayurreddy Sangepu
 */

public class ConcentrationModel {
    /**
     * Instance of board with 2d array
     */
    private Card clientBoard[][];
    /**
     * dimension of the board
     */
    private int dim;
    /**
     * it holds count of the matches made
     */
    private int matchesMade;
    /**
     * It holds object of first button clicked
     */
    private ConcentrationGUI.cellButton card1;
    /**
     * It holds object of second button clicked
     */
    private ConcentrationGUI.cellButton card2;

    /**
     * Enum that holds the card values
     */
    public enum Card
    {
        A("abra"),
        B("bulbasaur"),
        C("charizard"),
        D("diglett"),
        E("golbat"),
        F("golem"),
        G("jigglypuff"),
        H("magikarp"),
        I("meowth"),
        J("mewtwo"),
        K("natu"),
        L("pidgey"),
        M("pikachu"),
        N("squirtle"),
        O("poliwag"),
        P("psyduck"),
        Q("rattata"),
        R("slowpoke"),
        S("snorlak"),
        def("pokeball");

        /**
         * value of enum
         */
        public String value;

        /**
         * initializes value of enum
         * @param val it is the value of image
         */
        Card(String val)
        {
            value = val;
        }

    }

    /** the game status */
    public enum Status {
        OK,
        GAME_OVER,
        ERROR
    }

    /** how many moves have been made? */
    private int movesMade;
    /** the status of the game */
    private Status status;
    /** the observers of this model */
    private List<Observer<ConcentrationModel>> observers;

    /**
     * Constructor in initializes the model variables
     */
    public ConcentrationModel()
    {
        this.observers = new LinkedList<>();
        this.matchesMade = 0;
        this.status = Status.OK;
    }

    /**
     * creates the board with given dimension
     * @param dim dimension of the board
     */
    public void createBoard(int dim)
    {
        this.dim = dim;
        this.clientBoard = new Card[dim][dim];
        for (int row=0; row<this.dim; ++row) {
            for (int col=0; col<this.dim; ++col) {
                this.clientBoard[row][col] = Card.def;
            }
        }
    }

    /**
     * Get the number of moves that have been made.
     *
     * @return moves made
     */
    public int getMovesMade() {
        return this.movesMade;
    }

    /**
     * Get the status of the game.
     *
     * @return game status
     */
    public Status getGameStatus() {
        return this.status;
    }

    /**
     * Reveals a card on the board
     * @param row row of the card's position
     * @param col column of the card's position
     * @param card value of the card
     */
    public void revealCard(int row,int col,String card)
    {
        this.clientBoard[row][col] = Card.valueOf(card);
        this.status = Status.OK;
        this.movesMade++;
        if(this.card2!=null)
        {
            notifyObservers(new CardUpdate(this.card2,Card.valueOf(card)));
        }
        else
        {
            notifyObservers(new CardUpdate(this.card1,Card.valueOf(card)));
        }
    }

    /**
     * Reverts the cards in the event of mismatch
     *
     * @param row1 row of the first card's position
     * @param col1 column of the first card's position
     * @param row2 row of the second card's position
     * @param col2 column of the second card's position
     */
    public void revertCard(int row1, int col1, int row2, int col2)
    {
        this.clientBoard[row1][col1] = this.clientBoard[row2][col2] = Card.def;
        this.status = Status.OK;
        notifyObservers(new CardUpdate(this.card1,Card.def));
        notifyObservers(new CardUpdate(this.card2,Card.def));
        this.card1 = null;
        this.card2 = null;
    }

    /**
     * It sets the button object
     * @param card it is the button clicked
     * @return it returns if successful or not
     */
    public boolean setCard(ConcentrationGUI.cellButton card)
    {
        if(this.card1!=null && this.card2!=null){return false;}
        else{
            if(this.card1==null)
            {
                this.card1 = card;
            }
            else
            {
                this.card2 = card;
            }
            return true;
        }

    }

    /**
     * In the event of a match it updates matches made count
     *
     */
    public void updateMatchesMade()
    {
        this.matchesMade++;
        this.card1 = null;
        this.card2 = null;
        notifyObservers(null);
    }

    /**
     * it checks if a card is revealed or not
     * @param row row of the card's position
     * @param col column of the card's position
     * @return returns if revealed or not
     */
    public boolean isRevealed(int row, int col)
    {
        return this.clientBoard[row][col]!=Card.def;
    }

    /**
     * This method returns dimension of the board
     * @return dimension of the board
     */
    public int getDim()
    {
        return this.dim;
    }

    /**
     * This method sets the status of the game as the game progresses
     * @param status status of the game
     */
    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * This method returns the matches made count
     * @return count of matches made
     */
    public int getMatchesMade()
    {
        return this.matchesMade;
    }

    /**
     * This class represents the card to be updated on the view
     */
    public class CardUpdate
    {
        /**
         * instance of button
         */
        private ConcentrationGUI.cellButton button;
        /**
         * instance of card value
         */
        private Card card;

        /**
         * It initializes the card object
         * @param button button of the card
         * @param card value of the card
         */
        public CardUpdate(ConcentrationGUI.cellButton button, Card card)
        {
            this.button = button;
            this.card = card;
        }

        /**
         * This method returns button of the card
         * @return button of the card
         */
        public ConcentrationGUI.cellButton getButton()
        {
            return this.button;
        }

        /**
         * This method returns value of the card
         * @return value of the card
         */
        public Card getCard()
        {
            return this.card;
        }
    }


    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer<ConcentrationModel> observer) {
        this.observers.add(observer);
    }

    /** When the model changes, the observers are notified via their update() method */
    private void notifyObservers(CardUpdate card) {
        for (Observer<ConcentrationModel> obs: this.observers ) {
            obs.update(this, card);
        }
    }

}
