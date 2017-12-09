package place.network;
import place.PlaceBoard;
import place.PlaceTile;
import java.util.Observable;

public class ObservableBoard extends Observable{
    private PlaceBoard board;

    public ObservableBoard(int dim) {
        board = new PlaceBoard(dim);
    }

    public PlaceTile[][] getBoard() {
        return board.getBoard();
    }

    public PlaceTile getTile(int row, int col) {
        return board.getTile(row, col);
    }

    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        notifyObservers();
    }

    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

    public String toString() {
        return board.toString();
    }
}
