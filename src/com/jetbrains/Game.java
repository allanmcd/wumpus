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
    static Bow bow;
    static Cave cave;
    static Stage gameStage;
    static GIO gio;
    static Map map;
    static Player player;
    static boolean youWon;
    static boolean  youLost;
    static boolean stillPlayiing;
    static boolean valid;

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
    }

    static void youLost(String msg){
        youLost = true;
        stillPlayiing = false;
        gio.showDialog(msg + " YOU LOSE :-(");
    }

    //
    // Game constructor
    //
    Game(String caveName, Stage stage){
        stillPlayiing = true;
        gameStage = stage;

        if(useDefaults){
            initialRoom = 1;
        }
        else {
            login();
            // start in a random room
            Random random = new Random();
            initialRoom = random.nextInt(29) + 1;
        }
        Cave.wumpus = new Wumpus();

        player = new Player();

        gio = new GIO();
        map = new Map();
        cave = new Cave(caveName, initialRoom);
        valid = cave.valid;
        bow = new Bow(3);
    }

    //
    // Game helper functions
    //
    private void login() {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Please enter User Name and Password to login.");
        dialog.setResizable(false);
        Label userNameLabel = new Label("User Name:");
        Label passwordLabel = new Label("Password:");
        TextField userNameField = new TextField();
        PasswordField passwordField = new PasswordField();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 35, 20, 35));
        grid.add(userNameLabel, 1, 1);
        grid.add(userNameField, 2, 1);
        grid.add(passwordLabel, 1, 2);
        grid.add(passwordField, 2, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
        });
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
        });
        dialog.showAndWait();
    }

}

