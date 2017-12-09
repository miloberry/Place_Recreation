package place.network;

import java.util.Observable;
import place.PlaceBoard;
import place.PlaceTile;

public class ObservableBoard extends Observable {
    private PlaceBoard board;
    public ObservableBoard(int dim) {
        board = new PlaceBoard(dim);
    }

    public String toString() {
        return board.toString();
    }

    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        notifyObservers();
    }

    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

    public PlaceTile getTile(int row, int col) {
        return board.getTile(row, col);
    }

    public PlaceTile[][] getBoard() {
        return board.getBoard();
    }
}
