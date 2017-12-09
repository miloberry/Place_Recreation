package place.server;

import place.PlaceBoard;
import place.PlaceException;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class PlaceServer {
    private ServerSocket server;
    private int portNum;
    private PlaceBoard board;
    private NetworkServer networkServer;

    public PlaceServer(int port) throws PlaceException {
        try {
            this.server = new ServerSocket(port);
            portNum = port;
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



    public void run(boolean running, int dim) {
        board = new PlaceBoard(dim);
        networkServer = new NetworkServer();
        while (running) {
            try {
                System.out.println("looking for client");
                Socket temp = server.accept();
                System.out.println("found client: " + temp);
                networkServer.addClient(temp, board);
            }
            catch (IOException e) {
                System.err.println("Something has gone horribly wrong!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws PlaceException {
        if (!(args.length == 2)) {
            System.out.println("Usage: java PlaceServer port #_dim");
            System.exit(1);
        }
        else {
            try {
                PlaceServer server = new PlaceServer(Integer.parseInt(args[0]));
                server.run(true, Integer.parseInt(args[1]));
            }
            catch (PlaceException e) {
                System.err.println("Failed to start server!");
                e.printStackTrace();
            }
        }
    }
}
