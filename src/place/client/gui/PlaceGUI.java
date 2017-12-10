package place.client.gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import place.PlaceColor;
import place.PlaceException;
import place.network.ObservableBoard;
import place.network.PlaceClient;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaceGUI extends Application implements Observer{
    private static String username;
    private static boolean running = false;
    private static String host;
    private static int port;
    private static ObservableBoard board = new ObservableBoard();
    private Stage mainStage;
    private Rectangle[][] rectangles;
    private ToggleButton[] selectColors = new ToggleButton[16];
    private PlaceClient serverConn;
    private boolean sleeping;

    public static void main(String[] args) throws IOException, InterruptedException{
        if (args.length != 3) {
            System.out.println("Usage: java PlacePTUI host #_port username");
            System.exit(1);
        }
        host = args[0];
        port = Integer.parseInt(args[1]);
        username = args[2];
        Application.launch(args);
    }

    public void init() throws PlaceException,  IOException {
        serverConn = new PlaceClient(host, port, board);
        serverConn.connectToServer(username);
        running = true;
        board.addObserver(this);
    }

    public void start(Stage mainStage) throws InterruptedException {
        int count = 0;
        ToggleGroup colors = new ToggleGroup();
        for (PlaceColor color: PlaceColor.values()) {
            ToggleButton temp = new ToggleButton();
            temp.setToggleGroup(colors);
            temp.setMaxSize(30, 30);
            temp.setMinSize(30, 30);
            temp.setBackground(new Background(new BackgroundFill(Paint.valueOf(color.name()), CornerRadii.EMPTY, Insets.EMPTY)));
            selectColors[count]= temp;
            count++;
        }
        sleeping = true;
        Thread.sleep(1000);
        sleeping = false;
        rectangles = new Rectangle[board.getDim()][board.getDim()];
        BorderPane main = new BorderPane();
        GridPane grid = new GridPane();
        for (int r =0; r < board.getDim(); r++) {
            for (int c = 0; c < board.getDim(); c++) {
                Rectangle temp = new Rectangle(48, 48);
                temp.setFill(Paint.valueOf(board.getTile(r, c).getColor().name()));
                int row = r;
                int col = c;
                temp.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override public void handle(javafx.scene.input.MouseEvent event) {
                        try {
                            int chosen = -1;
                            for (int i = 0; i < selectColors.length; i++) {
                                if (selectColors[i].isSelected()) {
                                    chosen = i;
                                }
                            }
                            if (!sleeping) {
                                serverConn.changeTile(row, col, chosen);
                            }
                        }
                        catch (IOException | InterruptedException k) {
                            System.exit(3);
                        }
                    }
                } );
                Tooltip tooltip = new Tooltip();
                long secs = board.getTile(row, col).getTime()/1000;
                long seconds = secs%60;
                long minutes = (secs%3600 - seconds)/60;
                long hours = (secs% (3600 * 24) - ((minutes*60) + seconds))/(60*24);
                Date now = new Date();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
                String secString;
                String hoursString;
                String minString;
                if (seconds < 10){
                    secString = "0" + seconds;
                }
                else {
                    secString = String.valueOf(seconds);
                }
                if (minutes < 10) {
                    minString = "0" + minutes;
                }
                else {
                    minString = String.valueOf(minutes);
                }
                if (hours < 10) {
                    hoursString = "0" + hours;
                }
                else {
                    hoursString = String.valueOf(hours);
                }
                tooltip.setText(
                        "Coordinate: (" + row + ", " + col + ")\n" +
                                "Owner: " + board.getTile(row, col).getOwner() + "\n" +
                                "Time Changed: " + dateFormatter.format(now) + " " + hoursString + ":" + minString + ":" + secString + "\n" +
                                "Color: " + board.getTile(row, col).getColor().name().toLowerCase()
                );
                Tooltip.install(temp, tooltip);
                rectangles[r][c] = temp;
                grid.add(temp, r, c);
            }
        }
        HBox hbox = new HBox();
        hbox.getChildren().addAll(selectColors);
        mainStage.setTitle("Place: " + username);
        grid.setGridLinesVisible(true);
        main.setBottom(hbox);
        main.setCenter(grid);
        Scene sc = new Scene(main);
        mainStage.setScene(sc);
        mainStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            System.out.println("update");
            for (int r = 0; r < board.getDim(); r++) {
                for (int c = 0; c < board.getDim(); c++) {
                    int row = r;
                    int col = c;
                    long secs = board.getTile(row, col).getTime()/1000;
                    long seconds = secs%60;
                    long minutes = (secs%3600 - seconds)/60;
                    long hours = (secs% (3600 * 24) - ((minutes*60) + seconds))/(60*24);
                    String secString;
                    String hoursString;
                    String minString;
                    if (seconds < 10){
                        secString = "0" + seconds;
                    }
                    else {
                        secString = String.valueOf(seconds);
                    }
                    if (minutes < 10) {
                        minString = "0" + minutes;
                    }
                    else {
                        minString = String.valueOf(minutes);
                    }
                    if (hours < 10) {
                        hoursString = "0" + hours;
                    }
                    else {
                        hoursString = String.valueOf(hours);
                    }
                    Date now = new Date();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(
                            "Coordinate: (" + row + ", " + col + ")\n" +
                                    "Owner: " + board.getTile(row, col).getOwner() + "\n" +
                                    "Time Changed: " + dateFormatter.format(now) + " " + hoursString + ":" + minString + ":" + secString + "\n" +
                                    "Color: " + board.getTile(row, col).getColor().name().toLowerCase()
                    );
                    Platform.runLater(()-> Tooltip.install(rectangles[row][col], tooltip));
                    Platform.runLater(() -> rectangles[row][col].setFill(Paint.valueOf(board.getTile(row, col).getColor().name())));
                }
            }
            Platform.runLater(() -> mainStage.show());
            sleeping = true;
            Thread.sleep(500);
            sleeping = false;
        }
        catch (InterruptedException e) {
            System.exit(3);
        }
    }
}
