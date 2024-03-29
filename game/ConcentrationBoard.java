package game;

import common.ConcentrationException;
import common.ConcentrationProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the game board for the concentration game.
 *
 * @author Mayurreddy Sangepu
 */
public class ConcentrationBoard implements ConcentrationProtocol{
    /** the smallest board is 2x2 */
    private final static int MIN_DIM = 2;
    /** the largest board is 6x6 */
    private final static int MAX_DIM = 6;

    /** the square dimension of the board */
    private int DIM;
    /** the actual board is a 2-D grid of cards */
    private ConcentrationCard board[][];
    /** if the first card is revealed this is set (otherwise null) */
    private ConcentrationCard revealedCard;
    /** the number of card matches that have been made so far */
    private int matches;
    /** instance to determine a card match or mismatch*/
    private CardMatch cardMatch;
    /** contains Match or Mismatch string*/
    private String matchStatus;

    /**
     * An internal class used to determine a card match or mismatch.
     */
    public class CardMatch {
        /** the first card */
        private ConcentrationCard card1;
        /** the second card */
        private ConcentrationCard card2;
        /** do the cards match? */
        private boolean match;

        /**
         * Create a new instance from the two revealed cards and whether they matches.
         *
         * @param card1 first card
         * @param card2 second card
         * @param match do the cards match or not
         */
        public CardMatch(ConcentrationCard card1, ConcentrationCard card2, boolean match) {
            this.card1 = card1;
            this.card2 = card2;
            this.match = match;
        }

        /**
         * Get the first card.
         *
         * @return first card
         */
        public ConcentrationCard getCard1() {
            return this.card1;
        }

        /**
         * Get the second card.
         *
         * @return second card
         */
        public ConcentrationCard getCard2() {
            return this.card2;
        }

        /**
         * Is there a card match?
         *
         * @return whether there was a match or not
         */
        public boolean isMatch() {
            return this.match;
        }

        /**
         * Is it ready to check for a match - both cards should be non-null
         *
         * @return is a match ready to check?
         */
        public boolean isReady() {
            return this.card1 != null && this.card2 != null;
        }
    }

    /**
     * Create the board in non-cheat mode.
     *
     * @param DIM square dimension
     * @throws ConcentrationException if the dimension is illegal
     */
    public ConcentrationBoard(int DIM) throws ConcentrationException {
        this(DIM, false);
    }

