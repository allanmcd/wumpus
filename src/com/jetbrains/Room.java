package com.jetbrains;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.jetbrains.Game.*;

class Room {
    //
    // constants used for readability
    //
        // INNER/OUTER refer to the inside/outside wall edges
        private static final int INNER_WALL = 0;
        private static final int OUTER_WALL = 1;

        private static final int X = 0;
        private static final int Y = 1;

        private static final int POINT_0 = 0;
        private static final int POINT_1 = 1;
        private static final int POINT_2 = 2;
        private static final int POINT_3 = 3;
        private static final int POINT_4 = 4;
        private static final int POINT_5 = 5;
        private static final int POINT_6 = 6;

    //
    // Room instance variables
    //
    Wall walls[] = {new Wall(), new Wall(), new Wall(), new Wall(), new Wall(), new Wall()};
    int roomNumber;
    boolean hasPit;
    boolean hasBeenVisited;

    //
    // Room methods
    //
    void draw(){
        Group group = Game.gio.gioGroup;

        drawHexagonWalls(group, hexagon[OUTER_WALL], Color.BLACK);
        drawHexagonWalls(group, hexagon[INNER_WALL], Color.LIGHTGRAY);

        if(hasPit){drawPit(group);}

        drawPlayer(group);

        if(hasBat(roomNumber)){drawBat(group);}

        if (Cave.wumpus.isInRoom(roomNumber)) {drawWumpus(group);}

        drawTunnels(group, walls, Color.LIGHTGRAY);

        bow.draw();
    }

    void addTunnel(int roomToTunnelTo) {
        // scan all the walls looking for the one that is adjacent to the roomToTunnelTo
        for (int wallNumberIndex = 0; wallNumberIndex < 6; wallNumberIndex++) {
            Wall caveRoomWall = walls[wallNumberIndex];
            if (caveRoomWall.adjacentRoom == roomToTunnelTo) {
                // we found the wall to assign the tunnel to
                caveRoomWall.hasTunne1 = true;
                Debug.log("added tunnel from room " + roomNumber + " to room " + roomToTunnelTo );
                break;
            }
        }
    }

    void initWallPoints(){
        //
        // initialize the hexagon points and tunnel rectangle
        // NOTE: must be done AFTER the Room class has been instantiated
        //
        initRoomHexagon(hexagon[OUTER_WALL],0,40,110, 190,170);
        initRoomHexagon(hexagon[INNER_WALL],15,50,100, 180,160);
        initRoomTunnels();
    }

