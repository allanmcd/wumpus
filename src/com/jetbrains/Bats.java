package com.jetbrains;

public class Bats {
    //
    // Bats Instance variables
    //

    // UNDONE - make the number of bats variable
    //          but for now create 2
    Bat[] bats = new Bat[2];

    //
    // Bats methods
    //
    boolean inRoom(int caveRoomNumber){
        boolean batInRoom = false;
        for(int batIndex = 0; batIndex < bats.length; batIndex++){
            if(bats[batIndex].roomNumber == caveRoomNumber){
                batInRoom = true;
                break;
            }
        }
        return batInRoom;
    }

    //
    // Bats constructor
    //
    Bats(){
        // create some bats
        for(int batIndex = 0; batIndex < Game.maxBats; batIndex++) {
            bats[batIndex] = new Bat(batIndex);
        }
    }
}
