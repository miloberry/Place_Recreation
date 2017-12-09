package place.network;

import java.io.*;

import place.PlaceBoard;
import place.PlaceTile;

import java.net.Socket;

public class PlaceClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private PlaceBoard board;

    public PlaceClient(String hostname, int port) throws IOException {
        try {
            this.socket = new Socket(hostname, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("output");
            System.out.println("input");
            Thread netThread = new Thread(() -> this.run());
            netThread.start();

        } catch (IOException e) {
            System.err.println("Cannot connect to server " + hostname);
            System.exit(1);
            e.printStackTrace();
        }
    }

    public void connectToServer(String username) throws IOException {
        PlaceRequest<String> loginReq = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, username);
        out.writeUnshared(loginReq);
        out.flush();
    }

    @Override
    public void run() {
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                PlaceRequest<?> req =(PlaceRequest<?>) in.readUnshared();
                if (req.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                    System.out.println(req.getData());
                }
                if (req.getType() == PlaceRequest.RequestType.BOARD) {
                    board = (PlaceBoard) req.getData();
                    System.out.println(board);
                }
                if (req.getType() == PlaceRequest.RequestType.ERROR) {
                    System.out.println(req.getData());
                    System.exit(1);
                }
                if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    board.setTile((PlaceTile) req.getData());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("yikes");
            System.exit(1);
        }
    }
}