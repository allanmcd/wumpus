package com.jetbrains;

import com.jetbrains.Room;
import com.jetbrains.Wall;

import java.util.Random;

import static com.jetbrains.Cave.initialRoom;
import static com.jetbrains.Cave.rooms;
import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.useDefaults;

public class Pits {
    //
    // Pit constants
    //
    final int NUMBER_OF_PITS = 2;
    //
    // Pit instance variables
    //

    // not currently used - don't know why
    Pit pits[] = new Pit[2];

    int pitRooms[] = new int[2];
    int roomNumber;

    //
    // Pit methods
    //
    boolean isInRoom(int roomNumber){
        Room room = rooms[roomNumber];
        return room.hasPit;
    }

    boolean inAdjacentRoom(){
        boolean inAdjacentRoom = false;
        Room playerRoom = rooms[Game.player.roomNumber];
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

    void addPits(int numberOfPits){
        if (useDefaults) {
            rooms[13].hasPit = true;
            rooms[9].hasPit = true;
        } else {
            Random random = new Random();
            int pitRoomNumber;
            Boolean generateAnotherPitRoomNumber = false;

            for (int pitNumber = 1; pitNumber <= NUMBER_OF_PITS; pitNumber++) {
                do {
                    // assume the current bat room number is OK
                    generateAnotherPitRoomNumber = false;

                    pitRoomNumber = random.nextInt(29) + 1;

                    if (pitRoomNumber == initialRoom) {
                        // don't put a pit in the initial room
                        generateAnotherPitRoomNumber = true;
                    }

                    if (                                                                                                                                                                                 Cave.rooms[pitRoomNumber].hasBat()) {
                        // don't put a pit in a room with a bat
                        generateAnotherPitRoomNumber = true;
                    }

                    if (Cave.rooms[pitRoomNumber].hasPit) {
                        // don't put a pit in a room that already has a pit
                        generateAnotherPitRoomNumber = true;
                    }

                    if (pitRoomNumber == Cave.wumpus.roomNumber) {
                        // don't put a pit in a room with the Wumpus
                        generateAnotherPitRoomNumber = true;
                    }

                    if (generateAnotherPitRoomNumber) {
                        // generate another pit room number to test
                        pitRoomNumber = random.nextInt(29) + 1;
                    }
                } while (generateAnotherPitRoomNumber);

                rooms[pitRoomNumber].hasPit = true;
                pitRooms[pitNumber-1] = pitRoomNumber;
                System.out.println("pit assigned to room " + pitRoomNumber);

            }
        }
    }

    int roomWithPitInIt(){
        Random rnd = new Random();
        int pitRoomIndex = rnd.nextInt(NUMBER_OF_PITS);
        return pitRooms[pitRoomIndex];
    }


}

















