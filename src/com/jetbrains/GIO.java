//
// Wumpus Graphical Interface Object
//
package com.jetbrains;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;

//
// NOTE there should only be one GIO object
//
class GIO {
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

        gioGroup.getChildren().add(gridpane);
        Game.cave.rooms[roomNumber].draw();

        Game.gameStage.setScene(new Scene(gioGroup, 300, 250));
        Game.gameStage.show();

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
