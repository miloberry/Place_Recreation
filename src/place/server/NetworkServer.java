package place.server;

import place.PlaceBoard;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class NetworkServer {
    private HashMap<String, ObjectOutputStream> clientOut = new HashMap<>();
    private HashMap<String, ObjectInputStream> clientIn = new HashMap<>();


    public void addClient(Socket socket, PlaceBoard board) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        PlaceRequest<?> req = (PlaceRequest<?>)in.readUnshared();
        if (req.getType() == PlaceRequest.RequestType.LOGIN) {
            String username = (String)req.getData();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            if (clientOut.containsKey(username)) {
                PlaceRequest<String> errorReq = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Name in use");
                out.writeUnshared(errorReq);
                out.flush();
                socket.close();
            } else {
                clientIn.put(username, in);
                clientOut.put(username, out);
                run(username, board);
            }
        }
    }

    public void run(String username, PlaceBoard board) throws IOException {
        ObjectOutputStream out = clientOut.get(username);
        PlaceRequest<String> loginSuccessReq = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, "login good");
        out.writeUnshared(loginSuccessReq);
        out.flush();
        PlaceRequest<PlaceBoard> boardReq = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board);
        out.writeUnshared(boardReq);
        out.flush();
    }

}
