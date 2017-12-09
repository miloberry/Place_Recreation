package place.client.ptui;

import place.PlaceBoard;
import place.network.PlaceRequest;
import place.server.NetworkServer;
import place.server.PlaceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlacePTUI implements Observer{
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
            Scanner sysIn = new Scanner(new InputStreamReader(System.in));
            serverConn.connectToServer(username);
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
