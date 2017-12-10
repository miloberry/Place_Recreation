package place.server;
import place.PlaceTile;
import place.network.ObservableBoard;
import place.network.PlaceRequest;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * handles all client communication and keeps a list of all open sockets to communicate with
 * @author Taylor Berry
 * @author Parker Johnson
 */
public class NetworkServer extends Thread {
    private HashMap<String, ObjectOutputStream> clientOut = new HashMap<>();
    private HashMap<String, ObjectInputStream> clientIn = new HashMap<>();
    private HashMap<String, Socket> openSockets = new HashMap<>();
    private static boolean running = false;
    private ObservableBoard board;
    private String workingUser;

    /**
     * adds a client to list of running clients and creates input and output
     * if their username is not already in use, else send error and starts a thread
     * @param socket socket to connect
     * @param board board to share with client
     */
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
                    System.out.println(workingUser + " has logged in at " + socket);
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("yikes");
            System.exit(1);
        }
    }

    /**
     * tells client it has been successfully connected then sends board and then changes to waiting for tile changes
     */
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

    /**
     * while running the Network client waits for requests to change the tile and if they are valid tiles then it
     * will change the tile and tell the clients that the tile has changed
     * @param board: board that is being updated
     * @param in: the input it is listening on
     * @param username: the client that is asking to change tiles
     */
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
            try {
                openSockets.get(username).close();
                openSockets.remove(username);
                clientOut.remove(username);
                clientIn.remove(username);
                System.out.println(username + " has disconnected");
            }catch (IOException f){
                System.out.println("Something not good has happened");
            }
        }
    }
}