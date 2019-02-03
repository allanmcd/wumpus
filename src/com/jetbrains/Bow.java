package com.jetbrains;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.jetbrains.Game.*;

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
    double[] rect;
    boolean fired = false;

    //
    // Bow methods
    //
    void shoot(int targetRoomNumber){
        fired = false;
        if(targetRoomNumber == Cave.wumpus.roomNumber) {
            Game.youWon();
        }
        else{
            gio.updateInfo("No Wumpus in that room.  The arrow fell to cave floor");
        }
    }

    void draw(){
        Group group = Game.gio.gioGroup;

        try {
            Image bowImage = new Image(new FileInputStream("src/bow.png"));
            ImageView bowImageView = new ImageView(bowImage);
            rect[LEFT] = player.position[0]+30;
            bowImageView.setX(rect[LEFT]);
            rect[TOP] = player.position[1] + player.position[3]/2 - 10;
            bowImageView.setY(rect[TOP]);
            group.getChildren().add(bowImageView);

            rect[RIGHT] = rect[LEFT] + bowImage.getWidth();
            rect[BOTTOM] = rect[TOP] + bowImage.getHeight();
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
        rect = new double[4];
    }
}
