package com.jetbrains;

import javafx.stage.Stage;

import java.util.Random;

import static com.jetbrains.Main.useDefaults;

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
    static Wumpus wumpus;

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
        int initialRoom;
        if(useDefaults){
            initialRoom = 1;
        }
        else {
            // start in a random room
            Random random = new Random();
            initialRoom = random.nextInt(29) + 1;
        }
        wumpus = new Wumpus(initialRoom);

        gio = new GIO();
        map = new Map();
        cave = new Cave(caveName, initialRoom);
        gameStage = stage;
    }
}

