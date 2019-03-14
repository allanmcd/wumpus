package com.jetbrains;

import javafx.scene.Group;
import javafx.scene.paint.Color;

import static com.jetbrains.WumpusEquates.*;
import static com.jetbrains.WumpusEquates.Y1;

public class RoomView {

    /////////////////////////////////
    // RoomView Instance variables //
    /////////////////////////////////

    Group group;
    int currentRoomNumber;
    Color floorColor;
    Boolean isBorderRoom;
    double pitImageOpacity;
    double  scaleFactor;
    boolean showRoomNumber;
    boolean showPlayer;
    Point topLefts[] = new Point[2];
    int wallDeltas[][] = new int[2][3];

    // hexagon[inner/outer][points][x/y];
    double[][][]hexagon = new double[2][7][2];

    //////////////////////
    // RoomView methods //
    //////////////////////

    void initWallPoints(RoomView roomView){
        initRoomSizeParameters(roomView);
        //
        // initialize the hexagon points and tunnel rectangle
        // NOTE: must be done AFTER the Room class has been instantiated
        //
        //initRoomHexagon(OUTER_WALL, hexagon,110, 190,170);
        //initRoomHexagon(INNER_WALL, hexagon,100, 180,160);
        initRoomHexagon(roomView, OUTER_WALL, hexagon);
        initRoomHexagon(roomView, INNER_WALL, hexagon);
    }

    ///////////////////////////
    // RoomView constructors //
    ///////////////////////////

    public RoomView(Group group, boolean showRoomNumber, double scaleFactor, Color floorColor, Point topLeft){
        this.group = group;
        this.scaleFactor = scaleFactor;
        this.showRoomNumber = showRoomNumber;
        this.floorColor = floorColor;
        topLefts[OUTER_WALL] = topLeft;

        // use these default values
        this.showPlayer = true;
        this.pitImageOpacity = 1.0;
    }

    // make a copy of an existing RoomView
    public RoomView(RoomView roomView){
        this.group = roomView.group;
        this.scaleFactor = roomView.scaleFactor;
        this.showRoomNumber = roomView.showRoomNumber;
        this.floorColor = roomView.floorColor;
        this.topLefts = roomView.topLefts;
        this.wallDeltas = roomView.wallDeltas;
    }

    ///////////////////////////////
    // RoomView helper functions //
    ///////////////////////////////

    private void initRoomHexagon(RoomView roomView, int whichWall, double[][][] walls){
        //
        // compute and store the 6 points that define the room hexagon
        //
        //        point0    point1
        //
        //
        // point5                  point2
        //
        //
        //        point4    point3
        //
        int top = (int)roomView.topLefts[whichWall].y;
        int left = (int)roomView.topLefts[whichWall].x;

        int deltaX1 = roomView.wallDeltas[whichWall][X1];
        int deltaX2 = roomView.wallDeltas[whichWall][X2];
        int deltaY = roomView.wallDeltas[whichWall][Y1];

        double wall[][] = walls[whichWall];
        double point0X = left + deltaX1;
        double point0Y = top;
        wall[POINT_0][X] = point0X;
        wall[POINT_0][Y] = point0Y;

        double point1X = point0X + deltaX2;
        double point1Y = point0Y;
        wall[POINT_1][X] = point1X;
        wall[POINT_1][Y] = point1Y;

        double point2X = point1X + deltaX1;
        double point2Y = top + deltaY;
        wall[POINT_2][X] = point2X;
        wall[POINT_2][Y] = point2Y;

        double point3Y = point2Y + deltaY;
        wall[POINT_3][X] = point1X;
        wall[POINT_3][Y] = point3Y;

        wall[POINT_4][X] = point0X;
        wall[POINT_4][Y] = point3Y;

        wall[POINT_5][X] = left;
        wall[POINT_5][Y] = point2Y;

        // add a seventh point to close the polygon
        wall[POINT_6][X] = point0X;
        wall[POINT_6][Y] = point0Y;

        // UNDONE - eventually remove
        if(false) {
            System.out.println("initRoomHexagon");
            System.out.println("  wall type = " + (whichWall == INNER_WALL ? "INNER" : "OUTER"));
            System.out.println("  top = " + top);
            System.out.println("  left = " + left);
            System.out.println("  wall[POINT_0][X] = " + wall[POINT_0][X]);
            System.out.println("  wall[POINT_0][Y] = " + wall[POINT_0][Y]);
            System.out.println("  wall[POINT_1][X] = " + wall[POINT_1][X]);
            System.out.println("  wall[POINT_1][Y] = " + wall[POINT_1][Y]);
        }
    }

    private void initRoomSizeParameters(RoomView roomView){
        double scaleFactor = roomView.scaleFactor;
        int roomTop = (int)roomView.topLefts[OUTER_WALL].y;
        int roomLeft = (int)roomView.topLefts[OUTER_WALL].x;
        int wallDeltas[][] = roomView.wallDeltas;

        wallDeltas[OUTER_WALL][X1] = (int)Math.round(FULL_SCALE_DELTA_X1 * scaleFactor);
        wallDeltas[INNER_WALL][X1] = (int)Math.round(wallDeltas[OUTER_WALL][X1] * 100 / 110);
        wallDeltas[OUTER_WALL][X2] = (int)Math.round(FULL_SCALE_DELTA_X2 * scaleFactor);
        wallDeltas[INNER_WALL][X2] = (int)Math.round(wallDeltas[OUTER_WALL][X2] * 180 / 190);
        wallDeltas[OUTER_WALL][Y1] = (int)Math.round(FULL_SCALE_DELTA_Y * scaleFactor);
        wallDeltas[INNER_WALL][Y1] = (int)Math.round(wallDeltas[OUTER_WALL][Y1] * 160 / 170);

        double innerWallY = Math.round(roomTop + 11* scaleFactor);
        double innerWallX= Math.round(roomLeft + 14* scaleFactor);
        roomView.topLefts[INNER_WALL] = new Point(innerWallX, innerWallY);

        // UNDONE - eventually remove
        if(false) {
            System.out.println("initRoomSizeParameters");
            System.out.println("  OUTTER top = " + roomView.topLefts[OUTER_WALL].y);
            System.out.println("  INNER top = " + roomView.topLefts[INNER_WALL].y);
            System.out.println("  OUTTER left = " + roomView.topLefts[OUTER_WALL].x);
            System.out.println("  INNER left = " + roomView.topLefts[INNER_WALL].x);
            System.out.println("wallDeltas[OUTER_WALL][X1] = " + wallDeltas[OUTER_WALL][X1]);
            System.out.println("wallDeltas[INNER_WALL][X1] = " + wallDeltas[INNER_WALL][X1]);
            System.out.println("wallDeltas[OUTER_WALL][X2] = " + wallDeltas[OUTER_WALL][X2]);
            System.out.println("wallDeltas[INNER_WALL][X2] = " + wallDeltas[INNER_WALL][X2]);
            System.out.println("wallDeltas[OUTER_WALL][Y1] = " + wallDeltas[OUTER_WALL][Y1]);
            System.out.println("wallDeltas[INNER_WALL][Y1] = " + wallDeltas[INNER_WALL][Y1]);
        }
    }



}
