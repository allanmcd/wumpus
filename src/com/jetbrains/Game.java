package com.jetbrains;

import javafx.stage.Stage;

//
// NOTE there should only be one Game object
//
class Game {
    //
    // game static variables
    //
    static Stage gameStage;
    static Cave cave;
    static GIO gio;
    static String caveName;

    //
    // Game methods
    //
    void play(){
        gameStage.setTitle("Find The Wumpus");
        gio.gotoRoom(1);
    }

    //
    // Game constructor
    //
    Game(String caveName, Stage stage){
        this.caveName = caveName;
        cave = new Cave(caveName);
        gameStage = stage;
        gio = new GIO();
    }
}

