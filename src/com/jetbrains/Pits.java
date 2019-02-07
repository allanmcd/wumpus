package com.jetbrains;

import com.jetbrains.Room;
import com.jetbrains.Wall;

import java.util.Random;

import static com.jetbrains.Cave.initialRoom;
import static com.jetbrains.Cave.rooms;
import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.game;
import static com.jetbrains.Main.useDefaults;

public class Pits {
    //
    // Pit instance variables
    //
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
        Room playerRoom = rooms[game.player.roomNumber];
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
            for (int pitNumber = 1; pitNumber < 3; pitNumber++) {
                // generate a random room from 1 to 30
                int pitRoomNumber = random.nextInt(29) + 1;
                if (pitRoomNumber == initialRoom) {
                    // don't put a pit in the initial room
                    pitRoomNumber = random.nextInt(29) + 1;
                }
                rooms[pitRoomNumber].hasPit = true;
            }
        }
    }
}

















