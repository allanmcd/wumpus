package com.jetbrains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;

import static com.jetbrains.Game.cave;

//
// NOTE there should only be one Cave object
//
class Cave {
    //
    // Cave static variables
    //
    static Bats bats;
    static int currentRoom;
    static int initialRoom;
    static String name;
    static Pits pits;
    static int numberOfRooms = 30;
    static Room rooms[] = new Room[numberOfRooms];
    static boolean valid;
    static ArrayList highScores = new ArrayList();

    //
    // Cave constructor
    //
    //
    Cave(String caveName, int initialRoom) {
        name = caveName;
        this.initialRoom = initialRoom;
        rooms = newCaveRooms(30);
    }

    //
    // Cave local variables
    //
    static private Room caveRoom;

    //
    // Cave helper functions
    // need to be static because they are called from within static contexts
    //
    static boolean loadCave(String caveName) {
        BufferedReader br;
        try {
            // cave CSV format in BNF notation is:
            //      <roomNumber> <1>#<3>{<comma><tunnelRoom>}
            // multiple lines are used to define the rooms
            // we currently don't test for duplicate room definitions
            br = new BufferedReader(new FileReader("src/" + caveName + ".cave"));
            String line;

            // process all the lines from the cave file
            while ((line = br.readLine()) != null) {
                String[] args = line.split(",");

                // what room are these parameters for
                Debug.log("room number = " + args[0]);
                int roomNumber = Integer.parseInt(args[0].trim());

                caveRoom = rooms[roomNumber];
                caveRoom.roomNumber = roomNumber;

                // process all the parameters for the current line of the game file
                for (int argsIndex = 1; argsIndex < args.length; argsIndex++) {
                    String nextArg = args[argsIndex].trim();
                    // assume it is a room number to tunnel to
                    int numberOfRoomToTunnelTo = Integer.parseInt(args[argsIndex].trim());
                    caveRoom.addTunnel(numberOfRoomToTunnelTo);

                    // also add the tunnel to the room that was tunneled to
                    Room roomToTunnelTo = rooms[numberOfRoomToTunnelTo];
                    roomToTunnelTo.addTunnel(caveRoom.roomNumber);
                }
            }
            // initialize the wall and tunnel points for all the rooms
            for (int roomNumber = 1; roomNumber < 31; roomNumber++) {
                Room room = rooms[roomNumber];
                GIO.singleRoomView.initWallPoints(GIO.singleRoomView);
            }

            // create a couple of bats
            bats = new Bats();
            bats.addBats(2);

            // create a couple of pits
            Pits.addPits(2);

            Debug.log("");

        } catch (IOException e) {
            e.printStackTrace();
        }
        valid = verifyCave(caveName);

        if (valid) {
            System.out.println(caveName + " loaded");
        } else {
            System.out.println(caveName + " NOT loaded");
        }

        return valid;
    }

    static private boolean verifyCave(String caveName) {
        // assume the cave configuration is valid for now
        boolean valid = true;

        Stack roomStack = new Stack();
        // preload the stack with the first room
        int roomNumber = 1;
        roomStack.push(roomNumber);

        while (roomStack.empty() == false) {
            roomNumber = (int) roomStack.pop();
            Room thisRoom = rooms[roomNumber];
            thisRoom.hasBeenVisited = true;
            // check each wall to see if it has a tunnel
            for (int wallNumber = 0; wallNumber <= 5; wallNumber++) {
                Wall thisWall = thisRoom.walls[wallNumber];
                if (thisWall.hasTunne1) {
                    // see if the adjacent room has been visited
                    int adjacentRoomNumber = thisWall.adjacentRoom;
                    Room adjacentRoom = rooms[adjacentRoomNumber];
                    if (adjacentRoom.hasBeenVisited == false) {
                        // one of the current rooms adjacent Rooms has not been visited
                        // so add it to the room Stack
                        roomStack.push(adjacentRoomNumber);
                    }
                }
            }
        }


        // make sure each room in the cave has been visited
        for (roomNumber = 1; roomNumber <= 30; roomNumber++) {
            if (rooms[roomNumber].hasBeenVisited == false) {
                // this room has NOT been visited
                Debug.error("room " + roomNumber + " is not connected to any other rooms");
                valid = false;
                break;
            }
        }

        // make sure each of the rooms has no more than 3 tunnels
        if (valid) {
            valid = verifyNumberOfTunnels();
        }

        // make sure the cave does NOt have more than two pits or bats
        if (valid) {
            valid = verifyNumberOfPitsAndBats();
        }
        return valid;
    }

