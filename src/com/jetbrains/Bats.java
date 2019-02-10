package com.jetbrains;

import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.game;

public class Bats {
    //
    // Bats Instance variables
    //

    // UNDONE - make the number of bats variable
    //          but for now create 2
    Bat[] bats = new Bat[2];

    // used to keep track of where the bats are currently located
    int batRooms[] = new int[2];

    int numberKilled;
    //
    // Bats methods
    //
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

    void addBats(int numberOfBats){
        // create some bats
        for(int batIndex = 0; batIndex < Game.maxBats; batIndex++) {
            bats[batIndex] = new Bat(batIndex);
        }
    }

    void makeDead(int caveRoomNumber){
        for(int batIndex = 0; batIndex < bats.length; batIndex++){
            if(bats[batIndex].roomNumber == caveRoomNumber) {
                bats[batIndex].isDead = true;
                numberKilled++;
                game.stats.update();
            }
        }
    }

}
