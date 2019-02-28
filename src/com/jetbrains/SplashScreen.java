package com.jetbrains;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.primaryStage;
import static com.jetbrains.Main.useDefaults;
import static javafx.scene.input.KeyCode.ENTER;

public final class SplashScreen {

    //
    // splashScreen instance variables
    //
    public static Stage stage;
    private static Scene scene;
    public static boolean valid = false;
    static String userName;

    // private statics
    private static boolean firstTime = true;
    private static String[][] highScores = new String[10][2];
    private static String caveName;
    protected static final int NAME_COL = 0;
    protected static final int SCORE_COL = 1;


    //
    // SplashScreen methods
    //

    private static void init() {
        Game.signIn();
        caveName = gio.cavePicker();
        if(caveName.length() > 0){
            if (loadHighScores(highScores)) {

                Group splashGroup = new Group();
                addPlayerPane(splashGroup);

                Scene splashScene = new Scene(splashGroup, 400, 250);
                splashScene.setOnKeyPressed(e -> {
                    KeyCode keyCode = e.getCode();
                    if(keyCode == ENTER){
                        playGame(caveName);
                    }
                });

                stage = new Stage(StageStyle.UNDECORATED);

                stage.setWidth(600);
                stage.setHeight(600);

                stage.setScene(splashScene);
                valid = true;
            }
        }
    }


    public static void show(){
        if(firstTime){
            init();
        }
        if(valid){
            stage.show();
        }
    }

    public static void newGame() {
        // has the player logged in yet?

        // which cave should we load
        if (useDefaults) {
            caveName = "cave1";
        } else {
            // let the user pick the game to play
            caveName = gio.cavePicker();
        }

        playGame(caveName);
    }

    private static void getCaveName(){
        caveName = gio.cavePicker();
    }

    private static void playGame(String caveName) {
        Game.init(caveName, primaryStage);

        if(Game.loaded) {
            Game.play();
        } else {
            Game.ended();
        }
    }

    public static void replayGame()
    {
        playGame(caveName);
    }

    //
    // SplashScreen constructor
    //
    private SplashScreen(){
        // make SplashScreen a singleton - simulate a static top level class
    }

    private static boolean loadHighScores(String[][] highScores) {
        // assume that the load will succeed
        boolean loadSuceeded = true;
        String fileName = "src/" + caveName + ".highScores.csv";
        BufferedReader br;
        try {
            // highScores CSV format is:
            // player name, player score
            br = new BufferedReader(new FileReader(fileName));
            String line;

            int scoreIndex = 0;

            // process all the lines from the high scores file
            while ((line = br.readLine()) != null) {
                String[] args = line.split(",");

                // process the next high score
                scoreIndex++;

                // add the players name
                highScores[scoreIndex][NAME_COL] = args[0].trim();

                // add the players high score
                highScores[scoreIndex][SCORE_COL] = args[0].trim();
            }
        } catch (FileNotFoundException e) {
            Debug.error("Could not find the file named " + fileName);
            loadSuceeded = false;
        } catch (Exception e) {
            e.printStackTrace();
            loadSuceeded = false;
        }
        valid &= loadSuceeded;
        return loadSuceeded;

    }

    private static void addPlayerPane(Group group) {
        int row = 0;
        GridPane playerPane = new GridPane();

        // add a header above the players score table
        Label playerName = new Label("Name");
        Label playerScore = new Label("Score");
        //playerPane.add(playerName,0,0,(int)playerName.getWidth(),(int)playerName.getHeight());
        playerPane.add(playerName, NAME_COL,0);
        playerPane.add(playerScore,SCORE_COL,0);

        for(int playerRow = 1; playerRow < highScores.length; playerRow++){
            playerName = new Label("Name" + playerRow);
            playerScore = new Label("Score" + playerRow);
            playerPane.add(playerName, NAME_COL,playerRow);
            playerPane.add(playerScore,SCORE_COL,playerRow);
        }

        group.getChildren().add(playerPane);
    }

}
