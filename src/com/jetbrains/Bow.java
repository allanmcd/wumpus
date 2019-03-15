package com.jetbrains;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.jetbrains.GIO.gioGroup;
import static com.jetbrains.Game.*;
import static com.jetbrains.Player.numberOfArrows;

//
// NOTE there should only be one Bow object
//
public class Bow {

    ////////////////////
    // Bow  constants //
    ////////////////////

        private final int TOP = 0;
        private final int LEFT = 1;
        private final int BOTTOM = 2;
        private final int RIGHT = 3;

    ////////////////////////////
    // Bow instance variables //
    ////////////////////////////

    int arrowsRemaining;
    boolean fired;
    boolean drawn;

    /////////////////
    // Bow methods //
    /////////////////

    void shoot(RoomView roomView, int targetRoomNumber){
        drawn = false;
        fired = true;
        stats.decrementArrows();

        if(numberOfArrows.get() == 0){
            Game.youLost("You ran out of arrrows");
        }

        if(targetRoomNumber == Wumpus.roomNumber) {
            Game.youWon();
        } else if( Game.cave.bats.isInRoom(targetRoomNumber)){
            cave.bats.makeDead(targetRoomNumber);
            gio.updateInfo("You killed the bat in room " + targetRoomNumber);
            System.out.println("bat killed");
            CaveMap.refresh();
            gio.updateHint();
        } else if( Game.cave.pits.isInRoom(targetRoomNumber)){
            gio.updateInfo("The arrow fell into a pit and can not be recovered");
        } else{
            gio.updateInfo("Nothing in that room.  The arrow can not be recovered");
        }
        fired = false;
        draw(roomView);
    }

    void draw(RoomView roomView){
        ImageView playerImageView = Player.imageView(roomView);

        String imageFileName = "";
        Image bowImage;
            // remove any existing bow image
            if(roomView.bowImageView != null){
                gioGroup.getChildren().remove(roomView.bowImageView);
            }

            // create the appropriate bow image to implement some crude animation
            if(fired){
                // draw the empty bow image - no arrow
                imageFileName = "src/bow.empty.png";
            } else if(drawn){
                // draw the bow is drawn image
                imageFileName = "src/bow.drawn.png";
            } else{
                // draw the bow not drawn image
                imageFileName = "src/bow.png";
            }

        try {
            bowImage = new Image(new FileInputStream(imageFileName));
            roomView.bowImageView = new ImageView(bowImage);
            double imageWidth = roomView.scaleFactor * bowImage.getWidth();
            double imageHeight = roomView.scaleFactor * bowImage.getHeight();
            roomView.bowImageView.setPreserveRatio(true);
            roomView.bowImageView.setFitWidth(imageWidth);
            roomView.bowImageView.setFitHeight(imageHeight);

            // determine vertical positioning
            double imageX = 0;
            double imageY = 0;
            imageX = playerImageView.getX() + playerImageView.getFitWidth()/2;

            if(roomView.isForCaveMap){
                imageY = playerImageView.getY() + 5;
            } else {
                imageY = playerImageView.getY() + 45;
            }

            roomView.bowImageView.setX(imageX);
            roomView.bowImageView.setY(imageY);

            //UNDONE
            // draw a border aroung the bow if the user moves the mouse over it
            //imageView.setOnMouseEntered(e -> imageView.setStyle("-fx-border-color: blue"));
            //imageView.setStyle("-fx-border-color:red; -fx-background-radius:15.0" );
            //imageView.setStyle("-fx-border-width:24px solid;");

            roomView.bowImageView.setStyle("-fx-border-color: red;\n" +
                    "-fx-border-style: solid;\n" +
                    "-fx-border-width: 5;");
            roomView.group.getChildren().add(roomView.bowImageView);
        } catch (FileNotFoundException e) {
            // UNDONE should probably add code to display "e"
            Debug.error(("could not load " + imageFileName));
        }

    }

    /////////////////////
    // Bow constructor //
    /////////////////////

    Bow(int initialArrows){
        arrowsRemaining = initialArrows;
    }
}
