package place.network;

import place.PlaceBoard;
import place.PlaceTile;

import java.io.Serializable;
import java.util.Observable;

public class ObservableBoard extends Observable implements Serializable {
    private PlaceBoard board;

    public ObservableBoard() {

    }

    public ObservableBoard(int dim) {
        board = new PlaceBoard(dim);
    }

    public PlaceTile[][] getBoard(){
        return board.getBoard();
    }

    public PlaceTile getTile(int row, int col) {
        return board.getTile(row, col);
    }

    public void setUpTile(PlaceTile tile) {
        board.setTile(tile);
    }

    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        super.setChanged();
        super.notifyObservers();
    }

    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

    public String toString() {
        return board.toString();
    }

    public void allocate(int dim) {
        board = new PlaceBoard(dim);
    }

    public int getDim() {
        return board.DIM;
    }
}
