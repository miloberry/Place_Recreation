package place.client.ptui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class PlacePTUI implements Observer{
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java PlacePTUI host #_port username");
            System.exit(1);
        }
        else {
            int port = Integer.parseInt(args[1]);
            String host = args[0];
            String username = args[2];
            try {
                Socket clientSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
            }
            catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + host);
                System.exit(1);
            }
        }
    }
    @Override
    public void update(Observable o, Object arg) {

    }
}
