package com.jetbrains;

import javafx.stage.Stage;

//
// NOTE there should only be one Game object
//
class Game {
    //
    // game static variables
    //
    static Cave cave;
    static Stage gameStage;
    static GIO gio;
    static Map map;
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
        gio = new GIO();
        map = new Map();
        cave = new Cave(caveName);
        gameStage = stage;
    }
}

