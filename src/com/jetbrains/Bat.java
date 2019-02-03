package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Game.cave;
import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.useDefaults;

public class Bat {
    //
    // Bat  constants
    //
    private final int BAT_1 = 0;
    private final int BAT_2 = 1;

    //
    // Bat instance variables
    //
    int roomNumber;
    int number;

    // UNDONE - make the number of bats variable
    //          but for now create 2

    //
    // Bat constructor
    //
    Bat(int batNumber){
        int batIndex = batNumber - 1;
        if(useDefaults){
            if(batNumber == 1) {
                Game.map.batRooms[batIndex] = 26;
            }
            else{
                // use default for bat 2
                Game.map.batRooms[batIndex] = 16;
            }
        } else {
            setBatRoomNumber(batIndex, cave.initialRoom, batRandom);
        }

        number = batNumber;
    }

    //
    // Bat local variables
    //
    // need to maintain a unique Random generator
    // so that creating new bats uses the existing random number sequence
    private static Random batRandom = new Random();

    //
    // Bat helper functions
    //
    private void setBatRoomNumber(int batRoomIndex, int initialRoom, Random random){
        // generate a different random room from 1 to 30
        int batRoomNumber = random.nextInt(29) + 1;

        boolean generateAnotherBatRoomNumber;
        do {
            // assume the current bat room number is OK
            generateAnotherBatRoomNumber = false;

            if (batRoomNumber == initialRoom) {
                // don't put a bat in the initial room
                generateAnotherBatRoomNumber = true;
            }

            if (Cave.rooms[batRoomNumber].hasPit) {
                // don't put a bat in a room with a pit
                generateAnotherBatRoomNumber = true;
            }

            if (batRoomNumber == Cave.wumpus.roomNumber) {
                // don't put a bat in a room with the Wumpus
                generateAnotherBatRoomNumber = true;
            }

            if(generateAnotherBatRoomNumber) {
                // generate another bat room number to test
                batRoomNumber = random.nextInt(29) + 1;
            }
            }while(generateAnotherBatRoomNumber);

        Game.map.batRooms[batRoomIndex] = batRoomNumber;
    }
}
