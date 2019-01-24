package com.jetbrains;

public class Room {
    public Wall walls[] = {new Wall(), new Wall(), new Wall(), new Wall(), new Wall(), new Wall()};
    public int roomNumber;
    public boolean hasPit;
    public boolean hasBat;
    public boolean hasBeenVisited;

    public Room (int newRoomNumber,
                 int adjacentRoom1,
                 int adjacentRoom2,
                 int adjacentRoom3,
                 int adjacentRoom4,
                 int adjacentRoom5,
                 int adjacentRoom6
                 )
    {
        roomNumber  = newRoomNumber;
        walls[0].adjacentRoom = adjacentRoom1;
        walls[1].adjacentRoom = adjacentRoom2;
        walls[2].adjacentRoom = adjacentRoom3;
        walls[3].adjacentRoom = adjacentRoom4;
        walls[4].adjacentRoom = adjacentRoom5;
        walls[5].adjacentRoom = adjacentRoom6;
    }
}
