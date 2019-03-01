package com.jetbrains;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import javafx.geometry.*;

import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.primaryStage;
import static com.jetbrains.Main.useDefaults;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;

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
    private static ArrayList highScores = new ArrayList();
    private static String caveName;
    protected static final int NAME_COL = 0;
    protected static final int SCORE_COL = 1;
    protected static final int NAME_INDEX = 0;
    protected static final int SCORE_INDEX = 1;


    //
    // SplashScreen methods
    //

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

    private static void init() {
        Game.signIn();
        caveName = gio.cavePicker();
        if(caveName.length() > 0){
            if (loadHighScores(highScores)) {
                BorderPane splashBorderPane = new BorderPane();

                StackPane splashStackPane = new StackPane();
                splashStackPane.setPrefSize(400,250);
                addWumpusImage(splashStackPane);
                addPlayerPane(splashStackPane);
                splashBorderPane.setCenter(splashStackPane);;

                Label lblBlank = new Label("");
                Label lblWumpus = new Label("Wumpus");
                lblWumpus.setFont(Font.font("Verdana", FontWeight.BOLD, 36));

                Label lblCaveName = new Label("Welcome to " + caveName);
                lblCaveName.setFont(Font.font("Verdana", FontWeight.BOLD, 24));

                VBox splashHeaderPanel = new VBox();
                splashHeaderPanel.setAlignment(Pos.CENTER);
                splashHeaderPanel.getChildren().addAll(lblBlank,lblWumpus, lblCaveName);
                splashBorderPane.setTop(splashHeaderPanel);

                Label lblEnterToPlay = new Label("Press the ENTER key to play");
                lblEnterToPlay.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
                VBox splashBottomPanel = new VBox();
                splashBottomPanel.setAlignment(Pos.CENTER);
                splashBottomPanel.getChildren().add(lblEnterToPlay);
                splashBorderPane.setBottom(splashBottomPanel);

                Scene splashScene = new Scene(splashBorderPane, 400, 250);
                splashScene.setOnKeyPressed(e -> {
                    KeyCode keyCode = e.getCode();
                    if(keyCode == ENTER){
                        playGame(caveName);
                    } else if(keyCode == ESCAPE){
                        Game.quit();
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

    private static boolean loadHighScores(ArrayList highScores) {
        // assume that the load will succeed
        boolean loadSuceeded = true;
        String fileName = "src/" + caveName + ".highScores.csv";
        BufferedReader br;
        try {
            // highScores CSV format is:
            // player name, player score
            br = new BufferedReader(new FileReader(fileName));
            String line;

            // process all the lines from the high scores file
            while ((line = br.readLine()) != null) {
                String highScore[] = new String[2];
                String[] args = line.split(",");

                // process the next high score

                // add the players name
                highScore[NAME_INDEX] = args[0].trim();

                // add the players high score
                highScore[SCORE_INDEX] = args[1].trim();
                highScores.add(highScore);
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

    private static void addPlayerPane(StackPane pane) {
        int row = 0;
        GridPane playerPane = new GridPane();
        playerPane.setGridLinesVisible(true);

        // set up the column constraints
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setMinWidth(100);
        playerPane.getColumnConstraints().add(cc1);
        playerPane.getColumnConstraints().add(cc1);

        // set the row constraints
        RowConstraints rcTitle = new RowConstraints();
        rcTitle.setMinHeight(32);
        RowConstraints rcHeader = new RowConstraints();
        rcHeader.setMinHeight(24);
        playerPane.getRowConstraints().add(rcHeader);

        // add a title above the table
        addHighScoreTitle(playerPane);

        // add a header above the players score table
        addHighScoreHeader(playerPane);

        // add the individual high scores
        addHighScores(playerPane);

        playerPane.setAlignment(Pos.CENTER);
        pane.getChildren().add(playerPane);

    }

    private static void addHighScoreTitle(GridPane playerPane){
        Label lblHigh = new Label("High");
        lblHigh.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        lblHigh.setStyle("-fx-background-color: #FFFFFF;");
        //GridPane.setHalignment(lblHigh,HPos.RIGHT);
        VBox titleHighPane = new VBox();
        titleHighPane.getChildren().add(lblHigh);
        titleHighPane.setStyle("-fx-background-color: #FFFFFF;");
        titleHighPane.setAlignment(Pos.CENTER_RIGHT);
        titleHighPane.setPadding(new Insets(0, 2, 0, 0));

        Label lblScores = new Label("Scores");
        lblScores.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        lblScores.setStyle("-fx-background-color: #FFFFFF;");
        //GridPane.setHalignment(lblScores,HPos.LEFT);
        VBox titleScorePane = new VBox();
        titleScorePane.getChildren().add(lblScores);
        titleScorePane.setStyle("-fx-background-color: #FFFFFF;");
        titleScorePane.setAlignment(Pos.CENTER_LEFT);
        titleScorePane.setPadding(new Insets(0, 0, 0, 2));

        playerPane.add(titleHighPane,0,0);
        playerPane.add(titleScorePane,1,0);
    }

    private static void addHighScoreHeader(GridPane playerPane) {
        Label lblPlayerName = new Label("Name");
        lblPlayerName.setStyle("-fx-background-color: #FFFFFF;");
        lblPlayerName.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        GridPane.setHalignment(lblPlayerName, HPos.CENTER);
        HBox playerNamePane = new HBox();
        playerNamePane.getChildren().add(lblPlayerName);
        playerNamePane.setStyle("-fx-background-color: #FFFFFF;");
        playerNamePane.setAlignment(Pos.CENTER);

        Label lblPlayerScore = new Label("Score");
        lblPlayerScore.setStyle("-fx-background-color: #FFFFFF;");
        lblPlayerScore.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        GridPane.setHalignment(lblPlayerScore, HPos.CENTER);
        HBox playerScorePane = new HBox();
        playerScorePane.getChildren().add(lblPlayerScore);
        playerScorePane.setStyle("-fx-background-color: #FFFFFF;");
        playerScorePane.setAlignment(Pos.CENTER);

        playerPane.add(playerNamePane, NAME_COL, 1);
        playerPane.add(playerScorePane, SCORE_COL, 1);
    }

    private static void addHighScores(GridPane playerPane){
        RowConstraints rcEntry = new RowConstraints();
        rcEntry.setMinHeight(18);

        for(int playerGridRow = 2; playerGridRow < highScores.size()+2; playerGridRow++){
            String[] highScoreRow = (String[])highScores.get(playerGridRow-2);

            Label lblPlayerName = new Label(highScoreRow[NAME_INDEX]);
            lblPlayerName.setStyle("-fx-background-color: #FFFFFF;");
            lblPlayerName.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
            //REMOVE
            // GridPane.setHalignment(lblPlayerName,HPos.RIGHT);
            HBox playerNamePane = new HBox();
            playerNamePane.getChildren().add(lblPlayerName);
            playerNamePane.setStyle("-fx-background-color: #FFFFFF;");
            playerNamePane.setAlignment(Pos.CENTER_RIGHT);
            playerNamePane.setPadding(new Insets(0, 10, 0, 0));

            //playerNamePane.setStyle("-fx-border-color: black");

            Label lblPlayerScore = new Label(highScoreRow[SCORE_INDEX]);
            lblPlayerScore.setStyle("-fx-background-color: #FFFFFF;");
            lblPlayerScore.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
            HBox playerScorePane = new HBox();
            playerScorePane.getChildren().add(lblPlayerScore);
            playerScorePane.setStyle("-fx-background-color: #FFFFFF;");
            playerScorePane.setAlignment(Pos.CENTER_LEFT);
            playerScorePane.setPadding(new Insets(0, 0, 0, 10));
            //REMOVE
            // GridPane.setHalignment(lblPlayerScore,HPos.LEFT);

            //playerPane.add(lblPlayerName, NAME_COL,playerGridRow);
            playerPane.add(playerNamePane, NAME_COL,playerGridRow);
            //REMOVE
            // playerPane.add(new Label(""),1,0);
            playerPane.add(playerScorePane,SCORE_COL,playerGridRow);

            playerPane.getRowConstraints().add(rcEntry);
        }


    }

    private static void addWumpusImage(StackPane pane) {
        try
        {
            Image wumpusImage = new Image(new FileInputStream("src/wumpus.png"));
            ImageView wumpusImageView = new ImageView(wumpusImage);
            wumpusImageView.setPreserveRatio(true);
            wumpusImageView.setFitWidth(300);
            pane.getChildren().add(wumpusImageView);
            //pane.setCenter(wumpusImageView);
        }
        catch (FileNotFoundException e)
        {
            Debug.error(("could not add wumpus image to the splash page - wumpus.png file not found"));
        } catch (Exception e) {
            e.printStackTrace();
        }

}


}
