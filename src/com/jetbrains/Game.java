package com.jetbrains;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import javafx.stage.Stage;


public class Game {
    int caveNumber = 1;

    Stage gameStage;

    Cave cave = new Cave( caveNumber);

    public Game(int caveNumber, Stage gameStage){
        caveNumber = caveNumber;
        this.gameStage = gameStage;
    }

    public void play(){
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        gameStage.setTitle("Find The Wumpus");
        cave.rooms[1].draw(gameStage);
    }
}

