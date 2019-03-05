package com.jetbrains;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    // pass "useDefaults" as a command line arg while debugging this code
    static boolean useDefaults = false;
    static boolean skipSplashScreen = false;
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
            for(int i = 0; i < args.length; i++)
            {
                switch(args[i]) {
                    case "useDefaults":
                        useDefaults = true;
                        break;
                    case "skipSplashScreen":
                        skipSplashScreen = true;
                        break;
                        default:
                            System.out.println("invalid command line switch ' " + args[i]);
                            break;
                }

            }
        }
        System.out.println((useDefaults ? "" : "NOT ") + " using defaults");
        if(skipSplashScreen) {
            System.out.println("skipping the SplashScreen");
        }

        launch(args);}
}
