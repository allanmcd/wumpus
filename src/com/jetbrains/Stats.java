package com.jetbrains;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static com.jetbrains.GIO.statusGridPane;
import static com.jetbrains.Game.cave;
import static com.jetbrains.Main.game;

/**
 * The Stats class is used to contain game stats
 */


public class Stats {
    //
    // Stats global variables
    //
    static Text txtInfo;
    static Text txtHint;
    static Text txtArrows;
    static Text txtCoins;
    static Text txtTurns;
    static Text txtScore;

    static int gamePoints;
    static int numberOfArrows;
    static int numberOfCoins;
    static int turns;
    static int score;

    VBox vBox;

    //
    // Stats member function(s)
    //
    void addCoin(){
        numberOfCoins++;
        txtCoins.setText(Integer.toString(numberOfCoins));
        updateScore();
    }

    void subtractCoin(){
        numberOfCoins--;
        txtCoins.setText(Integer.toString(numberOfCoins));
        updateScore();
    }

    void decrementArrows(){
        numberOfArrows--;
        txtArrows.setText(Integer.toString(numberOfArrows));
        updateScore();
    }

    void addTwoArrows(){
        numberOfArrows = numberOfArrows + 2;
        txtArrows.setText(Integer.toString(numberOfArrows));
        updateScore();
    }

    void anotherTurn(){
        turns++;
        txtTurns.setText(Integer.toString(turns));
        updateScore();
    }

    void setInitialValues(){
        gamePoints = 0;
        numberOfArrows = 3;
        numberOfCoins = -1;
        turns = -1;
    }

    void updateScore(){
        score = 10 * numberOfArrows + numberOfCoins - turns;
        if(cave.wumpus.dead){
            score += 100;
        }
        txtScore.setText(Integer.toString(score));
    }

    VBox panel() {
        // define the sizes of the columns of the status grid
        statusGridPane = new GridPane();

        // Arrow status
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(100));
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(40));

        // Coin status
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(80));
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(40));

        // number of turns Status
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(80));
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(60));

        // Score Status
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(80));
        statusGridPane.getColumnConstraints().add(new ColumnConstraints(60));

        statusGridPane.setPadding(new Insets(5, 20, 5, 20));

        // create the status and info controls for the bottom pane of the BorderPane
        txtInfo = new Text();
        txtHint = new Text();

        Label lblArrows = new Label("Arrows: ");
        txtArrows = new Text(Integer.toString(numberOfArrows));

        Label lblCoins = new Label("Coins: ");
        txtCoins = new Text(Integer.toString(numberOfCoins));

        Label lblTurns = new Label("Turns: ");
        txtTurns = new Text(Integer.toString(turns));

        Label lblPoints = new Label("Score: ");
        txtScore = new Text(Integer.toString(gamePoints));

        setLabelStyles(lblArrows, lblCoins, lblTurns, lblPoints);
        setTextStyles(txtInfo, txtHint, txtArrows, txtCoins, txtTurns, txtScore);

        // add the info, hint and stat labels and text boxes to the gridpane
        statusGridPane.add(txtInfo, 0, 0);
        statusGridPane.add(txtHint, 0, 1);
        statusGridPane.add(lblArrows, 0, 2);
        statusGridPane.add(txtArrows, 1, 2);
        statusGridPane.add(lblCoins, 2, 2);
        statusGridPane.add(txtCoins, 3, 2);
        statusGridPane.add(lblTurns, 4, 2);
        statusGridPane.add(txtTurns, 5, 2);
        statusGridPane.add(lblPoints, 6, 2);
        statusGridPane.add(txtScore, 7, 2);

        // aligh the status labels and values
        statusGridPane.setHalignment(lblArrows, HPos.RIGHT);
        statusGridPane.setHalignment(txtArrows,HPos.LEFT);
        statusGridPane.setHalignment(lblCoins,HPos.RIGHT);
        statusGridPane.setHalignment(txtCoins,HPos.LEFT);
        statusGridPane.setHalignment(lblTurns,HPos.RIGHT);
        statusGridPane.setHalignment(txtTurns,HPos.LEFT);
        statusGridPane.setHalignment(lblPoints,HPos.RIGHT);
        statusGridPane.setHalignment(txtScore,HPos.LEFT);

        statusGridPane.setAlignment(Pos.CENTER);

        // package the status objects together into a vertical box
        // so that they will be on top of each other
        vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(txtInfo, txtHint, statusGridPane);
        vBox.setPadding(new Insets(0,0,20,0));


        return vBox;
    }

    //
    // Stats constructor
    //
    Stats(){
    }

    //
    // Stats helper functions
    //

    private void setTextStyles(Text... texts) {
        for (Text text : texts) {
            text.setFont(Font.font("Verdana", 18));
        }
    }

    private void setLabelStyles(Label... labels) {
        for (Label label : labels) {
            label.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        }
    }
}
