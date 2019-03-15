package com.jetbrains;

import static com.jetbrains.Game.cave;
import static com.jetbrains.WumpusEquates.*;
import static com.jetbrains.WumpusEquates.Y;

import javafx.beans.property.SimpleIntegerProperty;
import com.jetbrains.WumpusEquates;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

//
// NOTE there is only be one Player object
//
public final class Player {
    ///////////////////////////////
    // Player instance variables //
    ///////////////////////////////

    static SimpleIntegerProperty numberOfArrows = new SimpleIntegerProperty();
    static SimpleIntegerProperty numberOfCoins = new SimpleIntegerProperty();

    public static int nextTriviaIndex;

    static int roomNumber;
    static boolean isDead;
    static String name;
    // UNDONE need to save/load player object
    static int currentHighScore = 0;

    // position[X, Y, width, height];
    static double[] position;

    ////////////////////
    // Player methods //
    ////////////////////

    static void draw(RoomView roomView) {
        // display the player image centered in the room
        String verticalPosition = "Centered";
        if (cave.bats.isInRoom(roomNumber)) {
            verticalPosition = "Bottom";
        }
        if (Wumpus.isInRoom(roomNumber)) {
            verticalPosition = "Bottom";
        }

        Player.position = Cave.rooms[roomView.currentRoomNumber].drawImage(roomView, OPAQUE, verticalPosition, "player.png");
    }

    static boolean isInRoom(int caveRoomNumber){
        boolean isInRoom = false;
        if(roomNumber == caveRoomNumber){isInRoom = true;}
        return isInRoom;
    }

    static void init(){

        roomNumber = cave.initialRoom;
        numberOfArrows.set(3);
        isDead = false;
    }

    static void load(){
        // UNDONE - need to implement
    }

    static ImageView imageView(RoomView roomView) {
        // compute the top/left position for the Player based on the RoomView
        double[] retVal = new double[4];
        Group group = roomView.group;
        double scaleFactor = roomView.scaleFactor;
        String imageFileName = "src/player.png";
        try
        {
            Image image = new Image(new FileInputStream(imageFileName));
            ImageView imageView = new ImageView(image);
            double imageWidth = scaleFactor * image.getWidth();
            double imageHeight = scaleFactor * image.getHeight();
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeight);

            // get the x,y coordinates for the top horizontal line of the hex polygon
            double[] hexagonPoint0XY = roomView.hexagon[INNER_WALL][POINT_0];
            double[] hexagonPoint1XY = roomView.hexagon[INNER_WALL][POINT_1];
            double hexagonHorizLineWidth = hexagonPoint1XY[X] - hexagonPoint0XY[X];

            double imageLeft = hexagonPoint0XY[X] + hexagonHorizLineWidth / 2 - imageWidth / 2;
            imageView.setX(imageLeft);

            // get the bottom coordinates for the bottom line of the hex polygon
            double[] hexagonBottomXY = roomView.hexagon[INNER_WALL][POINT_3];
            double hexagonHeight = hexagonBottomXY[Y] - hexagonPoint0XY[Y];

            // determine the proper vertical positioning for the player image
            // display the player image centered in the room
            String verticalPosition = "Centered";
            if (cave.bats.isInRoom(roomView.currentRoomNumber)) {
                verticalPosition = "Bottom";
            }
            if (Wumpus.isInRoom(roomView.currentRoomNumber)) {
                verticalPosition = "Bottom";
            }
            double imageY = 0;
            switch (verticalPosition) {
                case "Top": {
                    // UNDONE - modified to better position bat & wumpus - isn't really TOP
                    //          but this hack will do for now
                    imageY = hexagonPoint0XY[Y] + 45;
                    break;
                }
                case "Bottom": {
                    imageY = hexagonBottomXY[Y] - imageHeight - 10;
                    break;
                }
                case "Centered": {
                    imageY = hexagonPoint0XY[Y] + hexagonHeight / 2 - imageHeight / 2;
                    break;
                }
                default: {
                    Debug.error("invalid drawImage verticalPosition parameter: " + verticalPosition);
                }
            }

            imageView.setY(imageY);
            retVal = new double[]{imageLeft, imageY, imageWidth, imageHeight};

            System.out.println("");
            System.out.println("Player.position() called");
            System.out.println("imageLeft  " + imageLeft);
            System.out.println("imageY  " + imageY);
            System.out.println("imageWidth  " + imageWidth);
            System.out.println("imageHeight  " + imageHeight);
            System.out.println("imageView.getFitWidth " + imageView.getFitWidth());
            System.out.println("imageView.getFitHeight " + imageView.getFitHeight());
            System.out.println("imageView.getX " + imageView.getX());
            System.out.println("imageView.getY " + imageView.getY());
            return imageView;
        } catch (FileNotFoundException e) {
            // UNDONE should probably add code to display "e"
            Debug.error(("could not load " + imageFileName));
        }
        return null;
    }

    static void save(){
        // UNDONE - need to implement
    }

    ////////////////////////
    // Player constructor //
    ////////////////////////

    private Player(){
        // Player is a static object, much like Math
    }
}
