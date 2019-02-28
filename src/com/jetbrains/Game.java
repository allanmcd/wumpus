package com.jetbrains;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Random;

import static com.jetbrains.Cave.initialRoom;
import static com.jetbrains.Cave.loadCave;
import static com.jetbrains.Main.useDefaults;

//
// NOTE there should only be one Game object
//
public final class Game {
    //
    // game static variables
    // most are accessed before game object is created
    //
    static Stage stage;
    static boolean loaded;
    static boolean youWon;
    static boolean  youLost;
    static boolean stillPlayiing;
    static int maxBats = 2;
    static String playerName;

    // game component objects
    static Bow bow;
    static Cave cave;
    static GIO gio;
    static Map map;
    static Player player;
    static Stats stats;

    //
    // Game methods
    //
    /*
    static void start(){

        // a blank cave name will force the user to pick one
        String caveName = "";
        if(useDefaults){
            initialRoom = 1;
            if(caveName.equals("")) {
                if (gio == null || gio.newCaveName == null || gio.newCaveName.equals(null)) {
                    caveName = "cave1";
                }
            }
        }
        else {
            // ask the user to sign in the first time a game is created
            if(Main.userName == null) {
                Main.userName = signIn();
            }

            // start in a random room
            Random random = new Random();
            initialRoom = random.nextInt(29) + 1;
        }

        newGame(caveName);

    }
    */

    static void play(){
        stage.setTitle("Find The Wumpus");

        if (cave.valid) {
            // make the stats pane visible
            stats.vBox.setVisible(true);

            // initialize the stats
            stats.setInitialValues();

            gio.gotoRoom(cave.initialRoom);
        }
    }

    static void youWon(){
        cave.wumpus.dead = true;
        youWon = true;
        Game.stats.update();
        gio.showDialog(  "YOU WIN","You shot the wumpus");
        ended();
    }

    static void youLost(String msg){
        youLost = true;
        stillPlayiing = false;
        gio.showDialog("YOU LOSE :-(",msg );
        ended();
    }

    static void ended(){
        // clear out the info text
        stats.txtInfo.setText("");

        // clear out the hint text
        stats.txtHint.setText("");

        // no longer in a cave
        gio.lblCaveName.setText("");

        // hide the stats pane
        stats.vBox.setVisible(false);

        // display the wumpus image as the splash screen
        gio.addSplash(gio.bpGame, "src/wumpus.png");
    }

    static void init(String caveName, Stage gameStage){
        stillPlayiing = true;
        stage = gameStage;

        // assume that the game will load without errors
        loaded = true;

        if(useDefaults){
            initialRoom = 1;
        }
        else {
            // start in a random room
            Random random = new Random();
            initialRoom = random.nextInt(29) + 1;
        }
        System.out.println("Initial Room is " + initialRoom);
        player = new Player();

        stats = new Stats();

        // which cave should we load
        if(caveName.equals("")) {
            // must be from initial launch
            caveName = gio.cavePicker();
        }

        gio = new GIO(caveName);

        if(caveName.equals("") || caveName.equals(null) ){
            // could not load a cave so bail - should display splash screen
            return;
        }

        map = new Map();

        Cave.wumpus = new Wumpus(initialRoom);

        cave = new Cave(caveName, initialRoom);
        loadCave(caveName);

        cave.pits = new Pits();

        bow = new Bow(3);

        loaded &= Trivia.init();
    }

    //
    // Game constructor
    //
    private Game(){
        // force it to be a static singleton
    }

    //
    // Game helper functions
    //
    public static void signIn() {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Sign In");
        dialog.setHeaderText("Please sign in.");
        dialog.setResizable(false);
        Label userNameLabel = new Label("Name:");
        TextField userNameField = new TextField();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 35, 20, 35));
        grid.add(userNameLabel, 1, 1);
        grid.add(userNameField, 2, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            dialog.close();
        });
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
            userNameField.requestFocus();
        });
        dialog.showAndWait();
        playerName = userNameField.getText();
    }
}

