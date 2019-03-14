package com.jetbrains;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.jetbrains.Game.*;
import static com.jetbrains.WumpusEquates.*;

class Room {

    /////////////////////////////
    // Room instance variables //
    /////////////////////////////

    int distaceFromWumpus = -1;
    boolean hasBeenVisited;
    boolean hasPit;
    int roomNumber;
    Wall wallWithTunnelClosestToWumpus;
    Wall wallWithTunnelClosestToPlayer;
    protected Wall walls[] = {new Wall(), new Wall(), new Wall(), new Wall(), new Wall(), new Wall()};

    ////////////////////////////
    // Room private variables //
    ////////////////////////////

    private final double OPAQUE = 1.0;

    //////////////////
    // Room methods //
    //////////////////

    void draw(RoomView roomView) {
        roomView.currentRoomNumber = roomNumber;

        roomView.initWallPoints(roomView);
        initRoomTunnels(roomView);

        drawHexagonWalls(roomView, OUTER_WALL, Color.BLACK);
        drawHexagonWalls(roomView, INNER_WALL, roomView.floorColor);

        if (hasPit) {
            drawImage(roomView, roomView.pitImageOpacity, "Centered", "pit.png");
        }

        if (roomView.showPlayer && !Player.isDead) {
            if (Player.roomNumber == roomNumber) {
                drawPlayer(roomView);
                bow.draw(roomView);
            }
        }

        if (hasBat()) {
            drawBat(roomView);
        }

        if (Wumpus.isInRoom(roomNumber)) {
            drawWumpus(roomView);
        }

        drawTunnels(roomView.group, walls, Color.LIGHTGRAY);

        if (wallWithTunnelClosestToWumpus != null || wallWithTunnelClosestToPlayer != null) {
            Wumpus.drawShortestPathInRoom(roomView);
        }
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

    boolean hasBat(){
        return cave.bats.isInRoom(roomNumber);
    }

    //////////////////////
    // Room constructor //
    /////////////////////

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

    ///////////////////////////
    // Room helper functions //
    ///////////////////////////
    private void initRoomTunnels(RoomView roomView){
        // compute the four points that define the tunnel polygon
        for(int wallNumber = 0; wallNumber < 6; wallNumber++)
        {
            if(walls[wallNumber].hasTunne1) {
                Point point1 = new Point(roomView.hexagon[INNER_WALL][wallNumber][X], roomView.hexagon[INNER_WALL][wallNumber][Y]);
                Point point2 = new Point(roomView.hexagon[INNER_WALL][wallNumber + 1][X], roomView.hexagon[INNER_WALL][wallNumber + 1][Y]);
                initWallTunnel(INNER_WALL, wallNumber, point1, point2);

                Point point3 = new Point(roomView.hexagon[OUTER_WALL][wallNumber][X], roomView.hexagon[OUTER_WALL][wallNumber][Y]);
                Point point4 = new Point(roomView.hexagon[OUTER_WALL][wallNumber + 1][X], roomView.hexagon[OUTER_WALL][wallNumber + 1][Y]);
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

    private void drawBat(RoomView roomView) {
        // display the bat image centered in the room

        String verticalPosition = "Centered";
        if(Player.isInRoom(roomNumber)){
            verticalPosition = "Top";
        }

        drawImage(roomView, OPAQUE, verticalPosition, "bat.png");
    }

    private void drawHexagonWalls(RoomView roomView, int whichWall, Color fillColor){
        double[][] hexPoints = roomView.hexagon[whichWall];
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
        roomView.group.getChildren().addAll(hexagon);
    }

    private double[] drawImage(RoomView roomView, double opacity, String verticalPosition, String imageFileName) {
        // display an image centered in the current room
        double[] retVal = new double[4];
        Group group = roomView.group;
        double scaleFactor = roomView.scaleFactor;
        try
        {
            Image image = new Image(new FileInputStream("src/" + imageFileName));
            ImageView imageView = new ImageView(image);
            imageView.setOpacity(opacity);
            double imageWidth = scaleFactor * image.getWidth();
            double imageHeight = scaleFactor * image.getHeight();
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeight);

            //double imageWidth = image.getWidth();
            double[] hexagonPoint0XY = roomView.hexagon[INNER_WALL][POINT_0];
            double[] hexagonPoint1XY = roomView.hexagon[INNER_WALL][POINT_1];
            double hexagonHorizLineWidth = hexagonPoint1XY[X] - hexagonPoint0XY[X];

            double imageLeft = hexagonPoint0XY[X] + hexagonHorizLineWidth/2 - imageWidth/2;
            imageView.setX(imageLeft);

            //double imageHeight = image.getHeight();
            double[] hexagonBottomXY = roomView.hexagon[INNER_WALL][POINT_3];
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

    private void drawPlayer(RoomView roomView) {
        // display the player image centered in the room
        String verticalPosition = "Centered";
        if(cave.bats.isInRoom(roomNumber)){ verticalPosition = "Bottom";}
        if(Wumpus.isInRoom(roomNumber)){ verticalPosition = "Bottom";}

        Player.position = drawImage(roomView, OPAQUE, verticalPosition,"player.png");
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
                    if (bow.drawn) {
                        event.consume();
                        int targetRoom = wall.adjacentRoom;
                        bow.shoot(targetRoom);
                    } else {
                        event.consume();
                        gio.gotoRoom(wall.adjacentRoom, "You crawled through a tunnel into");
                    }
                }
            });
        }
    }

    private void drawWumpus(RoomView roomView) {
        // display the wumpus image in the room
        String verticalPosition = "Centered";
        if(Player.isInRoom(roomNumber)){ verticalPosition = "Top";}

        drawImage(roomView, OPAQUE, verticalPosition, "wumpus.png");
    }
}
