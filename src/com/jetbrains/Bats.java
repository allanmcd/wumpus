package com.jetbrains;

import java.util.Random;

import static com.jetbrains.Game.cave;

public class Bats {
    //
    // Bats Instance variables
    //

    // UNDONE - make the number of bats variable
    //          but for now create 2
    int initialNumberOfBats = 2;
    Bat[] bats = new Bat[2];

    // used to keep track of where the bats are currently located
    int batRooms[] = new int[initialNumberOfBats];

    int numberOfBatsKilled;
    //
    // Bats methods
    //
    void addBats(int numberOfBats){
        // create some bats
        for(int batIndex = 0; batIndex < Game.maxBats; batIndex++) {
            bats[batIndex] = new Bat(batIndex);
        }
    }

    boolean inAdjacentRoom(){
        boolean inAdjacentRoom = false;
        Room playerRoom = cave.rooms[Game.player.roomNumber];
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
        Random rnd = new Random();
        int batRoomIndex = rnd.nextInt(initialNumberOfBats);
        for(int i = 0; i < initialNumberOfBats; i++) {
            if (batRooms[batRoomIndex] == 0) {
                // no bat in this room - try another room
                batRoomIndex = rnd.nextInt(initialNumberOfBats);
            }
        }
        if(batRooms[batRoomIndex] == 0){
            return 0;
        }
        return batRoomIndex;
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
