package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.game;
import static com.jetbrains.Main.useDefaults;

//
// NOTE there should only be one Wumpus object
//
public class Wumpus {
    //
    // Wumpus instance variables
    //
    int roomNumber;
    boolean dead;

    //
    // Wumpus methods
    //
    boolean isInRoom(int caveRoomNumber){
        boolean isInRoom = false;
        if(roomNumber == caveRoomNumber){isInRoom = true;}
        return isInRoom;
    }

    boolean inAdjacentRoom(){
        boolean inAdjacentRoom = false;
        Room playerRoom = cave.rooms[game.player.roomNumber];
        for(int wallNumber = 0; wallNumber < 6; wallNumber++){
            Wall wall = playerRoom.walls[wallNumber];
            if(wall.hasTunne1) {
                if (isInRoom(wall.adjacentRoom)) {
                    inAdjacentRoom = true;
                    break;
                }
            }
        }
        return inAdjacentRoom;
    }
    //
    // Wumpus constructor
    //
    Wumpus(int initialRoom){
        if(useDefaults){
            roomNumber = 6;
        } else {
            Random random = new Random();
            // generate a random room from 1 to 30
             roomNumber = random.nextInt(29) + 1;
            if(roomNumber == initialRoom){
                // don't put the wumpus in the initial room
                roomNumber = random.nextInt(29) + 1;
            }
        }
        System.out.println("Wumpus assigned to " + roomNumber);
    }
}
