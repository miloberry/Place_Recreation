package place.client.gui;
import javafx.*;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import place.PlaceBoard;
import place.PlaceColor;
import place.network.PlaceClient;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class PlaceGUI extends Application {
    private static String username;
    private static boolean running = false;

    public static void main(String[] args) throws IOException, InterruptedException{
        if (args.length != 3) {
            System.out.println("Usage: java PlacePTUI host #_port username");
            System.exit(1);
        }
        else {
            int port = Integer.parseInt(args[1]);
            String host = args[0];
            username = args[2];
            PlaceClient serverConn = new PlaceClient(host, port);
            serverConn.connectToServer(username);
            running = true;
            Application.launch(args);
        }
    }

    public void init() {

    }

    public void start(Stage mainStage) {
        BorderPane main = new BorderPane();
        GridPane grid = new GridPane();
        Rectangle fuck = new Rectangle(100, 100, Paint.valueOf(PlaceColor.WHITE.toString()));
        mainStage.show();
    }
}
