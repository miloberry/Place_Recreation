package place.network;

import java.util.Observable;
import place.PlaceBoard;

public class ObservableBoard extends Observable {
    private PlaceBoard board;
    public ObservableBoard(int dim) {
        board = new PlaceBoard(dim);
    }
}
