package com.jetbrains;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    // pass "useDefaults" as a command line arg while debugging this code
    static boolean useDefaults;
    static Game game;
    static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //UNDONE get cave number from user
        String caveName = "cave1";
        this.primaryStage = primaryStage;
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
