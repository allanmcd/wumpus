//
// Wumpus Graphical Interface Object
//
package com.jetbrains;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;

import static com.jetbrains.Game.bow;

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

        gioGroup = new Group();

        Main.game.player.roomNumber = roomNumber;
        gioGroup.getChildren().add(gridpane);
        Game.cave.rooms[roomNumber].draw();

        Scene gioScene = new Scene(gioGroup, 300, 250);
        Game.gameStage.setScene(gioScene);
        Game.gameStage.show();

        gioScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent evt) {
                        double mouseX = evt.getX();
                        double mouseY = evt.getY();
                        if (mouseX > bow.rect[LEFT] && mouseX < bow.rect[RIGHT]) {
                            if (mouseY > bow.rect[TOP] && mouseY < bow.rect[BOTTOM]) {
                                System.out.println("mouse click detected! in bow " + evt.getSource());
                                bow.fired = true;
                            }
                        }
                    }
                });
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
}
