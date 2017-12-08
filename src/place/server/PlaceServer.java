package place.server;

import com.sun.security.ntlm.Client;
import place.PlaceException;
import place.network.PlaceClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PlaceServer {
    private ServerSocket server;
    private ArrayList<Client> clients = new ArrayList<>();

    public PlaceServer(int port) throws PlaceException {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    public void close() {
        try {
            this.server.close();
        } catch (IOException ioe) {
            // squash
        }
    }

    public void addClient() {

    }

    public void removeClient() {

    }

    public void run(boolean running) {
        while (running) {
            try {
                Socket temp = server.accept();
                PlaceClient tempClient = new PlaceClient(temp);
            }
            catch (IOException e) {
                System.err.println("Something has gone horribly wrong!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws PlaceException {
        if (!(args.length == 3)) {
            System.out.println("Usage: java ReversiServer port #_dim #_dim");
            System.exit(1);
        }
        else {
            try {
                PlaceServer server = new PlaceServer(Integer.parseInt(args[0]));
            }
            catch (PlaceException e) {
                System.err.println("Failed to start server!");
                e.printStackTrace();
            }
        }
    }
}
