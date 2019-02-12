package com.jetbrains;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.jetbrains.GIO.gioGroup;
import static com.jetbrains.Game.*;
import static com.jetbrains.Main.game;

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

    //
    // Bow methods
    //
    void shoot(int targetRoomNumber){
        drawn = false;
        fired = true;
        stats.decrementArrows();

        // update the bow image
        draw();

        if(stats.numberOfArrows == 0){
            game.youLost("You ran out of arrrows");
        }

        if(targetRoomNumber == Cave.wumpus.roomNumber) {
            Game.youWon();
        } else if( game.cave.bats.isInRoom(targetRoomNumber)){
            cave.bats.makeDead(targetRoomNumber);
            gio.updateInfo("You killed the bat in room " + targetRoomNumber);
            System.out.println("bat killed");
        }
        else{
            gio.updateInfo("Nothing in that room.  The arrow can not be recovered");
        }
        fired = false;
        draw();
    }

    void draw(){
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

            double imageViewLeft = player.position[0]+30;
            Double bowLeft = player.position[0] + player.position[2] - bowImage.getWidth();
            imageView.setX(bowLeft);

            Double bowTop = player.position[1] + player.position[3]/2 - 10;
            imageView.setY(bowTop);
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
