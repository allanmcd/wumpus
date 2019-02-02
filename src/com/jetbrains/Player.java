package com.jetbrains;

//
// NOTE there should only be one Player object
//
public class Player {
    //
    // Player instance variables
    //
    int roomNumber;

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
    Player(int initialRoom){
        roomNumber = initialRoom;
    }
}
