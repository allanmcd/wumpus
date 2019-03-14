package com.jetbrains;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static com.jetbrains.Cave.rooms;
import static com.jetbrains.Game.cave;
import static com.jetbrains.Game.stats;
import static com.jetbrains.Main.useDefaults;
import static com.jetbrains.WumpusEquates.*;

//
// NOTE there is only one Wumpus object
//
public final class Wumpus {
    ///////////////////////////////
    // Wumpus instance variables //
    //////////////////////////////

    static int roomNumber;
    static boolean isDead;
    static Random random = new Random();

    ////////////////////
    // Wumpus methods //
    ///////////////////

    static void addAdjacentRooms(ArrayList<Stack> roomNumberStacks, int distanceFromWumpus) {
        Stack innerRoomNumbersStack = roomNumberStacks.get(distanceFromWumpus -1);
        Stack outerRoomNumbersStack = new Stack();

        // process all the rooms at this stack level, building a stack one room further from Wumpus
        for(int stackIndex = 0; stackIndex < innerRoomNumbersStack.size(); stackIndex++) {
            int innerRoomNumber = (int) innerRoomNumbersStack.get(stackIndex);
            Room innerRoom = Cave.rooms[innerRoomNumber];
//            innerRoom.distaceFromWumpus = distanceFromWumpus;
            // look for a tunnel in all 6 walls of the current room
            for (int wallIndex = 0; wallIndex < 6; wallIndex++) {
                Wall nextWall = innerRoom.walls[wallIndex];
                if (nextWall.hasTunne1) {
                    // this wall has a tunnel - does it lead back towards the Wumpus
                    Room nextAdjacentOuterRoom = Cave.rooms[nextWall.adjacentRoom];
                    if(nextAdjacentOuterRoom.distaceFromWumpus != 0) {
                        // does not go back to Wumpus room
                        if (nextAdjacentOuterRoom.distaceFromWumpus == -1) {
                            // hasn't been "checked" so it's free to use
                            //leads away from Wumpus - need to add it to the list of rooms to check
                            outerRoomNumbersStack.add(nextWall.adjacentRoom);
                        }
                    }
                }
            }
        }
        roomNumberStacks.add(outerRoomNumbersStack);
    }

    static void computeShortestPath(){
        Stack shortestPathRoomStack = new Stack();
        Room currentRoom = rooms[Player.roomNumber];
        shortestPathRoomStack.push(currentRoom);
        Room closestRoom = currentRoom;
        int shortestDistance = currentRoom.distaceFromWumpus;
        int nextRoomNumber = currentRoom.roomNumber;
        // create a stack with all the rooms on the shortest path in it
        while(nextRoomNumber != Wumpus.roomNumber){
            currentRoom = rooms[nextRoomNumber];
            Wall[] walls = currentRoom.walls;
            // look for walls with tunnels
            for(int wallNumber = 0; wallNumber < 6; wallNumber++){
                Wall wall = walls[wallNumber];
                if(wall.hasTunne1){
                    // check out the adjacent room
                    Room adjacentRoom = rooms[wall.adjacentRoom];
                    if(adjacentRoom.distaceFromWumpus < shortestDistance){
                        shortestDistance = adjacentRoom.distaceFromWumpus;
                        closestRoom = adjacentRoom;
                        currentRoom.wallWithTunnelClosestToWumpus = new Wall();
                        currentRoom.wallWithTunnelClosestToWumpus = wall;
                        for(int adjacentRoomWallNumber = 0; adjacentRoomWallNumber < 6; adjacentRoomWallNumber++){
                            Wall nextWall = adjacentRoom.walls[adjacentRoomWallNumber];
                            if(nextWall.hasTunne1){
                                int tunnelToRoomNumber = nextWall.adjacentRoom;
                                if(tunnelToRoomNumber == currentRoom.roomNumber){
                                    // we found the tunnel pointing back from adjecent room back to current room
                                    adjacentRoom.wallWithTunnelClosestToPlayer = new Wall();
                                    adjacentRoom.wallWithTunnelClosestToPlayer = nextWall;
                                }
                            }
                        }
                    }
                }
            }
            nextRoomNumber = closestRoom.roomNumber;
            shortestPathRoomStack.push(closestRoom);
        }
    }

