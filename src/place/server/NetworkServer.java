package place.server;

import place.PlaceBoard;
import place.PlaceTile;
import place.network.ObservableBoard;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class NetworkServer extends Thread {
    private HashMap<String, ObjectOutputStream> clientOut = new HashMap<>();
    private HashMap<String, ObjectInputStream> clientIn = new HashMap<>();
    private HashMap<String, Socket> openSockets = new HashMap<>();
    private static boolean running = false;
    private ObservableBoard board;
    private String workingUser;
    private long startTime = System.currentTimeMillis();


    public void addClient(Socket socket, ObservableBoard board) {
        running = true;
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            PlaceRequest<?> req = (PlaceRequest<?>)in.readUnshared();
            this.board = board;
            if (req.getType() == PlaceRequest.RequestType.LOGIN) {
                this.workingUser = (String) req.getData();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                if (clientOut.containsKey(workingUser)) {
                    PlaceRequest<String> errorReq = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Username in use");
                    out.writeUnshared(errorReq);
                    out.flush();
                    socket.close();
                } else {
                    clientIn.put(workingUser, in);
                    clientOut.put(workingUser, out);
                    openSockets.put(workingUser, socket);
                    Thread netThread = new Thread(() -> this.run());
                    netThread.start();
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("yikes");
            System.exit(1);
        }
    }

    public void run() {
        try {
            String username = workingUser;
            ObjectOutputStream out = clientOut.get(username);
            ObjectInputStream in = clientIn.get(username);
            PlaceRequest<String> loginSuccessReq = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS,
                    "logged in as " + username);
            out.writeUnshared(loginSuccessReq);
            out.flush();
            PlaceRequest<ObservableBoard> boardReq = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board);
            out.writeUnshared(boardReq);
            out.flush();
            this.tileChanging(board, in, username);
        }
        catch (IOException e) {
            System.exit(1);
        }
    }

    public void tileChanging(ObservableBoard board, ObjectInputStream in, String username) {
        try {
            while (running) {
                PlaceRequest<?> req = (PlaceRequest<?>)in.readUnshared();
                if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile temp = (PlaceTile) req.getData();
                    temp.setTime(System.currentTimeMillis());
                    if (board.isValid(temp)) {
                        board.setTile(temp);
                        PlaceRequest<PlaceTile> tileChanged = new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, temp);
                        for (ObjectOutputStream output: clientOut.values()) {
                            output.writeUnshared(tileChanged);
                            output.flush();
                        }
                    }
                }
                if (req.getType() == PlaceRequest.RequestType.ERROR) {
                    openSockets.get(username).close();
                    openSockets.remove(username);
                    clientIn.remove(username);
                    clientOut.remove(username);

                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.exit(1);
        }
    }
}