package place.server;

import java.io.*;

import place.PlaceException;
import place.network.ObservableBoard;
import place.network.PlaceRequest;

import java.net.Socket;
import java.util.Scanner;

public class PlaceClient extends Thread {
    private Socket socket;
    private Scanner scanner;
    private PrintWriter writer;

    public PlaceClient(String hostname, int port) throws IOException {
        try {
            this.socket = new Socket(hostname, port);
            this.writer = new PrintWriter(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
            System.out.println(socket.getOutputStream());
        }

        catch (IOException e) {
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
        }
    }
    public void connectToServer(String username) {
        writer.println(PlaceRequest.RequestType.LOGIN + " " + username);
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            System.out.println("Place Client run");
            System.out.println("Scanner: " + socket.getInputStream());
            while (true) {
                if (scanner.hasNextLine()) {
                    System.out.println("working here");
                    String input = scanner.nextLine();
                    String[] inputLines = input.split(" ");
                    if (input.equals(PlaceRequest.RequestType.LOGIN_SUCCESS.toString())) {
                        System.out.println("connected");
                    }
                    if (inputLines.length == 2 && inputLines[0].equals(PlaceRequest.RequestType.BOARD.toString())) {
                        System.out.println(inputLines[1]);
                    }
                }
            }
        }
            catch(IOException e) {

            }
        }
//        while (scanner.hasNextLine()) {
//            System.out.println("working here");
//            String input = scanner.nextLine();
//            String[] inputLines = input.split(" ");
//            if (input.equals(PlaceRequest.RequestType.LOGIN_SUCCESS.toString())) {
//                System.out.println("connected");
//            }
//            if (inputLines.length == 2 && inputLines[0].equals(PlaceRequest.RequestType.BOARD.toString())) {
//                System.out.println(inputLines[1]);
//            }
//        }

    }