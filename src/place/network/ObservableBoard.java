package place.network;
import place.PlaceBoard;
import place.PlaceTile;
import java.io.Serializable;
import java.util.Observable;

/**
 * an observable version of the place board
 * @author Taylor Berry
 * @author Parker Johnson
 */
public class ObservableBoard extends Observable implements Serializable {
    private PlaceBoard board;

    /**
     * empty constructor - here so client can create an Observableboard and then later actually have a PlaceBoard made
     * the reference is created as soon as client can and then updated
     */
    public ObservableBoard() {

    }

    /**
     * makes a PlaceBoard with give dimensions
     * @param dim dimensions of the board
     */
    public ObservableBoard(int dim) {
        board = new PlaceBoard(dim);
    }

    /**
     * returns the place board.getBoard
     * @return PlaceTile[][]
     */
    public PlaceTile[][] getBoard(){
        return board.getBoard();
    }

    /**
     * returns tile at row and column
     * @param row row of tile
     * @param col col of tile
     * @return the tile at the column and row for the PlaceBoard
     */
    public PlaceTile getTile(int row, int col) {
        return board.getTile(row, col);
    }

    /**
     * separate method from set tile - used when userboard is set up in Network client
     * doesn't update user about updates so is not repeatedly printing when board is being created
     * @param tile
     */
    public void setUpTile(PlaceTile tile) {
        board.setTile(tile);
    }

    /**
     * sets tile to give tile on PlaceBoard
     * @param tile the tile you want to change to
     */
    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        super.setChanged();
        super.notifyObservers();
    }

    /**
     * says if the tile is valid or not
     * @param tile: tile to check
     * @return: whether the tile is valid
     */
    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

    /**
     * the toString
     * @return: the toString
     */
    public String toString() {
        return board.toString();
    }

    /**
     * creates the board with the desired dimensions after ObservableBoard has been created
     * @param dim: dimensions of the board
     */
    public void allocate(int dim) {
        board = new PlaceBoard(dim);
    }

    /**
     * gives the dimensions of the board since those could not be accessed otherwise
     * @return: the dimensions of the board
     */
    public int getDim() {
        return board.DIM;
    }
}