    static void drawShortestPathInRoom(RoomView roomView){
        if(roomView.scaleFactor == 1.0){
            // don't draw shortest path in full size rooom
            return;
        }

        Room room = Cave.rooms[roomView.currentRoomNumber];
        if(room.wallWithTunnelClosestToWumpus != null);{
            // this room lies on the shortest path to the Wumpus

            Point topLeft[] = roomView.topLefts;
            double roomTopX = topLeft[0].x;
            double roomTopY = topLeft[0].y;

            double deltaX1 = roomView.wallDeltas[OUTER_WALL][X1];
            double deltaX2 = roomView.wallDeltas[OUTER_WALL][X2];
            double lineStartX = roomTopX + deltaX1 + deltaX2/2;

            double deltaY = roomView.wallDeltas[OUTER_WALL][Y1];
            double lineStartY = roomTopY + deltaY;

            Wall closestToWumpusWall = room.wallWithTunnelClosestToWumpus;
            Object tunnelPoints[] = closestToWumpusWall.tunnel;

            double tunnelOuterX1 = ((Point)tunnelPoints[0]).x;
            double tunnelOuterX2 = ((Point)tunnelPoints[1]).x;
            double lineEndX = tunnelOuterX1 + (tunnelOuterX2 - tunnelOuterX1)/2;

            double tunnelOuterY1 = ((Point)tunnelPoints[0]).y;
            double tunnelOuterY2 = ((Point)tunnelPoints[1]).y;
            double lineEndY = tunnelOuterY1 + (tunnelOuterY2 - tunnelOuterY1)/2;

            Line line = new Line();
            line.setStartX(lineStartX);
            line.setStartY(lineStartY);
            line.setEndX(lineEndX);
            line.setEndY(lineEndY);
            line.setStroke(Color.GREEN);
            line.setStrokeWidth(3);
            roomView.group.getChildren().add(line);

        }
    }

