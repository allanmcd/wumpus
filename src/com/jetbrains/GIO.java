//
// Wumpus Graphical Interface Object
//
package com.jetbrains;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.Random;

import static com.jetbrains.Cave.caveName;
import static com.jetbrains.Game.*;
import static com.jetbrains.Main.game;

//
// NOTE there should only be one GIO object
//
class GIO {
    //
    // GIO  constants
    //
    final int TOP = 0;
    final int LEFT = 1;
    final int BOTTOM = 2;
    final int RIGHT = 3;

    //
    // GIO static variables
    //
    static Group gioGroup;
    static Label lblInfo;
    static Scene gioScene;

    //
    // GIO methods
    //
    void gotoRoom(int roomNumber) {
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        Label lblRoomNumber = new Label("Room " + roomNumber);
        lblRoomNumber.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        GridPane.setHalignment(lblRoomNumber, HPos.CENTER);
        gridpane.add(lblRoomNumber, 15, 0);

        Label lblBlankLine = new Label("");
        lblRoomNumber.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        gridpane.add(lblBlankLine, 23, 1);

        lblInfo = new Label();
        lblInfo.setFont(Font.font("Verdana", 18));
        gridpane.add(lblInfo, 0, 35);

        game.player.roomNumber = roomNumber;

        gioGroup = new Group();

        gioGroup.getChildren().add(gridpane);
        Game.cave.rooms[roomNumber].draw();

        BorderPane.setAlignment(gioGroup,Pos.CENTER);

        bpGame.setCenter(gioGroup);

        Game.gameStage.setScene(gioScene);
        Game.gameStage.show();

        if (roomNumber == Cave.wumpus.roomNumber) {
            Game.youLost();
        }
        else if (Cave.rooms[roomNumber].hasBat()) {
            relocatePlayer();
        }
        else {
            // have to examine all mouse clicks because clicking on the transparent part of
            // the mow does not generate a mouseclick event for the bow image
            gioScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent evt) {
                    double mouseX = evt.getX();
                    double mouseY = evt.getY();
                    if (mouseX > bow.rect[LEFT] && mouseX < bow.rect[RIGHT]) {
                        if (mouseY > bow.rect[TOP] && mouseY < bow.rect[BOTTOM]) {
                            bow.fired = true;
                        }
                    }
                }
            });
        }
    }

    void updateInfo(String infoText) {
        gio.lblInfo.setText(infoText);
    }

    void showDialog(String dlgMsg) {
        Label msgLabel = new Label(dlgMsg);

        Dialog dialog = new Dialog<>();
        dialog.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 35, 20, 35));
        grid.add(msgLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2 + 100);
        });

        dialog.showAndWait();
    }

    //
    // GIO constructor
    //
    GIO() {
        tfRoomNumber.setAlignment(Pos.CENTER_RIGHT);
        // set up the sceeen display area
        gioScene = new Scene(bpGame, 400, 250);
        gameStage.setWidth(600);
        gameStage.setHeight(600);

        // build the menu bar
        //Build the first menu.
        Menu gameMenu = new Menu("Game");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem replayMenuItem = new MenuItem("Replay Current Game");
        MenuItem quitMenuItem = new MenuItem("Quit");

        replayMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                game = new Game(caveName, Main.primaryStage);
                if(game.valid){game.play();};
            }
        });

        quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });

        gameMenu.getItems().addAll(newMenuItem, replayMenuItem, quitMenuItem);

        MenuBar gameMenuBar = new MenuBar();
        gameMenuBar.getMenus().add(gameMenu);
        bpGame.setTop(gameMenuBar);
    }

    ;

    //
    // javafx controls
    //
    TextField tfRoomNumber = new TextField();

    BorderPane bpGame = new BorderPane();

    //
    // GIO helper functions
    //

    private void relocatePlayer() {
        showDialog("A bat has captured you and will transport you to another room");
        gio.gotoRoom(nextEmptyRoom());
    }

    private int nextEmptyRoom() {
        Random random = new Random();
        int nextEmptyRoomNumber = random.nextInt(29) + 1;

        boolean generateAnotherRoomNumber;
        do {
            // assume the current room number is OK
            generateAnotherRoomNumber = false;

            if (Cave.rooms[nextEmptyRoomNumber].hasBat()) {
                // not empty - bat in room
                generateAnotherRoomNumber = true;
            }

            if (Cave.rooms[nextEmptyRoomNumber].hasPit) {
                // not empty - pit in room
                generateAnotherRoomNumber = true;
            }

            if (nextEmptyRoomNumber == Cave.wumpus.roomNumber) {
                // not empty - wumpus in room
                generateAnotherRoomNumber = true;
            }

            if (generateAnotherRoomNumber) {
                // generate another room number to test
                nextEmptyRoomNumber = random.nextInt(29) + 1;
            }
        } while (generateAnotherRoomNumber);

        return nextEmptyRoomNumber;
    }
}
