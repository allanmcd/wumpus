package com.jetbrains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application{
    // pass "useDefaults" as a command line arg while debugging this code
    static boolean useDefaults;
    static Game game;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //UNDONE get cave number from user
        String caveName = "cave1";
        primaryStage.setHeight(800);
        primaryStage.setWidth(600);
        game = new Game(caveName, primaryStage);
        if(game.cave.valid){
            System.out.println(caveName + " loaded");
            game.play();
        }
        else{
            System.out.println(caveName + " NOT loaded");
        }
    }

    public static void main(String[] args) {
        // were any arguments passed from the command line
        if(args.length > 0) {
            //NOTE this assumes that there is only one possible arg
            // see if we are in debug mode
            String arg0 = args[0];
            useDefaults = arg0.equals("useDefaults");
        }

        launch(args);}
}
