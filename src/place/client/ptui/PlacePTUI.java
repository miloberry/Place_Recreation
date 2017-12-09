package place.client.ptui;
import place.network.PlaceClient;
import java.io.IOException;

public class PlacePTUI {
    private static String username;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java PlacePTUI host #_port username");
            System.exit(1);
        }
        else {
            int port = Integer.parseInt(args[1]);
            String host = args[0];
            username = args[2];
            PlaceClient serverConn = new PlaceClient(host, port);
            serverConn.connectToServer(username);
        }
    }
}
