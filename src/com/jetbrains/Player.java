package com.jetbrains;

import static com.jetbrains.Game.cave;
import javafx.beans.property.SimpleIntegerProperty;

//
// NOTE there is only be one Player object
//
public final class Player {
    //
    // Player instance variables
    //
    static SimpleIntegerProperty numberOfArrows = new SimpleIntegerProperty();
    static SimpleIntegerProperty numberOfCoins = new SimpleIntegerProperty();

    public static int nextTriviaIndex;

    static int roomNumber;
    static boolean isDead;
    static String name;
    // UNDONE need to save/load player object
    static int currentHighScore = 0;

    // position[X, Y, width, height];
    static double[] position;

    //
    // Player methods
    //
    static boolean isInRoom(int caveRoomNumber){
        boolean isInRoom = false;
        if(roomNumber == caveRoomNumber){isInRoom = true;}
        return isInRoom;
    }

    static void init(){

        roomNumber = cave.initialRoom;
        numberOfArrows.set(3);
        isDead = false;
    }

    static void load(){
        // UNDONE - need to implement
    }

    static void save(){
        // UNDONE - need to implement
    }

    //
    // Player constructor
    //
    private Player(){
        // Player is a static object, much like Math
    }
}
