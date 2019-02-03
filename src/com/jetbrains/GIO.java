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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.Random;

import static com.jetbrains.Game.*;

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

    //
    // GIO methods
    //
    void gotoRoom(int roomNumber){
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        Label lblRoomNumber = new Label("Room " + roomNumber);
        lblRoomNumber.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        GridPane.setHalignment(lblRoomNumber, HPos.CENTER);
        gridpane.add(lblRoomNumber, 23, 0);

        Label lblBlankLine = new Label("");
        lblRoomNumber.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        gridpane.add(lblBlankLine, 23, 1);

        lblInfo = new Label();
        lblInfo.setFont(Font.font("Verdana", 18));
        gridpane.add(lblInfo, 0, 35);

        gioGroup = new Group();

        Main.game.player.roomNumber = roomNumber;
        gioGroup.getChildren().add(gridpane);
        Game.cave.rooms[roomNumber].draw();

        Scene gioScene = new Scene(gioGroup, 300, 250);
        Game.gameStage.setScene(gioScene);
        Game.gameStage.show();

        if(roomNumber == Cave.wumpus.roomNumber) {
            Game.youLost();
        }else if(Cave.rooms[roomNumber].hasBat()) {
            relocatePlayer();
        }
        else{
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

    void updateInfo(String infoText){
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
        grid.setPadding(new Insets(20, 35, 20, 35));
        grid.add(msgLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
        });

        dialog.showAndWait();
    }

    //
    // GIO constructor
    //
    GIO(){
        tfRoomNumber.setAlignment(Pos.CENTER_RIGHT);
    };

    //
    // javafx controls
    //
    TextField tfRoomNumber = new TextField();

    //
    // GIO helper functions
    //

    private void relocatePlayer(){
        showDialog("A bat has captured you and will transport you to another room");
        gio.gotoRoom(nextEmptyRoom());
    }

    private int nextEmptyRoom(){
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

            if(generateAnotherRoomNumber) {
                // generate another room number to test
                nextEmptyRoomNumber = random.nextInt(29) + 1;
            }
        }while(generateAnotherRoomNumber);

        return nextEmptyRoomNumber;
    }

}
