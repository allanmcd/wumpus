package com.jetbrains;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.*;
import java.util.*;

import static com.jetbrains.Cave.highScores;
import static com.jetbrains.GIO.statusGridPane;
import static com.jetbrains.Game.cave;
import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.useDefaults;
import static com.jetbrains.Player.numberOfArrows;
import static com.jetbrains.Player.numberOfCoins;
import static com.jetbrains.SplashScreen.NAME_INDEX;
import static com.jetbrains.SplashScreen.SCORE_INDEX;

/**
 * The Stats class is used to contain game stats
 */


public class Stats {
    ////////////////////////////
    // Stats global variables //
    ////////////////////////////
    static Text txtInfo;
    static Text txtHint;
    static Text txtArrows;
    static Text txtCoins;
    static Text txtTurns;
    static Text txtScore;

    static int numberOfTurns;
    static int score;

    VBox vBox;

    ////////////////////////////////////
    // Stats local instance variables //
    ////////////////////////////////////
    private int scoreFudgeFactor = 0;

    //
    // Stats member function(s)
    //
    void addCoin() {
        numberOfCoins.set(numberOfCoins.get() + 1);
        update();
    }

    void anotherTurn() {
        numberOfTurns++;
        txtTurns.setText(Integer.toString(numberOfTurns));
        update();
    }

    void decrementArrows() {
        numberOfArrows.set((numberOfArrows.get()) - 1);
        //txtArrows.setText(Integer.toString(numberOfArrows));
        update();
    }

