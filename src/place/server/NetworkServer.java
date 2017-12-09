package place.server;

import place.network.ObservableBoard;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public class NetworkServer {
    private HashMap<String, OutputStream> clients = new HashMap<>();

    public boolean addClient(PlaceClient client, String username) throws IOException {
        if (clients.containsKey(username)) {
            return false;
        }
        else {
            clients.put(username, client.getOutputStream());
            return true;
        }
    }

    public void run(boolean worked, String username, ObservableBoard board) {
        System.out.println("work please");
        if (worked) {
            System.out.println("here " + clients.get(username).toString());
            PrintWriter writer = new PrintWriter(clients.get(username), true);
            writer.println(PlaceRequest.RequestType.LOGIN_SUCCESS);
            writer.println(PlaceRequest.RequestType.BOARD + " " + board);
        }
    }

}