    boolean hasBat(int roomNumber){
        return cave.bats.isInRoom(roomNumber);
    }
    //
    // Room constructor
    //
    Room (int newRoomNumber,
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
    //
    // Room local variables
    //

    // hexagon[inner/outer][points][x/y];
    private double[][][]hexagon = new double[2][7][2];

    //
    // Room helper functions
    //

    private void initRoomTunnels(){
        // compute the four points that define the tunnel polygon
        for(int wallNumber = 0; wallNumber < 6; wallNumber++)
        {
            if(walls[wallNumber].hasTunne1) {
                Point point1 = new Point(hexagon[INNER_WALL][wallNumber][X], hexagon[INNER_WALL][wallNumber][Y]);
                Point point2 = new Point(hexagon[INNER_WALL][wallNumber + 1][X], hexagon[INNER_WALL][wallNumber + 1][Y]);
                initWallTunnel(INNER_WALL, wallNumber, point1, point2);

                Point point3 = new Point(hexagon[OUTER_WALL][wallNumber][X], hexagon[OUTER_WALL][wallNumber][Y]);
                Point point4 = new Point(hexagon[OUTER_WALL][wallNumber + 1][X], hexagon[OUTER_WALL][wallNumber + 1][Y]);
                initWallTunnel(OUTER_WALL, wallNumber, point3, point4);
            }
        }
    }

    private void initWallTunnel(int innerOuter, int wallNumber, Point point1, Point point2){
        Wall wall = walls[wallNumber];

        if(wall.hasTunne1){
            // assumes point2 further right than point1
            double wallWidth = point2.x - point1.x;
            double tunnelMidX = point1.x + wallWidth/2;
            double tunnelWidth = .2 * wallWidth;
            double tunnelLeft = tunnelMidX - tunnelWidth/2;
            double tunnelRight = tunnelLeft + tunnelWidth;

            // assumes point2 lower than point1
            double wallHeight = point2.y - point1.y;
            double tunnelMidY = point2.y - wallHeight/2;
            double tunnelHeight = .2 * wallHeight;
            double tunnelTop = tunnelMidY - tunnelHeight/2;
            double tunnelBottom = tunnelTop + tunnelHeight;

            if(innerOuter == INNER_WALL) {
                wall.tunnel[POINT_0] = new Point(tunnelLeft, tunnelTop);
                wall.tunnel[POINT_1] = new Point(tunnelRight, tunnelBottom);
            }else {
                wall.tunnel[POINT_2] = new Point(tunnelRight, tunnelBottom);
                wall.tunnel[POINT_3] = new Point(tunnelLeft, tunnelTop);
            }
        }
    }

    private void initRoomHexagon(double[][] hexagon,int hexLeft, int hexTop, int deltaX1, int deltaX2, int deltaY){
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
        double point0X = hexLeft + deltaX1;
        double point0Y = hexTop;
        hexagon[0][X] = point0X;
        hexagon[0][Y] = point0Y;

        double point1X = point0X + deltaX2;
        double point1Y = point0Y;
        hexagon[1][X] = point1X;
        hexagon[1][Y] = point1Y;

        double point2X = point1X + deltaX1;
        double point2Y = hexTop + deltaY;
        hexagon[2][X] = point2X;
        hexagon[2][Y] = point2Y;

        double point3Y = point2Y + deltaY;
        hexagon[3][X] = point1X;
        hexagon[3][Y] = point3Y;

        hexagon[4][X] = point0X;
        hexagon[4][Y] = point3Y;

        hexagon[5][X] = hexLeft;
        hexagon[5][Y] = point2Y;

        // add a seventh point to close the polygon
        hexagon[6][X] = point0X;
        hexagon[6][Y] = point0Y;
    }

    private void drawHexagonWalls(Group group, double[][] hexPoints, Color fillColor){
        Polygon hexagon = new Polygon();
        hexagon.getPoints().addAll(new Double[]{
                hexPoints[POINT_0][X], hexPoints[POINT_0][Y],
                hexPoints[POINT_1][X], hexPoints[POINT_1][Y],
                hexPoints[POINT_2][X], hexPoints[POINT_2][Y],
                hexPoints[POINT_3][X], hexPoints[POINT_3][Y],
                hexPoints[POINT_4][X], hexPoints[POINT_4][Y],
                hexPoints[POINT_5][X], hexPoints[POINT_5][Y],
                hexPoints[POINT_6][X], hexPoints[POINT_6][Y],
        });
        hexagon.setFill(fillColor);
        group.getChildren().addAll(hexagon);
    }

    private void drawTunnels(Group group, Wall[] walls, Color fillColor) {
        for(int wallNumber = 0; wallNumber < 6; wallNumber++){
            Wall wall = walls[wallNumber];
            if(wall.hasTunne1) {
                drawTunnel(group, wall, fillColor);
            }
        }
    }

    private void drawTunnel(Group group, Wall wall, Color fillColor){
        Polygon tunnelPoly = new Polygon();
        Point point0 = (Point)wall.tunnel[0];
        Point point1 = (Point)wall.tunnel[1];
        Point point2 = (Point)wall.tunnel[2];
        Point point3 = (Point)wall.tunnel[3];
        tunnelPoly.getPoints().addAll(new Double[]{
                point0.x, point0.y,
                point1.x, point1.y,
                point2.x, point2.y,
                point3.x, point3.y,
                point0.x, point0.y
        });
        tunnelPoly.setFill(fillColor);
        group.getChildren().addAll(tunnelPoly);

        // is the game still in play
        if(stillPlayiing) {
            // define code to be executed when a click occurs on the tunnel
            tunnelPoly.setOnMouseClicked((event) -> {
                if(stillPlayiing) {
                    if (bow.fired) {
                        int targetRoom = wall.adjacentRoom;
                        bow.shoot(targetRoom);
                    } else {
                        gio.gotoRoom(wall.adjacentRoom);
                        event.consume();
                    }
                }
            });
        }
    }

    private void drawPit(Group group) {
        // display the pit image centered in the room
        try
        {
            Image pitImagep = new Image(new FileInputStream("src/pit.png"));
            ImageView imageView = new ImageView(pitImagep);

            double[] hexagonPoint5XY = hexagon[INNER_WALL][POINT_5];
            double pitImagepLeft = hexagonPoint5XY[X] - 5;
            imageView.setX(pitImagepLeft);

            double[] hexagonPoint0XY = hexagon[INNER_WALL][POINT_0];
            double pitImagepTop = hexagonPoint0XY[Y] - 5;
            imageView.setY(pitImagepTop);
            group.getChildren().add(imageView);
        }
        catch (FileNotFoundException e)
        {
            // UNDONE should probably add code to display "e"
            Debug.error(("could not load pit.png"));
        }
    }

    private void drawBat(Group group) {
        // display the bat image centered in the room

        String verticalPosition = "Centered";
        if(player.isInRoom(roomNumber)){ verticalPosition = "Top";}

        drawImage(group, verticalPosition, "bat.png");
    }

    private void drawPlayer(Group group) {
        // display the player image centered in the room

        String verticalPosition = "Centered";
        if(cave.bats.isInRoom(roomNumber)){ verticalPosition = "Bottom";}
        if(Cave.wumpus.isInRoom(roomNumber)){ verticalPosition = "Bottom";}

        player.position = drawImage(group, verticalPosition,"player.png");
    }

    private void drawWumpus(Group group) {
        // display the wumpus image in the room
        String verticalPosition = "Centered";
        if(player.isInRoom(roomNumber)){ verticalPosition = "Top";}

        drawImage(group, verticalPosition, "wumpus.png");
    }

    private double[] drawImage(Group group, String verticalPosition, String imageFileName) {
        // display an image centered in the current room
        double[] retVal = new double[4];
        try
        {
            Image image = new Image(new FileInputStream("src/" + imageFileName));
            ImageView imageView = new ImageView(image);

            double imageWidth = image.getWidth();
            double[] hexagonPoint0XY = hexagon[INNER_WALL][POINT_0];
            double[] hexagonPoint1XY = hexagon[INNER_WALL][POINT_1];
            double hexagonHorizLineWidth = hexagonPoint1XY[X] - hexagonPoint0XY[X];

            double imageLeft = hexagonPoint0XY[X] + hexagonHorizLineWidth/2 - imageWidth/2;
            imageView.setX(imageLeft);

            double imageHeight = image.getHeight();
            double[] hexagonBottomXY = hexagon[INNER_WALL][POINT_3];
            double hexagonHeight = hexagonBottomXY[Y] - hexagonPoint0XY[Y];

            // determine vertical positioning
            double imageY = 0;
            switch (verticalPosition){
                case "Top":{
                    // UNDONE - modified to better position bat & wumpus - isn't really TOP
                    //          but this hack will do for now
                    imageY = hexagonPoint0XY[Y] + 45;
                    break;
                }
                case "Bottom": {
                    imageY = hexagonBottomXY[Y] - imageHeight -10;
                    break;
                }
                case "Centered": {
                    imageY = hexagonPoint0XY[Y] + hexagonHeight/2 - imageHeight/2;
                    break;
                }
                default:{
                    Debug.error("invalid drawImage verticalPosition parameter: " + verticalPosition);
                }
            }

            imageView.setY(imageY);
            group.getChildren().add(imageView);
            retVal =  new double[]{imageLeft, imageY, imageWidth, imageHeight};
        }
        catch (FileNotFoundException e)
        {
            // UNDONE should probably add code to display "e"
            Debug.error(("could not load " + imageFileName));
        }
        return retVal;
    }
}
