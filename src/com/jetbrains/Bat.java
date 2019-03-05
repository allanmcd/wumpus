package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Game.cave;
import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.useDefaults;

public class Bat {
    ////////////////////
    // Bat  constants //
    ////////////////////
    private final int BAT_1 = 0;
    private final int BAT_2 = 1;

    ////////////////////////////
    // Bat instance variables //
    ////////////////////////////

    int roomNumber;
    int number;
    boolean isDead;

    /////////////////////////
    // Bat private variables //
    /////////////////////////

    // need to maintain a unique Random generator
    // so that creating new bats uses the existing random number sequence
    private static Random batRandom = new Random();

    /////////////////
    // Bat methods //
    /////////////////

    void relocateBatFrom(int currentRoomNumber){
        for(int i = 0; i < cave.bats.batRooms.length; i++){
            if(cave.bats.batRooms[i] == currentRoomNumber){
                setBatRoomNumber(i,currentRoomNumber,batRandom);
            }
        }
    }

    /////////////////////
    // Bat constructor //
    /////////////////////

    Bat(int batIndex){
        if(useDefaults){
            if(batIndex == 0) {
                // set default value for bat 1
                cave.bats.batRooms[batIndex] = 26;
                roomNumber = 26;
            }
            else{
                // use default for bat 2
                cave.bats.batRooms[batIndex] = 16;
                roomNumber = 16;
            }
        } else {
            setBatRoomNumber(batIndex, cave.initialRoom, batRandom);
        }
        number = batIndex + 1;
    }

    //////////////////////////
    // Bat helper functions //
    //////////////////////////

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

            for(int batIndex = 0; batIndex < Game.maxBats; batIndex++){
                if(cave.bats.batRooms[batIndex] == batRoomNumber){
                    // this room already has a bat in it - keep looking
                    generateAnotherBatRoomNumber = true;
                }
            }

            if(generateAnotherBatRoomNumber) {
                // generate another bat room number to test
                batRoomNumber = random.nextInt(29) + 1;
            }
            }while(generateAnotherBatRoomNumber);

        cave.bats.batRooms[batRoomIndex] = batRoomNumber;
        roomNumber = batRoomNumber;
        System.out.println("bat assigned to " + batRoomNumber );
    }
}
