package com.jetbrains;

import java.util.Random;
import java.util.Stack;

import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.useDefaults;

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

    static void flee() {
        /*
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
*/
        // build an list of rooms to try at incrementing distances from the Wumpus
        Stack[] roomStacks = new Stack[4];

        // distance to move needs to be 2 to 4 rooms
        Random rnd = new Random();
        int howFarOut = 0;
        while (howFarOut < 2){
            howFarOut = rnd.nextInt(3) + 1;
        }
        System.out.println("need to move Wumpus " + howFarOut + " rooms away");
        int distanceFromWumpus = 0;
        addAdjacentRooms(roomStacks, distanceFromWumpus, howFarOut);

        // find an unoccupied room the appropriate distance from the current Wumpus location
        boolean acceptableRoomFound = false;
        while(acceptableRoomFound == false){

        }

//        System.out.println("Wumpus moved to room " + roomNumber );
    }

    static void addAdjacentRooms(Stack[] roomStacks, int distanceFromWumpus, int howFarOut) {
        System.out.println(" addAdjacentRooms called");
        Stack nextRoomStack = new Stack();
        if(distanceFromWumpus == 0) {
            Room wumpusRoom = Cave.rooms[Wumpus.roomNumber];
            wumpusRoom.distaceFromWumpus = 0;
            // create an initial list of rooms surrounding the Wumpus
            for (int wallIndex = 0; wallIndex < 6; wallIndex++) {
                Wall nextWall = Cave.rooms[Wumpus.roomNumber].walls[wallIndex];
                if (nextWall.hasTunne1) {
                    // this wall has a tunnel - need to add it to the initial list of rooms to check
                    nextRoomStack.add(nextWall.adjacentRoom);
                }
            }
            roomStacks[0] = nextRoomStack;
        } else {
            nextRoomStack = roomStacks[distanceFromWumpus];
        }
        // process all the rooms at this stack level, building a stack one room further from Wumpus
        distanceFromWumpus += 1;
        roomStacks[distanceFromWumpus] = new Stack();
        for(int stackIndex = 0; stackIndex < nextRoomStack.size(); stackIndex++) {
            int roomNumber = (int) nextRoomStack.get(stackIndex);
            Room currentRoom = Cave.rooms[roomNumber];
            currentRoom.distaceFromWumpus = distanceFromWumpus;
            // look for a tunnel in all 6 walls of the current room
            for (int wallIndex = 0; wallIndex < 6; wallIndex++) {
                Wall nextWall = currentRoom.walls[wallIndex];
                if (nextWall.hasTunne1) {
                    // this wall has a tunnel - does it lead back towards the Wumpus
                    Room nextAdjacentRoom = Cave.rooms[nextWall.adjacentRoom];
                    if(nextAdjacentRoom.distaceFromWumpus != 0) {
                        // does not point back to Wumpus room
                        if (nextAdjacentRoom.distaceFromWumpus == -1) {
                            // hasn't been "checked" so it's free to use
                            //leads away from Wumpus - need to add it to the list of rooms to check
                            roomStacks[distanceFromWumpus].add(nextWall.adjacentRoom);
                        }
                    }
                }
            }
        }

        if(distanceFromWumpus < howFarOut){
            addAdjacentRooms(roomStacks, distanceFromWumpus, howFarOut);
        }
        System.out.println("addAdjacentRooms exiting");
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