    static private boolean verifyNumberOfTunnels() {
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
                Debug.error("room " + roomNumber + " had " + numberOfTunnels + " tunnels - 3 is the maximum allowed");
                return false;
            }
        }
        return true;
    }

    static private boolean verifyNumberOfPitsAndBats() {
        int numberOfPits = 0;
        int numberOfBats = 0;

        for (int roomNumber = 1; roomNumber <= 30; roomNumber++) {
            Room room = rooms[roomNumber];
            if (room.hasPit) {
                numberOfPits++;
            }
            if (cave.bats.isInRoom(room.roomNumber)) {
                numberOfBats++;
            }
        }

        // make sure this room has exactly 2 pits
        if (numberOfPits != 2) {
            Debug.error("cave " + name + " has " + numberOfPits + " pits - it should have 2");
            return false;
        }

        // make sure this room has exactly 2 bats
        if (numberOfBats != Game.maxBats) {
            Debug.warning("cave " + name + " has " + numberOfBats + " bats - it should have " + Game.maxBats);
            return false;
        }

        return true;
    }

    static Room[] newCaveRooms(int numberOfRooms) {
        Room caveRooms[] = {
                // create a dummy room to allow the remaining rooms to be 1 based indexed
                new Room(0, 0, 0, 0, 0, 0, 0),
                // create the 30 rooms
                new Room(1, 25, 26, 2, 7, 6, 30),
                new Room(2, 26, 3, 9, 8, 7, 1),
                new Room(3, 27, 28, 4, 9, 2, 26),
                new Room(4, 28, 5, 11, 10, 9, 3),
                new Room(5, 29, 30, 6, 11, 4, 28),
                new Room(6, 30, 1, 7, 12, 11, 5),
                new Room(7, 1, 2, 8, 13, 12, 6),
                new Room(8, 2, 9, 15, 14, 13, 7),
                new Room(9, 3, 4, 10, 15, 8, 2),
                new Room(10, 4, 11, 17, 16, 15, 9),
                new Room(11, 5, 6, 12, 17, 10, 4),
                new Room(12, 6, 7, 13, 18, 17, 11),
                new Room(13, 7, 8, 14, 19, 18, 12),
                new Room(14, 8, 15, 21, 20, 19, 13),
                new Room(15, 9, 10, 16, 21, 14, 8),
                new Room(16, 10, 17, 23, 22, 21, 15),
                new Room(17, 11, 12, 18, 23, 16, 10),
                new Room(18, 12, 13, 19, 24, 23, 17),
                new Room(19, 13, 14, 20, 25, 24, 18),
                new Room(20, 14, 21, 27, 26, 25, 19),
                new Room(21, 15, 16, 22, 27, 20, 14),
                new Room(22, 16, 23, 29, 28, 27, 21),
                new Room(23, 17, 18, 24, 29, 22, 16),
                new Room(24, 18, 19, 25, 30, 29, 23),
                new Room(25, 19, 20, 26, 1, 30, 24),
                new Room(26, 20, 27, 3, 2, 1, 25),
                new Room(27, 21, 22, 28, 3, 26, 20),
                new Room(28, 22, 29, 5, 4, 3, 27),
                new Room(29, 23, 24, 30, 5, 28, 22),
                new Room(30, 24, 25, 1, 6, 5, 29),
        };
        return caveRooms;
    }
}
