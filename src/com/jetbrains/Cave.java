package com.jetbrains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.stage.Stage;


public class Cave {
    public Cave(int caveNumber){
        loadCave(caveNumber);
    }

    Stage fooStage = new Stage();
    public int number;

    public boolean valid = false;

    private void loadCave(int caveNumber){
        number = caveNumber;
        BufferedReader br = null;
        try {
            // cave CSV format is:
            //      roomNumber [,tunnelRoom1] [,tunnelRoom2] [,tunnelRoom3] [,pit] [,bat]
            // multiple lines are used to define the rooms
            // we currently don't test for duplicate room definitions
            br = new BufferedReader(new FileReader("src/cave" + caveNumber + ".csv"));
            String line;

            // process all the lines from the cave file
            while ((line = br.readLine()) != null) {
                String[] args = line.split(",");

                // what room are these parameters for
                Debug.log("room number = " + args[0]);
                int roomNumber = Integer.parseInt(args[0].trim());

                Room caveRoom = rooms[roomNumber];
                caveRoom.roomNumber = roomNumber;

                // process all the parameters for the current line of the game file
                for(int argsIndex = 1; argsIndex < args.length; argsIndex++) {
                    String nextArg = args[argsIndex].trim();
                    switch(nextArg){
                        case "bat":
                            Debug.log("room " + roomNumber + " has a bat");
                            caveRoom.hasBat = true;
                            break;
                        case "pit":
                            Debug.log("room " + roomNumber + " has a pit");
                            caveRoom.hasPit = true;
                            break;
                        default:
                            // assume it is a room number to tunnel to
                            int numberOfRoomToTunnelTo = Integer.parseInt(args[argsIndex].trim());
                            addTunnel(caveRoom, numberOfRoomToTunnelTo);

                            // also add the tunnel to the room that was tunneled to
                            Room roomToTunnelTo = rooms[numberOfRoomToTunnelTo];
                            addTunnel(roomToTunnelTo, caveRoom.roomNumber);
                    }
                }
                Debug.log("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        verifyCave(caveNumber);
    }

    // helper functions

    private void addTunnel(Room caveRoom, int roomToTunnelTo) {
        // scan all the walls looking for the one that is adjacent to the roomToTunnelTo
        for (int wallNumberIndex = 0; wallNumberIndex < 6; wallNumberIndex++) {
            Wall caveRoomWall = caveRoom.walls[wallNumberIndex];
            if (caveRoomWall.adjacentRoom == roomToTunnelTo) {
                // we found the wall to assign the tunnel to
                caveRoomWall.hasTunne1 = true;
                Debug.log("added tunnel from room " + caveRoom.roomNumber + " to room " + roomToTunnelTo );
                break;
            }
        }
    }

    private void verifyCave(int gameNumber){
        int currentCaveNumber;
        int nextCaveNumber;

        // assume the cave configuration is valid for now
        valid = true;

        // verify that each room can be visited
        // first we mark each room that is the destination of a tunnel
        for (int roomNumber = 1; roomNumber <= 30; roomNumber++) {
            // examine all the walls looking for tunnels
            for (int wallNumber = 0; wallNumber <= 5; wallNumber++) {
                Room thisRoom = rooms[roomNumber];
                Wall thisWall = thisRoom.walls[wallNumber];
                if (thisWall.hasTunne1) {
                    // mark this room as having been visited
                    int adjacentRoomNumber = thisWall.adjacentRoom;
                    Room adjacentRoom = rooms[adjacentRoomNumber];
                    adjacentRoom.hasBeenVisited = true;
                }
            }
        }

        // make sure each room in the cave has been visited
        for (int roomNumber = 1; roomNumber <= 30; roomNumber++) {
            if (rooms[roomNumber].hasBeenVisited == false) {
                // this room has NOT been visited
                Debug.log("room " + roomNumber + " is not connected to any other rooms");
                valid = false;
                break;
            }
        }

        // make sure each of the rooms has no more than 3 tunnels
        if(valid) {
            valid = verifyNumberOfTunnels();
        }

        // make sure the cave does NOt have more than two pits or bats
        if(valid) {
            valid = verifyNumberOfPitsAndBats();
        }

        Debug.log("game number " + gameNumber + " is " + (valid ? "" : "NOT") + "valid");
        return;
    }

    private boolean verifyNumberOfTunnels(){
        // scan through all the rooms in the cave
        for (int roomNumber = 1; roomNumber <= 30; roomNumber++) {
            int numberOfTunnels = 0;
            Room room = rooms[roomNumber];
            // check all the walls for tunnels
            for (int wallNumber = 0; wallNumber <= 5; wallNumber++) {
                Wall wall = room.walls[wallNumber];
                if (wall.hasTunne1) {
                    numberOfTunnels++;
                }
            }

            // did this room have more than 3 tunnels
            if (numberOfTunnels > 3) {
                Debug.log("room " + roomNumber + " had " + numberOfTunnels + " tunnels - 3 is the maximum allowed");
                return false;
            }
        }
        return true;
    }

    private boolean verifyNumberOfPitsAndBats() {
        int numberOfPits = 0;
        int numberOfBats = 0;

        for (int roomNumber = 1; roomNumber <= 30; roomNumber++) {
            Room room = rooms[roomNumber];
            if(room.hasPit){
                numberOfPits++;
            }
            if(room.hasBat){
                numberOfBats++;
            }
        }

        // make sure this room has exactly 2 pits
        if (numberOfPits != 2) {
            Debug.log("cave has " + numberOfPits + " pits - it should have 2");
            return false;
        }

        // make sure this room has exactly 2 bats
        if (numberOfBats != 2) {
            Debug.log("cave has " + numberOfBats + " bats - it should have 2");
            return false;
        }

        return true;
    }



    // cave rooms structure
    public Room rooms[] = {
            // create a dummy room to allow the remaining rooms to be base 1 indexed
            new Room(0,0,0,0,0,0,0),
            // create the 30 rooms
            new Room( 1, 25,26,2,7,6,30),
            new Room( 2, 26, 3, 9, 8, 7, 1),
            new Room( 3, 27,28,4,9,2,26),
            new Room( 4, 28, 5, 11, 10, 9, 3),
            new Room( 5, 29, 30, 6, 11, 4, 28),
            new Room( 6, 30, 1, 7, 12, 11, 5),
            new Room( 7, 1, 2, 8, 13, 12, 6),
            new Room( 8, 2, 9, 15, 14, 13, 7),
            new Room( 9, 3, 4, 10, 15, 8, 2),
            new Room( 10, 4, 11, 17, 16, 15, 9),
            new Room( 11, 5, 6, 12, 17, 10, 4),
            new Room( 12, 6, 7, 13, 18, 17, 11),
            new Room( 13, 7, 8, 14, 19, 18, 12),
            new Room( 14, 8, 15, 21, 20, 19, 13),
            new Room( 15, 9, 10, 16, 21, 14, 8),
            new Room( 16, 10, 17, 23, 22, 21, 15),
            new Room( 17, 11, 12, 18, 23, 16, 10),
            new Room( 18, 12, 13, 19, 24, 23, 17),
            new Room( 19, 13, 14, 20, 25, 24, 18),
            new Room( 20, 14, 21, 27, 26, 25, 19),
            new Room( 21, 15, 16, 22, 27, 20, 14),
            new Room( 22, 16, 23, 29, 28, 27, 21),
            new Room( 23, 17, 18, 24, 29, 22, 16),
            new Room( 24, 18, 19, 25, 30, 29, 23),
            new Room( 25, 19, 20, 26, 1, 30, 24),
            new Room( 26, 20, 27, 3, 2, 1, 25),
            new Room( 27, 21, 22, 28, 3, 26, 20),
            new Room( 28, 22, 29, 5, 4, 3, 27),
            new Room( 29, 23, 24, 30, 5, 28, 22),
            new Room( 30, 24, 25, 1, 6, 5, 29),
    };
}
