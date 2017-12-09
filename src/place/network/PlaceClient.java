package place.network;

import java.io.*;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class PlaceClient extends Thread implements Observer{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private PlaceBoard board;
    private String username;

    public PlaceClient(String hostname, int port) throws IOException {
        try {
            this.socket = new Socket(hostname, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            Thread netThread = new Thread(() -> this.run());
            netThread.start();

        } catch (IOException e) {
            System.err.println("Cannot connect to server " + hostname);
            System.exit(1);
            e.printStackTrace();
        }
    }

    public void connectToServer(String username) throws IOException {
        this.username = username;
        PlaceRequest<String> loginReq = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, username);
        out.writeUnshared(loginReq);
        out.flush();
    }

    public synchronized void changeTile(int row, int col, int color) throws IOException {
        PlaceColor placeColor = null;
        for (PlaceColor test: PlaceColor.values()) {
            if (test.getNumber() == color) {
                placeColor = test;
            }
        }
        if (placeColor != null) {
            PlaceTile toChange = new PlaceTile(row, col, username, placeColor);
            PlaceRequest<PlaceTile> changeReq = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, toChange);
            out.writeUnshared(changeReq);
            out.flush();
        }
        else {
            System.out.println("Color not valid - Please Enter Number between 0 - 15");
        }
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
                    currentThread().sleep(1000);
                }
                if (req.getType() == PlaceRequest.RequestType.ERROR) {
                    System.out.println(req.getData());
                    System.exit(1);
                }
                if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    board.setTile((PlaceTile) req.getData());
                    System.out.println(board);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("yikes");
            System.exit(1);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println(board);
    }
}