package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Cave.initialRoom;
import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.useDefaults;
import static jdk.nashorn.internal.objects.NativeMath.random;

//
// NOTE there should only be one Wumpus object
//
public final class Wumpus {
    //
    // Wumpus instance variables
    //
    static int roomNumber;
    static boolean isDead;
    static Random random = new Random();

    //
    // Wumpus methods
    //
    static boolean isInRoom(int caveRoomNumber){
        boolean isInRoom = false;
        if(roomNumber == caveRoomNumber){isInRoom = true;}
        return isInRoom;
    }

    static boolean inAdjacentRoom(){
        boolean inAdjacentRoom = false;
        Room playerRoom = cave.rooms[Player.roomNumber];
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

    static void moveToRoom(int rooomToMoveTo){
        roomNumber = roomNumber;
    }

    static void moveToRandomRoom(){
        // generate a different random room from 1 to 30
        int newWumpusRoomNumber = random.nextInt(29) + 1;

        boolean generateAnotherWumpusRoomNumber;
        do {
            // assume the current bat room number is OK
            generateAnotherWumpusRoomNumber = false;

            if (newWumpusRoomNumber == Player.roomNumber) {
                // don't put the wumpus in the same room as the player
                generateAnotherWumpusRoomNumber = true;
            }

            if (Cave.rooms[newWumpusRoomNumber].hasPit) {
                // don't put the Wumpus in a room with a pit
                generateAnotherWumpusRoomNumber = true;
            }

            if (newWumpusRoomNumber == Wumpus.roomNumber) {
                // don't put the Wumpus back in the same room
                generateAnotherWumpusRoomNumber = true;
            }

            if(generateAnotherWumpusRoomNumber) {
                // generate another bat room number to test
                newWumpusRoomNumber = random.nextInt(29) + 1;
            }
        }while(generateAnotherWumpusRoomNumber);

        roomNumber = newWumpusRoomNumber;
        System.out.println("Wumpus moved to room " + roomNumber );
    }

    static void init(int initialRoom){
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
        isDead = false;
        System.out.println("Wumpus assigned to " + roomNumber);
    }

    //
    // Wumpus constructor
    //
    private Wumpus(int initialRoom){
    }
}
