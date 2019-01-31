package com.jetbrains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        // get cave number from user
        String caveName = "cave1";
        primaryStage.setHeight(800);
        primaryStage.setWidth(600);
        Game game = new Game(caveName, primaryStage);
        if(game.cave.valid){
            System.out.println(caveName + " loaded");
            game.play();
        }
        else{
            System.out.println(caveName + " NOT loaded");
        }
    }

    public static void main(String[] args) {
        launch(args);}
}
