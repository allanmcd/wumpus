package com.jetbrains;

import static com.jetbrains.Game.cave;
import javafx.beans.property.SimpleIntegerProperty;

//
// NOTE there should only be one Player object
//
public class Player {
    //
    // Player instance variables
    //
    static SimpleIntegerProperty numberOfArrows = new SimpleIntegerProperty();
    static SimpleIntegerProperty numberOfCoins = new SimpleIntegerProperty();

    public static int nextTriviaIndex;

    int roomNumber;

    // position[X, Y, width, height];
    double[] position;

    //
    // Player methods
    //
    boolean isInRoom(int caveRoomNumber){
        boolean isInRoom = false;
        if(roomNumber == caveRoomNumber){isInRoom = true;}
        return isInRoom;
    }

    //
    // Player constructor
    //
    Player(){

        roomNumber = cave.initialRoom;
        numberOfArrows.set(3);
    }
}
