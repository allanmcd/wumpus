package com.jetbrains;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    // pass "useDefaults" as a command line arg while debugging this code
    static boolean useDefaults;
    static Game game;
    static Stage primaryStage;
    static String userName;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

        // a blank cave name will force the user to pick one
        String caveName = "";
        newGame(caveName);
    }

    public static void newGame(String caveName){
        game = new Game(caveName, primaryStage);

        if(game.loaded) {
            game.play();
        } else {
            game.ended();
        }
    }

    public static void main(String[] args) {
        // were any arguments passed from the command line
        if(args.length > 0) {
            //NOTE this assumes that there is only one possible arg
            // see if we are in debug mode
            String arg0 = args[0];
            useDefaults = arg0.equals("useDefaults");
            System.out.println((useDefaults ? "" : "NOT ") + " using defaults");
        }

        launch(args);}
}
