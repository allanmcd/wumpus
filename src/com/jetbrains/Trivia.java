package com.jetbrains;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class Trivia {
    //
    // Trivia Instance variables
    //
    private static String[][] triviaQuestions = new String[100][5];
    //
    // Trivia methods
    //
    public static boolean ask (int maxQuestions, int minCorrect)
    {
        boolean passed = false;
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Trivia Dialog");
        stage.setMinWidth(250);
        Label lbl = new Label();
        lbl.setText("You must answer " + maxQuestions + " questions correctly");
        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> stage.close());
        VBox pane = new VBox(20);
        pane.getChildren().addAll(lbl, btnOK);
        pane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.showAndWait();
        return passed;
    }

    public static boolean init(){
        // assume that the initialization will succeed
        boolean initSuceeded = true;
        String fileName = "src/trivia.csv";
        BufferedReader br;
        try {
            // cave CSV format is:
            // question, correct answer, one to three wrong answers
            br = new BufferedReader(new FileReader(fileName));
            String line;

            // ignore the first line - it contains the format descriptino
            br.readLine();

            int questionNumber = -1;

            // process all the lines from the Trivia Questions file
            while ((line = br.readLine()) != null) {
                String[] args = line.split(",");

                // remember the next question
                questionNumber++;
                triviaQuestions[questionNumber][0] = args[0].trim();

                // remember the correct answer
                triviaQuestions[questionNumber][1] = args[1].trim();

                // process all the wrong answers for the current line of the file
                for (int argsIndex = 2; argsIndex < args.length; argsIndex++) {
                    triviaQuestions[questionNumber][argsIndex] = args[argsIndex].trim();
                }
            }
        } catch (FileNotFoundException e) {
            Debug.error("Could not find the file named " + fileName);
            initSuceeded = false;
        } catch (Exception e) {
            e.printStackTrace();
            initSuceeded = false;
        }
        return initSuceeded;
    }

    //
    // Trivia consturctor
    //
    private Trivia(){
        // make sure Trivia is a singleton
    }
}