    static void flee() {

        //need to reset all room.distanceFromWumpus
        updateDistanceFrom();

        // build an list of rooms at incrementing distances from the Wumpus
        // distance to move needs to be 2 to 4 rooms
        int howFarOut = 0;
        if(useDefaults){
            howFarOut = 3;
        } else {
            Random rnd = new Random();
            while (howFarOut < 2) {
                howFarOut = rnd.nextInt(3) + 1;
            }
        }
        System.out.println("need to move Wumpus " + howFarOut + " rooms away");
        Stack wumpusRoomStack = new Stack();
        wumpusRoomStack.add(Wumpus.roomNumber);
        ArrayList<Stack> roomNumberStacks = new ArrayList<Stack>();
        roomNumberStacks.add(wumpusRoomStack);

        int distanceFromWumpus = 1;
        while(distanceFromWumpus <= howFarOut){
            addAdjacentRooms(roomNumberStacks, distanceFromWumpus);
            distanceFromWumpus++;
        }

        // find an unoccupied room the appropriate distance from the current Wumpus location
        Stack newWumpusRoomNumbersStack = new Stack();
        newWumpusRoomNumbersStack = roomNumberStacks.get(howFarOut);
        int potentialWumpusRoomNumber = 0;
        while(newWumpusRoomNumbersStack.size() > 0 && potentialWumpusRoomNumber == 0) {
            potentialWumpusRoomNumber = (int)newWumpusRoomNumbersStack.pop();
            System.out.println("Wumpus.flee() checking rooom " + potentialWumpusRoomNumber);
            if (Cave.rooms[potentialWumpusRoomNumber].hasBat()) {
                // don't put the Wumpus in a room with a bat
                potentialWumpusRoomNumber = 0;
            }
        }

        // did we find a suitable room to relocat the Wumpus to?
        if(potentialWumpusRoomNumber == 0) {
            //Holy batshit - how could this happen
            // must have encountered a dead end room in the search path
            System.out.println("Wumpus.flee couldnot find a room " + howFarOut + " rooms away");
            Wumpus.moveToRandomRoom();
            System.out.println("Moved Wumpus to random room " + Wumpus.roomNumber);
        } else{
            Wumpus.roomNumber = potentialWumpusRoomNumber;
            System.out.println("Wumpus moved to room " + potentialWumpusRoomNumber);
        }
        updateDistanceFrom();
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

    static int markAdjacentRooms(ArrayList<Stack> roomNumberStacks, int distanceFromWumpus) {
        Stack innerRoomNumbersStack = roomNumberStacks.get(distanceFromWumpus -1);
        Stack outerRoomNumbersStack = new Stack();
        int roomsMarked = 0;

        // process all the rooms at this stack level, building a stack one room further from Wumpus
        for(int stackIndex = 0; stackIndex < innerRoomNumbersStack.size(); stackIndex++) {
            int innerRoomNumber = (int) innerRoomNumbersStack.get(stackIndex);
            Room innerRoom = Cave.rooms[innerRoomNumber];
            innerRoom.distaceFromWumpus = distanceFromWumpus -1;
            // look for a tunnel in all 6 walls of the current room
            for (int wallIndex = 0; wallIndex < 6; wallIndex++) {
                Wall nextWall = innerRoom.walls[wallIndex];
                if (nextWall.hasTunne1) {
                    // this wall has a tunnel - does it lead back towards the Wumpus
                    Room nextAdjacentOuterRoom = Cave.rooms[nextWall.adjacentRoom];
                    if(nextAdjacentOuterRoom.distaceFromWumpus != 0) {
                        // does not go back to Wumpus room
                        if (nextAdjacentOuterRoom.distaceFromWumpus == -1) {
                            // hasn't been "checked" so it's free to use
                            //leads away from Wumpus - need to add it to the list of rooms to check
                            outerRoomNumbersStack.add(nextWall.adjacentRoom);
                            roomsMarked++;
                        }
                    }
                }
            }
        }
        if(roomsMarked > 0) {
            roomNumberStacks.add(outerRoomNumbersStack);
        }

        // this will return 0 when all the rooms have been marked
        return roomsMarked;
    }

    static void moveToRandomRoom(){
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

            if (Cave.rooms[newWumpusRoomNumber].hasBat()) {
                // don't put the Wumpus in a room with a bat
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

        roomNumber = newWumpusRoomNumber;
        System.out.println("Wumpus moved to room " + roomNumber );
    }

    static void moveToRoom(int rooomToMoveTo){
        roomNumber = roomNumber;
    }

    static void updateDistanceFrom(){
        //need to reset all room.distanceFromWumpus to -1;
        for(int roomNumber = 0; roomNumber < 31; roomNumber++)
        {
            Cave.rooms[roomNumber].distaceFromWumpus = -1;
            rooms[roomNumber].wallWithTunnelClosestToWumpus = null;
            rooms[roomNumber].wallWithTunnelClosestToPlayer = null;
        }
        Cave.rooms[Wumpus.roomNumber].distaceFromWumpus = 0;

        // build an list of rooms at incrementing distances from the Wumpus
        Stack wumpusRoomStack = new Stack();
        wumpusRoomStack.add(Wumpus.roomNumber);
        ArrayList<Stack> roomNumberStacks = new ArrayList<Stack>();
        roomNumberStacks.add(wumpusRoomStack);

        int distanceFromWumpus = 1;
        int roomsRemaining = 30;
        int roomsMarked = 0;
        do{
            roomsMarked = markAdjacentRooms(roomNumberStacks, distanceFromWumpus);
            distanceFromWumpus++;
        }while(roomsMarked > 0);

        updateDistanceFromText();
    }

    static void updateDistanceFromText() {
        int distanceFromWumpus = rooms[Player.roomNumber].distaceFromWumpus;
        String roomsAway = " rooms away";
        if (distanceFromWumpus == 1) {
            roomsAway = " room away";
        }
        stats.txtWumpus.setText("The Wumpus is " + distanceFromWumpus + roomsAway);
    }

    ////////////////////////
    // Wumpus constructor //
    ////////////////////////

    private Wumpus(int initialRoom){
    }
}