    /**
     * Create the board.
     *
     * @param DIM square dimension
     * @param cheat whether to display the fully revealed board or not
     * @throws ConcentrationException if the dimensions are invalid
     */
    public ConcentrationBoard(int DIM, boolean cheat) throws ConcentrationException {
        // check for bad dimensions
        if (DIM < MIN_DIM || DIM > MAX_DIM) {
            throw new ConcentrationException("Board size out of range: " + DIM);
        } else if (DIM % 2 != 0) {
            throw new ConcentrationException("Board size not even: " + DIM);
        }

        /** create the pair of cards and shuffle them */
        List<Character> chars = new ArrayList<>(DIM*DIM);
        for (char i=0; i<(DIM*DIM)/2; ++i) {
            chars.add((char)(i+'A'));
            chars.add((char)(i+'A'));
        }
        Collections.shuffle(chars);

        /**
         * Create the grid of cards and populate from the shuffled list.
         */
        this.DIM = DIM;
        this.board = new ConcentrationCard[DIM][DIM];
        for (int row=0; row<DIM; ++row) {
            for (int col=0; col<DIM; ++col) {
                this.board[row][col] = new ConcentrationCard(row, col, chars.remove(0));
            }
        }

        // if cheat mode is enabled display the fully revealed board
        if (cheat) {
            System.out.println("SOLUTION:");
            System.out.println(this.toString());
        }

        // hide all the cards in the board
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                this.board[row][col].hide();
            }
        }

        // initialize rest of state
        this.revealedCard = null;
        this.matches = 0;
    }

    /**
     * Get the square dimension of the board
     * @return square dimension
     */
    public int getDIM() {
        return this.DIM;
    }

    /**
     * Get a card from the board at a coordinate.
     *
     * @param row the row
     * @param col the column
     * @return the card
     * @throws ConcentrationException if the coordinate is invalid
     */
    public ConcentrationCard getCard(int row, int col) throws ConcentrationException {
        if(row>=this.DIM || col>=this.DIM)
        {
            throw new ConcentrationException("Coordinates out of range " + row + " " + col);
        }
        else
        {
            ConcentrationCard card = this.board[row][col];
            if(!card.isHidden()) {
                throw new ConcentrationException("Card already revealed at " + row + " " + col);
            }
            else
            {
                return this.board[row][col];
            }
        }
    }

    /**
     * Reveal a hidden card.
     *
     * @param row the row
     * @param col the column
     * @return resulting information about a potential match or mismatch
     * @throws ConcentrationException if the game is over, the coordinate is invalid, or the
     *     card has already been revealed.
     */
    public CardMatch reveal(int row, int col) throws ConcentrationException {
        if(gameOver())
        {
            throw new ConcentrationException("Invalid Coordinates "+ row + " " + col);
        }
        ConcentrationCard card = getCard(row, col);
        card.reveal();

        if(this.revealedCard == null)
        {
            this.cardMatch = new CardMatch(this.revealedCard, card, false);
            this.revealedCard = card;
        }
        else
        {
            if(this.revealedCard.getLetter() == (card.getLetter()))
            {
                this.cardMatch = new CardMatch(this.revealedCard, card, true);
                this.matches+=1;
            }
            else
            {
                this.cardMatch = new CardMatch(this.revealedCard, card, false);
            }
            this.revealedCard = null;
        }

        return this.cardMatch;
    }

    /**
     * The game is over when all the matches have been made.
     *
     * @return whether the game is over or not
     */
    public boolean gameOver() {
        return this.matches >= (this.DIM * this.DIM) / 2;
    }

    /**
     * Returns a string representation of the board, for example a
     * 4x4 game that is just underway.
     *
     *   0123
     * 0|G...
     * 1|G...
     * 2|....
     * 3|....
     *
     * @return the board as a string
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // build the top row of indices
        str.append("  ");
        for (int col=0; col<this.DIM; ++col) {
            str.append(col);
        }
        str.append("\n");
        // build each row of the actual board
        for (int row=0; row<this.DIM; ++row) {
            str.append(row).append("|");
            // build the columns of the board
            for (int col=0; col<this.DIM; ++col) {
                ConcentrationCard card = this.board[row][col];
                // based on whether the card is hidden or not display
                // build with the correct letter
                if (card.isHidden()) {
                    str.append(ConcentrationCard.HIDDEN);
                } else {
                    str.append(this.board[row][col].getLetter());
                }
            }
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * This method processes the input coordinates and
     * returns the message according to the protocol
     *
     * @param row row of the card to be revealed
     * @param col column of the card to be revealed
     * @return returns the message based on the protocol
     * @throws ConcentrationException
     */
    public String processInput(int row, int col) throws ConcentrationException {
        String msg = "";
        CardMatch cardMatch = reveal(row,col);
        if(cardMatch.isReady())
        {
            ConcentrationCard card1 = cardMatch.getCard1(),card2 = cardMatch.getCard2();
            if(cardMatch.isMatch())
            {
                this.matchStatus = String.format(MATCH_MSG,card1.getRow(),card1.getCol(),card2.getRow(),card2.getCol());
            }
            else {
                this.matchStatus = String.format(MISMATCH_MSG,card1.getRow(),card1.getCol(),card2.getRow(),card2.getCol());
                card1.hide();
                card2.hide();
            }
        }
        else
        {
            row=this.revealedCard.getRow();
            col=this.revealedCard.getCol();
            this.matchStatus = null;
        }
        String letter = ""+this.board[row][col].getLetter();
        msg = String.format(CARD_MSG,row,col,letter);

        return msg;
    }

    public String getMatchStatus() {
        return this.matchStatus;
    }

}
