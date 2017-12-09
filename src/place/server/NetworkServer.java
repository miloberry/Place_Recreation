package place.server;

import place.PlaceBoard;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class NetworkServer extends Thread implements Observer{
    private HashMap<String, ObjectOutputStream> clientOut = new HashMap<>();
    private HashMap<String, ObjectInputStream> clientIn = new HashMap<>();
    private static boolean running = false;
    private PlaceBoard board;
    private String workingUser;


    public void addClient(Socket socket, PlaceBoard board) {
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
            PlaceRequest<PlaceBoard> boardReq = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board);
            out.writeUnshared(boardReq);
            out.flush();
            this.tileChanging(board, in);
        }
        catch (IOException e) {
            System.exit(1);
        }
    }

    public void tileChanging(PlaceBoard board, ObjectInputStream in) {
        try {
            while (running) {
                PlaceRequest<?> req = (PlaceRequest<?>)in.readUnshared();
                if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile temp = (PlaceTile) req.getData();
                    if (board.isValid(temp)) {
                        board.setTile(temp);
                        PlaceRequest<PlaceTile> tileChanged = new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, temp);
                        for (ObjectOutputStream output: clientOut.values()) {
                            output.writeUnshared(tileChanged);
                            output.flush();
                        }
                    }
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.exit(1);
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
