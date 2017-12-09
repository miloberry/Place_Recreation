package place.server;

import place.PlaceException;
import place.network.ObservableBoard;
import place.network.PlaceRequest;

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

    public void addClient(String username) {
        try {
            PlaceClient temp = new PlaceClient("localhost", portNum, board);
            boolean connect = networkServer.addClient(temp, username);
            if (connect) {
                System.out.println("connected");
                clients.add(temp);
                networkServer.run(connect, username, board);
                temp.start();
                temp.join();
            }
        }
        catch (IOException e) {
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
            System.exit(1);
        }

        catch (InterruptedException e) {
            System.err.println("fuck");
            System.exit(1);
        }
    }

    public void run(boolean running, int dim) {
        board = new ObservableBoard(dim);
        networkServer = new NetworkServer();
        boolean worked = false;
        while (running) {
            try {
                Socket temp = server.accept();
                System.out.println("found client: " + temp);
                Scanner scanner = new Scanner(temp.getInputStream());
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] lines = line.split(" ");
                    if (lines.length == 2 && lines[0].equals(PlaceRequest.RequestType.LOGIN.toString())) {
                        System.out.println("LOGIN");
                        System.out.println(lines[1]);
                        addClient(lines[1]);
                    }
                }
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
