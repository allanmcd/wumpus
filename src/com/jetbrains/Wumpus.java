package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.useDefaults;

//
// NOTE there should only be one Wumpus object
//
public class Wumpus {
    //
    // Wumpus instance variables
    //
    int roomNumber;

    //
    // Wumpus methods
    //
    boolean isInRoom(int caveRoomNumber){
        boolean isInRoom = false;
        if(roomNumber == caveRoomNumber){isInRoom = true;}
        return isInRoom;
    }

    //
    // Wumpus constructor
    //
    Wumpus(){
        if(useDefaults){
            roomNumber = 6;
        } else {
            Random random = new Random();
            // generate a random room from 1 to 30
             roomNumber = random.nextInt(29) + 1;
            if(roomNumber == cave.initialRoom){
                // don't put the wumpus in the initial room
                roomNumber = random.nextInt(29) + 1;
            }
        }
    }
}
