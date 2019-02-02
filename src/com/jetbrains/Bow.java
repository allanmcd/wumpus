package com.jetbrains;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.jetbrains.Game.player;
import static com.jetbrains.Game.wumpus;

//
// NOTE there should only be one Player object
//
public class Bow {
    //
    // Bow  constants
    //
        final int TOP = 0;
        final int LEFT = 1;
        final int BOTTOM = 2;
        final int RIGHT = 3;
    //
    // Bow instance variables
    //
    int arrowsRemaining;
    double[] rect;
    Button btnArrow;
    boolean fired = false;

    //
    // Bow methods
    //
    boolean shoot(int targetRoomNumber){
        boolean hitWumpus = false;
        if(targetRoomNumber == wumpus.roomNumber){hitWumpus = true;}
        return hitWumpus;
    }

    void draw(){
        btnArrow.setLayoutY(400);
        btnArrow.setLayoutX(150);
        btnArrow.setMaxSize(30,50);
        Group group = Game.gio.gioGroup;
        group.getChildren().add(btnArrow);


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
        btnArrow = new Button();
        rect = new double[4];
    }
}
