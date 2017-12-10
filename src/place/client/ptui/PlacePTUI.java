package place.client.ptui;
import place.PlaceBoard;
import place.network.ObservableBoard;
import place.network.PlaceClient;
import java.io.IOException;
import java.util.Scanner;

public class PlacePTUI {
    private static String username;
    private static boolean running = false;
    private static ObservableBoard board = new ObservableBoard();

    public static void main(String[] args) throws IOException, InterruptedException{
        if (args.length != 3) {
            System.out.println("Usage: java PlacePTUI host #_port username");
            System.exit(1);
        }
        else {
            int port = Integer.parseInt(args[1]);
            String host = args[0];
            username = args[2];
            PlaceClient serverConn = new PlaceClient(host, port, board);
            serverConn.connectToServer(username);
            running = true;
            Scanner scanner = new Scanner(System.in);
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
    }
}

