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
import static com.jetbrains.Main.game;
import static com.jetbrains.Main.useDefaults;

//
// NOTE there should only be one Game object
//
class Game {
    //
    // game static variables
    // most are accessed before game object is created
    //
    static Stage gameStage;
    static boolean youWon;
    static boolean  youLost;
    static boolean stillPlayiing;
    static int maxBats = 2;

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
    void play(){
        gameStage.setTitle("Find The Wumpus");
        gio.gotoRoom(1);
    }

    static void youWon(){
        game.youWon = true;
        gio.updateInfo("You shot the wumpus.  YOU WIN !!!!!!!");
        ended();
    }

    static void youLost(String msg){
        youLost = true;
        stillPlayiing = false;
        gio.showDialog("YOU LOSE :-)",msg );
        ended();
    }

    static void ended(){
        // display the wumpus image as the splash screen
        gio.addSplash(gio.bpGame, "src/wumpus.png");
    }

    //
    // Game constructor
    //
    Game(String caveName, Stage stage){
        stillPlayiing = true;
        gameStage = stage;

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

        player = new Player();

        stats = new Stats();

        // which cave should we load
        if(caveName.equals("")){
            // must be from initial launch
            caveName = Game.gio.cavePicker();
            if(caveName.equals("") || caveName.equals(null) ){
                // could not load a cave
                Debug.error("no cave selected");
                System.exit(-1);
            }
        }

        gio = new GIO(caveName);

        map = new Map();

        Cave.wumpus = new Wumpus(initialRoom);

        cave = new Cave(caveName, initialRoom);

        bow = new Bow(3);
    }

    //
    // Game helper functions
    //
    String signIn() {
        String userName;
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
        userName = userNameField.getText();
        return userName;
    }
}

