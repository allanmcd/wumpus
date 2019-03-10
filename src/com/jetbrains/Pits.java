package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Cave.initialRoom;
import static com.jetbrains.Cave.rooms;
import static com.jetbrains.Main.useDefaults;

public final class Pits {
    //
    // Pit constants
    //
    static final int NUMBER_OF_PITS = 2;
    //
    // Pit instance variables
    //

    static int pitRooms[] = new int[2];
    static int roomNumber;

    //
    // Pit methods
    //
    static boolean isInRoom(int roomNumber){
        Room room = rooms[roomNumber];
        return room.hasPit;
    }

    static boolean inAdjacentRoom(){
        boolean inAdjacentRoom = false;
        Room playerRoom = rooms[Player.roomNumber];
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

    static void addPits(int numberOfPits){
        if (useDefaults) {
            int pitRoom1 = 13;
            int pitRoom2 = 9;
            rooms[pitRoom1].hasPit = true;
            rooms[pitRoom2].hasPit = true;
            pitRooms[0] = pitRoom1;
            pitRooms[1] = pitRoom2;
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

                    if (pitRoomNumber == Wumpus.roomNumber) {
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

    static int roomWithPitInIt(){
        Random rnd = new Random();
        int pitRoomIndex = rnd.nextInt(NUMBER_OF_PITS);
        return pitRooms[pitRoomIndex];
    }

    //////////////////////
    // Pits constructor //
    //////////////////////
    private void Pits(){

    }
}

















