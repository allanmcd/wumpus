package com.jetbrains;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    // pass "useDefaults" as a command line arg while debugging this code
    static boolean useDefaults = false;
    static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        SplashScreen.show();
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
