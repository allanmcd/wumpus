package com.jetbrains;

import javafx.stage.Stage;

class Game {
    //
    // class instance variables
    //
    static Stage gameStage;
    static Cave cave;

    //
    // class member variables
    //

    //
    // class local variables
    //
    private int caveNumber;

    //
    // class constructor
    //
    Game(int caveNumber, Stage stage){
        this.caveNumber = caveNumber;
        cave = new Cave(caveNumber);
        gameStage = stage;
    }

    //
    // class helper functions
    //
    void play(){
        gameStage.setTitle("Find The Wumpus");
        cave.gotoRoom(1);
    }
}

