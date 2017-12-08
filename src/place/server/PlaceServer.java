package place.server;

import place.PlaceException;
import place.network.ObservableBoard;
import place.network.PlaceClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;

public class PlaceServer {
    private ServerSocket server;
    private HashSet<PlaceClient> clients = new HashSet<>();
    private int portNum;
    private ObservableBoard board;

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

    public void addClient() throws IOException {
        try {
            PlaceClient temp = new PlaceClient("localhost", portNum, board);
            clients.add(temp);
        }
        catch (IOException e) {
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
        }
    }

    public void removeClient() {

    }

    public void run(boolean running, int dim) {
        board = new ObservableBoard(dim);
        while (running) {
            System.out.println("waiting for client");
            try {
                Socket temp = server.accept();
                addClient();
                PrintWriter writer = new PrintWriter(temp.getOutputStream());
                writer.println("LOGIN_SUCESSFUL");
                writer.println("BOARD");
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
