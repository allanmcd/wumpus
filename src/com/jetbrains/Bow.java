package com.jetbrains;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.jetbrains.GIO.gioGroup;
import static com.jetbrains.Game.*;
import static com.jetbrains.Player.numberOfArrows;

//
// NOTE there should only be one Player object
//
public class Bow {
    //
    // Bow  constants
    //
        private final int TOP = 0;
        private final int LEFT = 1;
        private final int BOTTOM = 2;
        private final int RIGHT = 3;

    //
    // Bow instance variables
    //
    int arrowsRemaining;
    boolean fired;
    ImageView imageView;
    boolean drawn;
    RoomView roomView;
    //
    // Bow methods
    //
    void shoot(int targetRoomNumber){
        drawn = false;
        fired = true;
        stats.decrementArrows();

        // update the bow image
        draw(roomView);

        if(numberOfArrows.get() == 0){
            Game.youLost("You ran out of arrrows");
        }

        if(targetRoomNumber == Cave.wumpus.roomNumber) {
            Game.youWon();
        } else if( Game.cave.bats.isInRoom(targetRoomNumber)){
            cave.bats.makeDead(targetRoomNumber);
            gio.updateInfo("You killed the bat in room " + targetRoomNumber);
            System.out.println("bat killed");
        }
        else{
            gio.updateInfo("Nothing in that room.  The arrow can not be recovered");
        }
        fired = false;
        draw(roomView);
    }

    void draw(RoomView roomView){
        this.roomView = roomView;
        try {
            // remove any existing bow image
            if(imageView != null){
                gioGroup.getChildren().remove(imageView);
            }

            // create the appropriate bow image to implement some crude animation
            Image bowImage;
            if(fired){
                // draw the empty bow image - no arrow
                bowImage = new Image(new FileInputStream("src/bow.empty.png"));
            } else if(drawn){
                // draw the bow is drawn image
                bowImage = new Image(new FileInputStream("src/bow.drawn.png"));
            } else{
                // draw the bow not drawn image
                bowImage = new Image(new FileInputStream("src/bow.png"));
            }

            imageView = new ImageView(bowImage);
            double imageWidth = roomView.scaleFactor * bowImage.getWidth();
            double imageHeight = roomView.scaleFactor * bowImage.getHeight();
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeight);

            //UNDONE - add tool tips to bow
            //Tooltip bowToolTip = new Tooltip("click on to shoot");
            //Tooltip.install(imageView,bowToolTip);

            // position the bow near the players left arm
            double imageViewLeft = player.position[0]+30;
            Double bowLeft = player.position[0] + player.position[2] - bowImage.getWidth();
            imageView.setX(bowLeft);

            Double bowTop = player.position[1] + player.position[3]/2 - 10;
            imageView.setY(bowTop);

            //UNDONE
            // draw a border aroung the bow if the user moves the mouse over it
            //imageView.setOnMouseEntered(e -> imageView.setStyle("-fx-border-color: blue"));
            //imageView.setStyle("-fx-border-color:red; -fx-background-radius:15.0" );
            //imageView.setStyle("-fx-border-width:24px solid;");

            gioGroup.getChildren().add(imageView);
        }
        catch (FileNotFoundException e)
        {
            // UNDONE should probably add code to display "e"
            Debug.error(("could not load bow.png"));
        }
    }

    //
    // Bow constructor
    //
    Bow(int initialArrows){
        arrowsRemaining = initialArrows;
    }
}
