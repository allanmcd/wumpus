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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import javafx.geometry.*;

import static com.jetbrains.Cave.highScores;
import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.*;
import static com.jetbrains.Trivia.init;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;

public final class SplashScreen {

    //
    // splashScreen instance variables
    //
    public static Stage stage;
    private static Scene scene;
    public static boolean valid = false;

    // private statics
    private static boolean firstTime = true;
    protected static final int NAME_COL = 0;
    protected static final int SCORE_COL = 1;
    protected static final int NAME_INDEX = 0;
    protected static final int SCORE_INDEX = 1;

    //
    // SplashScreen methods
    //

    public static void show(){
        if(skipSplashScreen){
            Player.name = "me";
            init();
            playGame(Cave.name);
        } else {
            if (firstTime) {
                Game.signIn();
                init();
            }
            if (valid) {
                stage.show();
            }
        }
    }

    public static void newGame() {
        // which cave should we load
        if (useDefaults) {
            Cave.name = "cave1";
            Player.name = "me";
        } else {
            // let the user pick the game to play
            Cave.name = gio.cavePicker();
        }

        playGame(Cave.name);
    }

    private static void playGame(String caveName) {
        Game.init(caveName, primaryStage);

        if(Game.loaded) {
            Game.play();
        } else {
            newGame();
        }
    }

    public static void replayGame()
    {
        playGame(Cave.name);
    }

    //
    // SplashScreen constructor
    //
    private SplashScreen(){
        // make SplashScreen a singleton - simulate a static top level class
    }

    private static void init() {
        Cave.name = gio.cavePicker();

        while(Cave.name.length() == 0 || Stats. loadHighScores() == false){
            Cave.name = gio.cavePicker();
        };

        BorderPane splashBorderPane = new BorderPane();

        StackPane splashStackPane = new StackPane();
        splashStackPane.setPrefSize(400,250);
        addWumpusImage(splashStackPane);
        addPlayerPane(splashStackPane);
        splashBorderPane.setCenter(splashStackPane);;

        // create Wumpus title pane
        Label lblBlank = new Label("");
        Label lblWumpus = new Label("Wumpus");
        lblWumpus.setFont(Font.font("Verdana", BOLD, 36));

        // create cave welcome pane
        Label lblCaveName = new Label(Player.name + ", welcome to " + Cave.name);
        lblCaveName.setFont(Font.font("Verdana", BOLD, 24));

        // create a panel to put the title and welcome panes in
        VBox splashHeaderPanel = new VBox();
        splashHeaderPanel.setAlignment(Pos.CENTER);
        splashHeaderPanel.getChildren().addAll(lblBlank,lblWumpus, lblCaveName);
        splashBorderPane.setTop(splashHeaderPanel);

        // create the "press ENTER"  hint
        Label lblEnterToPlay = new Label("Press the ENTER key to play");
        lblEnterToPlay.setFont(Font.font("Verdana", BOLD, 18));
        VBox splashBottomPanel = new VBox();
        splashBottomPanel.setAlignment(Pos.CENTER);
        splashBottomPanel.getChildren().add(lblEnterToPlay);
        splashBorderPane.setBottom(splashBottomPanel);

        // create the Splash Page scene
        Scene splashScene = new Scene(splashBorderPane, 400, 250);
        splashScene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if(keyCode == ENTER){
                playGame(Cave.name);
            } else if(keyCode == ESCAPE){
                Game.quit();
            }
        });

        // create the Splash Page stage
        stage = new Stage(StageStyle.UNDECORATED);

        stage.setWidth(600);
        stage.setHeight(600);

        stage.setScene(splashScene);
        valid = true;
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
        VBox titleHighPane = newLabelPane("High",BOLD, 24, Pos.CENTER_RIGHT, 0,2,0,0);
        playerPane.add(titleHighPane,0,0);

        VBox titleScorePane = newLabelPane("Scores",BOLD, 24, Pos.CENTER_LEFT, 0,0,0,2);
        playerPane.add(titleScorePane,1,0);
    }

    private static void addHighScoreHeader(GridPane playerPane) {
        VBox playerNamePane = newLabelPane("Name",BOLD, 18, Pos.CENTER, 0,0,0,0);
        playerPane.add(playerNamePane, NAME_COL, 1);

        VBox playerScorePane = newLabelPane("Score",BOLD, 18, Pos.CENTER, 0,0,0,0);
        playerPane.add(playerScorePane, SCORE_COL, 1);
    }

    private static void addHighScores(GridPane playerPane){
        RowConstraints rcEntry = new RowConstraints();
        rcEntry.setMinHeight(18);

        for(int playerGridRow = 2; playerGridRow < highScores.size()+2; playerGridRow++){
            String[] highScoreRow = (String[])highScores.get(playerGridRow-2);

            String name = highScoreRow[NAME_INDEX];
            VBox playerNamePane = newLabelPane(name,NORMAL, 16, Pos.CENTER_RIGHT, 0,10,0,0);

            playerPane.add(playerNamePane, NAME_COL,playerGridRow);

            String score = highScoreRow[SCORE_INDEX];
            VBox playerScorePane = newLabelPane(score,NORMAL, 16, Pos.CENTER_LEFT, 0,0,0,10);
            playerPane.add(playerScorePane,SCORE_COL,playerGridRow);

            playerPane.getRowConstraints().add(rcEntry);
        }
    }

    private static VBox newLabelPane(String labelText, FontWeight fontWeight, int fontSize, Pos pos, int top, int right, int bottom, int left){
        Label label = new Label(labelText);
        label.setFont(Font.font("Verdana",fontWeight, fontSize));
        VBox vbox = new VBox();
        vbox.getChildren().add(label);
        vbox.setStyle("-fx-background-color: #FFFFFF;");
        vbox.setAlignment(pos);
        vbox.setPadding(new Insets(top, right, bottom, left));
        return vbox;
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