    static boolean loadHighScores() {
        // assume that the load will succeed
        boolean loadSuceeded = true;
        String fileName = "src/" + Cave.name + ".highScores.csv";
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
                String nextName = args[0].trim();
                highScore[NAME_INDEX] = nextName;

                // add the players high score
                String nextScore = args[1].trim();
                highScore[SCORE_INDEX] = nextScore;
                highScores.add(highScore);

                // is the current playaer in the high score list
                if(nextName.equals(Player.name)){
                    // current player is in the high score list
                    // update his/her previous high score
                    Player.currentHighScore = Integer.parseInt(nextScore);
                }
            }
        } catch (FileNotFoundException e) {
            Debug.error("Could not find the file named " + fileName);
            loadSuceeded = false;
        } catch (Exception e) {
            e.printStackTrace();
            loadSuceeded = false;
        }
        return loadSuceeded;
    }

    void modifyScore() {
        scoreFudgeFactor = gio.getHowMany(-100, 100, "Add/Subtract how many points:");
        update();
    }

    void saveHighScores() {
        String fileName = "src/" + Cave.name + ".highScores.csv";
        try {
            // highScores CSV format is:
            // player name, player score

            // process all the lines from the high scores file
            FileWriter highScoresFile = new FileWriter(fileName, false);

            for (int scoreIndex = 0; scoreIndex < highScores.size(); scoreIndex++) {
                String[] nextScoreEntry = new String[2];
                nextScoreEntry = (String[]) highScores.get(scoreIndex);
                String name = nextScoreEntry[NAME_INDEX];
                String highScore = nextScoreEntry[SCORE_INDEX];
                String nextFileEntry = String.format("%s,%s%n", name, highScore);
                highScoresFile.write(nextFileEntry);
            }

            highScoresFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setHighScore() {
        boolean newScoreAdded = false;
        int playersOldHighScore = Player.currentHighScore;
        boolean playerAlreadyInTop10 = false;
        boolean playerInTopTen = false;

        // Remember the current high score and player
        String[] scoreEntry = (String[]) highScores.get(0);
        int previousHighScore = Integer.parseInt(scoreEntry[SCORE_INDEX]);
        String previousHighPlayer = scoreEntry[NAME_INDEX];

        // if the player already has a high score, replace it if current one is greater
        boolean highScoreReplaced = false;
        for (int highScoresIndex = 0; highScoresIndex < highScores.size(); highScoresIndex++) {
            scoreEntry = (String[]) highScores.get(highScoresIndex);
            if (scoreEntry[NAME_INDEX].equals(Player.name)) {
                // player already has an entry
                playerAlreadyInTop10 = true;
                // should we update it
                playersOldHighScore = Integer.parseInt(scoreEntry[SCORE_INDEX]);
                if (score > playersOldHighScore) {
                    // replace old score with new high score
                    scoreEntry[SCORE_INDEX] = Integer.toString(score);
                    highScores.set(highScoresIndex, scoreEntry);
                    highScoreReplaced = true;
                    newScoreAdded = true;
                    break;
                }
            }
        }

        // if player didn't already have an entry in the high score table then add one
        if (highScoreReplaced == false && playerAlreadyInTop10 == false) {
            String[] scoreRow = new String[2];
            scoreRow[NAME_INDEX] = Player.name;
            scoreRow[SCORE_INDEX] = Integer.toString(score);
            highScores.add(scoreRow);
            newScoreAdded = true;
            // assume the player will be in top 10
            playerInTopTen = true;
        }

        // sort the array score column in descending order
        // Custom `Comparator` to sort the list of String [] on the basis of second element.
        Collections.sort(highScores, new Comparator<String[]>() {
            @Override
            public int compare(String[] a1, String[] a2) {
                return a2[1].compareTo(a1[1]);  // the reverse order is define here.
            }
        });

        // make sure the high score list never has more than 10 entries
        String[] scoreRow = new String[2];
        while (highScores.size() > 9) {
            scoreRow = (String[]) highScores.get(10);
            if (scoreRow[NAME_INDEX].equals(Player.name)) {
                // players name will be chopped off the end so no need to re-write
                newScoreAdded = false;
                playerInTopTen = false;
            }
            // remove the last element - should never be more than 11

            highScores.remove(highScores.size());
        }

        // write the high scores out if it was modified
        if (newScoreAdded) {
            saveHighScores();
        }

        // give the player some feedback
        // UNDONE - WTF - could this be more confusing
        String[] firstScoreRow = (String[]) highScores.get(0);
        String playerMsg = "";
        if (firstScoreRow[NAME_INDEX].equals(Player.name)) {
            // player is the top high scorer
            if (Player.name.equals(previousHighPlayer)) {
                // player was already the top high scorer
                if (score > playersOldHighScore) {
                    playerMsg = "Congratulations - You beat your previous high score of " + previousHighScore;
                    playerMsg += " and you are still the highest scoring player";
                } else {
                    playerMsg = "Congratulations - you are still the highest scoring player but your current score of " + score;
                    playerMsg += " does not beat your former high score of " + playersOldHighScore;
                }
            } else {
                // player was NOT already the top scorer
                playerMsg = "Congratulations - You beat " + previousHighPlayer + "'s previous high score of " + previousHighScore;
                playerMsg += " and now you are the highest scoring player";
            }
        } else if (playerAlreadyInTop10) {
            if(newScoreAdded) {
                // player is NOT top high scorer but is already in the top 10 and has a new high score
                // does player have a previous high score
                if (playersOldHighScore > 0) {
                    // player has a previous high score
                    if (score > playersOldHighScore) {
                        playerMsg = "Congratulations - you have beaten your previous high score of " + playersOldHighScore;
                        playerMsg += " and are still in the top 10 of high scores";
                    } else if (score == playersOldHighScore) {
                        playerMsg = "Congratulations - you have matched your previous high score of " + playersOldHighScore;
                        playerMsg += " and are still in the top 10 of high scores";
                    } else {
                        playerMsg = "You did not beat your previous high score of " + playersOldHighScore;
                        playerMsg += " but you are still in the top 10 of high scores";
                    }
                }
            }else {
                playerMsg = "You did not beat your previous high score of " + playersOldHighScore;
                playerMsg += " but you are still in the top 10 of high scores";
            }
        } else if (playerInTopTen) {
            if(newScoreAdded) {
                // player is NOT top high scorer but has been added to the top 10
                // does player have a previous high score
                if (playersOldHighScore > 0) {
                    // player has a previous high score
                    if (score > playersOldHighScore) {
                        playerMsg = "Congratulations - you have beaten your previous high score of " + playersOldHighScore;
                        playerMsg += " and are still in the top 10 of high scores";
                    } else if (score == playersOldHighScore) {
                        playerMsg = "Congratulations - you have matched your previous high score of " + playersOldHighScore;
                        playerMsg += " and are still in the top 10 of high scores";
                    } else {
                        playerMsg = "You did not beat your previous high score of " + playersOldHighScore;
                        playerMsg += " but you are still in the top 10 of high scores";
                    }
                } else {
                    // player does not have a previous high score
                    playerMsg = "Congratulations - you have been added to the list of top 10 high scores";
                }
            }
        } else if (playersOldHighScore > 0) {
            // Player is NOT in the top 10 but has a previous high score
            if (score == playersOldHighScore) {
                playerMsg = "you matched your old high score of " + playersOldHighScore;

            } else if (score > playersOldHighScore) {
                playerMsg = "you beat your previous high score of " + playersOldHighScore;
            } else {
                playerMsg += "You did not beat your previous high score of " + playersOldHighScore;
            }
        } else {
            playerMsg = "Your score sucks - better luck next time";
        }

        if(score > Player.currentHighScore){
            Player.currentHighScore = score;
            Player.save();
        }
        Debug.message(playerMsg);
    }

    void setInitialValues(){
        score = 0;
        numberOfCoins.set(-1);
        if(useDefaults){
            numberOfCoins.set(1);
        }
        numberOfTurns = -1;
    }

    void subtractCoin(){
        numberOfCoins.set(numberOfCoins.get() - 1);
        update();
    }

    void update(){
        // i took artistic liberties to add 15 points for each bat killed
        score = 10 * numberOfArrows.get() + 15* cave.bats.numberOfBatsKilled+ numberOfCoins.get() - numberOfTurns + scoreFudgeFactor;
        if(Wumpus.isDead){
            score += 100;
        }
        txtScore.setText(Integer.toString(score));
        txtTurns.setText(Integer.toString(numberOfTurns));
    }

    VBox pane() {
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

        // create the info, hint and trivia controls for the bottom pane of the BorderPane
        txtInfo = new Text();
        txtHint = new Text();

        Label lblArrows = new Label("Arrows: ");
        txtArrows = new Text();
        txtArrows.textProperty().bind(Bindings.convert(numberOfArrows));

        Label lblCoins = new Label("Coins: ");
        txtCoins = new Text();
        txtCoins.textProperty().bind(Bindings.convert(numberOfCoins));

        Label lblTurns = new Label("Turns: ");
        txtTurns = new Text(Integer.toString(numberOfTurns));

        Label lblPoints = new Label("Score: ");
        txtScore = new Text(Integer.toString(score));

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

        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();
        RowConstraints rc3 = new RowConstraints();
        rc3.setMaxHeight(40);
        statusGridPane.getRowConstraints().addAll( rc1, rc2,rc3);

        // package the status objects together into a vertical box
        // so that they will be on top of each other
        vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(txtInfo, txtHint, statusGridPane);
        vBox.setPadding(new Insets(0,0,20,0));

        return vBox;
    }

    ///////////////////////
    // Stats constructor //
    //////////////////////
    Stats(){
        vBox = pane();
    }

    ////////////////////////////
    // Stats helper functions //
    ///////////////////////////

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
