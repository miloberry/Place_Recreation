package place.network;

import place.PlaceException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class PlaceClient {
    private Socket socket;
    private Scanner scanner;
    private PrintStream printer;

    public PlaceClient(String hostname, int port, ObservableBoard model) throws IOException {
        try {
            this.socket = new Socket(hostname, port);
            this.printer = new PrintStream(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
        }

        catch (IOException e) {
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
        }
    }
}
