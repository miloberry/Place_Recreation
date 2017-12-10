package place.network;

import java.io.*;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class PlaceClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ObservableBoard board;
    private ObservableBoard userboard;
    private String username;

    public PlaceClient(String hostname, int port, ObservableBoard userboard) throws IOException {
        try {
            this.userboard = userboard;
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

    public synchronized void changeTile(int row, int col, int color) throws IOException, InterruptedException {
        PlaceColor placeColor = null;
        for (PlaceColor test: PlaceColor.values()) {
            if (test.getNumber() == color) {
                placeColor = test;
            }
        }
        if (placeColor != null) {
            PlaceTile toChange = new PlaceTile(row, col, username, placeColor, System.currentTimeMillis());
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
                    board = (ObservableBoard) req.getData();
                    userboard.allocate(board.getDim());
                    for (int r = 0; r < board.getDim(); r++) {
                        for (int c = 0; c < board.getDim(); c++) {
                            userboard.setUpTile(board.getTile(r, c));
                        }
                    }
                    System.out.println(board);
                    currentThread().sleep(1000);
                }
                if (req.getType() == PlaceRequest.RequestType.ERROR) {
                    System.out.println(req.getData());
                    System.exit(1);
                }
                if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    userboard.setTile((PlaceTile) req.getData());
                    System.out.println(userboard);
                }
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e){
            try {
                PlaceRequest<String> error = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "system issue");
                out.writeUnshared(error);
                out.flush();
                socket.close();
                System.exit(2);
            }
            catch (Exception k) {
                System.exit(2);
            }
        }
    }
}