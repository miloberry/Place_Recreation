package place.server;
import place.PlaceException;
import place.network.ObservableBoard;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * the main server - just waits for clients and then sends then to NetworkServer
 * @author Taylor Berry
 * @author Parker Johnson
 */
public class PlaceServer {
    private ServerSocket server;
    private int portNum;
    private ObservableBoard board;
    private NetworkServer networkServer;

    /**
     * sets up server on port
     * @param port: port to start server on
     * @throws PlaceException
     */
    public PlaceServer(int port) {
        try {
            this.server = new ServerSocket(port);
            portNum = port;
        }
        catch (IOException e) {
            System.out.println("Error starting server on " + portNum);
        }
    }

    /**
     * sits and waits for client to connect then sends them to NetworkServer
     * also makes board that is sent to NS
     * @param running: if program is running
     * @param dim: dimensions of the board
     */
    public void run(boolean running, int dim) {
        board = new ObservableBoard(dim);
        networkServer = new NetworkServer();
        while (running) {
            try {
                System.out.println("looking for client");
                Socket temp = server.accept();
                networkServer.addClient(temp, board);
            }
            catch (IOException e) {
                System.err.println("Something has gone horribly wrong!");
                e.printStackTrace();
            }
        }
    }

    /**
     * main method - starts server if there are the correct number of args
     * @param args the program arguments - #_port #_dim
     * @throws PlaceException
     */
    public static void main(String[] args) throws PlaceException {
        if (!(args.length == 2)) {
            System.out.println("Usage: java PlaceServer #_port #_dim");
            System.exit(1);
        }
        else {
            PlaceServer server = new PlaceServer(Integer.parseInt(args[0]));
            server.run(true, Integer.parseInt(args[1]));
        }
    }
}
