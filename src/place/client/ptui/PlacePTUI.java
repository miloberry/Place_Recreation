package place.client.ptui;
import place.network.ObservableBoard;
import place.network.PlaceClient;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * The text based client
 * @author : Taylor Berry
 * @author : Parker Johnson
 */
public class PlacePTUI implements Observer{
    private static String username;
    private static boolean running = false;
    private static ObservableBoard board = new ObservableBoard();

    /**
     * checks that the args are good then sends information to run which is a non-static method
     * @param args arguments Usage: java PlacePTUI host #_port username
     * @throws IOException if stream gets corrupted
     * @throws InterruptedException if thread gets interrupted
     */
    public static void main(String[] args) throws IOException, InterruptedException{
        if (args.length != 3) {
            System.out.println("Usage: java PlacePTUI host #_port username");
            System.exit(1);
        }
        else {
            int port = Integer.parseInt(args[1]);
            String host = args[0];
            username = args[2];
            PlacePTUI temp = new PlacePTUI();
            temp.run(port, host);
        }
    }

    /**
     * sets up communication with the server through a PlaceClient and then creates a Scanner
     * to read in input from user to relay back to the server
     * @param port the port to connect to
     * @param host the host name
     * @throws IOException if the streams close/get corrupted
     * @throws InterruptedException if the thread gets interrupted
     */
    public void run(int port, String host) throws IOException, InterruptedException {
        PlaceClient serverConn = new PlaceClient(host, port, board);
        serverConn.connectToServer(username);
        running = true;
        Scanner scanner = new Scanner(System.in);
        board.addObserver(this);
        while (running) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                String[] inputs = input.split(" ");
                int row = Integer.parseInt(inputs[0]);
                int col = Integer.parseInt(inputs[1]);
                int color = Integer.parseInt(inputs[2]);
                if (row == -1) {
                    System.out.println("Invalid row");
                    System.exit(1);
                }
                else {
                    serverConn.changeTile(row, col, color);
                }
            }
        }
    }

    /**
     * prints board when board has been updated
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        System.out.println(board);
    }
}

