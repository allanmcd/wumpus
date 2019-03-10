package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Game.cave;

public class Bats {
    /////////////////////////////
    // Bats Instance variables //
    /////////////////////////////

    // UNDONE - make the number of bats variable
    //          but for now create 2
    int initialNumberOfBats = 2;
    Bat[] bats = new Bat[2];

    // used to keep track of where the bats are currently located
    int batRooms[] = new int[initialNumberOfBats];

    int numberOfBatsKilled;

    //////////////////
    // Bats methods //
    //////////////////
    void addBats(int numberOfBats){
        // create some bats
        for(int batIndex = 0; batIndex < Game.maxBats; batIndex++) {
            bats[batIndex] = new Bat(batIndex);
            System.out.println("putting bat in room " + bats[batIndex].roomNumber);
        }
    }

    boolean inAdjacentRoom(){
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

    boolean isInRoom(int caveRoomNumber){
        boolean batInRoom = false;
        for(int batIndex = 0; batIndex < bats.length; batIndex++){
            if(bats[batIndex].isDead == false) {
                if (bats[batIndex].roomNumber == caveRoomNumber) {
                    batInRoom = true;
                    break;
                }
            }
        }
        return batInRoom;
    }

    int roomWithBatInIt(){
        int roomNumberWithBatInIt = 0;
        Random rnd = new Random();
        int whichBat = rnd.nextInt(initialNumberOfBats);
        boolean keepOnSearching = true;
        while(keepOnSearching) {
            // search for all possible bats
            for(int batRoomNumber = 0; batRoomNumber < initialNumberOfBats; batRoomNumber++) {
                for (int roomNumber = 0; roomNumber < Cave.numberOfRooms; roomNumber++) {
                    if (Cave.rooms[roomNumber].hasBat()) {
                        if (roomNumber == Cave.bats.batRooms[whichBat]) {
                            // found the room that contains the desired bat
                            keepOnSearching = false;
                            batRoomNumber = initialNumberOfBats;  //abort outer for stmt
                            roomNumberWithBatInIt = roomNumber;
                            break;
                        }
                    }
                }
            }
            // we've already searched for all possible bats so give up
            keepOnSearching = false;
        }
        return roomNumberWithBatInIt;
    }

    void makeDead(int caveRoomNumber){
        for(int batIndex = 0; batIndex < bats.length; batIndex++){
            if(bats[batIndex].roomNumber == caveRoomNumber) {
                bats[batIndex].isDead = true;
                numberOfBatsKilled++;
                Game.stats.update();
            }
        }
    }

}
