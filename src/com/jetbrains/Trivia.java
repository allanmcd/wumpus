package com.jetbrains;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import static javafx.scene.input.KeyCode.ENTER;

public final class Trivia {
    
    ////////////////////////////
    // Trivia Instance variables
    ////////////////////////////
    private static Stage questionStage;
    private static int rightAnswers;
    private static String[][] triviaQuestions = new String[100][5];
    private static int wrongAnswers;

    /////////////////
    // Trivia methods
    /////////////////
    public static boolean ask (int maxQuestions, int minCorrect)
    {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Trivia Dialog");
        stage.setMinWidth(250);
        Label lbl = new Label();
        lbl.setText("You must answer " + minCorrect + " out of " + maxQuestions + " questions correctly");

        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> stage.close());

        Label blankLine = new Label("");
        blankLine.setMaxHeight(10);

        VBox pane = new VBox(20);
        VBox.setMargin(btnOK,new Insets(0,0,10,0));
        pane.getChildren().addAll(lbl, btnOK);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if(keyCode == ENTER){
                stage.close();
            }
        });

        stage.setScene(scene);
        stage.showAndWait();
        rightAnswers = 0;
        wrongAnswers = 0;
        boolean retVal = askQuestions(maxQuestions, minCorrect);
        return retVal;
    }

    public static boolean init(){
        // assume that the initialization will succeed
        boolean initSuceeded = true;
        String triviaFileName = "src/trivia.csv";
        BufferedReader br;
        try {
            // cave CSV format is:
            // question, correct answer, one to three wrong answers
            br = new BufferedReader(new FileReader(triviaFileName));
            String line;

            int questionNumber = -1;

            // process all the lines from the Trivia Questions file
            while ((line = br.readLine()) != null) {
                // treat lines that start with // as a comment and ignore them
                if (line.indexOf("//") != 0) {
                    // not a comment so process it
                    String[] args = line.split(",");

                    // remember the next question
                    questionNumber++;
                    triviaQuestions[questionNumber][0] = args[0].trim();

                    // remember the correct answer
                    triviaQuestions[questionNumber][1] = args[1].trim();

                    // process all the wrong answers for the current line of the file
                    int numberOfArgs = args.length <6?args.length:5;
                    for (int argsIndex = 2; argsIndex < numberOfArgs; argsIndex++) {
                        triviaQuestions[questionNumber][argsIndex] = args[argsIndex].trim();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Debug.error("Could not find the file named " + triviaFileName);
            initSuceeded = false;
        } catch (Exception e) {
            e.printStackTrace();
            initSuceeded = false;
        }
        return initSuceeded;
    }

    //////////////////////
    // Trivia constructor
    /////////////////////
    private Trivia(){
        // make sure Trivia is a singleton
    }

    //////////////////////////
    // Trivia helper functions
    //////////////////////////
    private static void wrongAnswer(){
        wrongAnswers++;
        closeQuestionStage();
    }

    private static void rightAnswer(){
        rightAnswers++;
        closeQuestionStage();
    }

    private static void closeQuestionStage(){
        questionStage.close();
    }

    private static boolean askQuestions(int maxQuestions, int minCorrect){
        while(wrongAnswers <= maxQuestions - minCorrect && rightAnswers < minCorrect){
            int triviaIndex = Player.nextTriviaIndex++;
            askQuestion(triviaIndex);
        }
        if(rightAnswers == minCorrect){
            return true;
        }
        return false;
    }

    private static void askQuestion(int triviaIndex){
        questionStage = new Stage(StageStyle.UNDECORATED);
        questionStage.initModality(Modality.APPLICATION_MODAL);
        questionStage.setAlwaysOnTop(true);
        questionStage.setTitle("Trivia Dialog");
        questionStage.setMinWidth(250);

        String question = triviaQuestions[triviaIndex][0];
        question = question.replace("{","");
        question = question.replace("}","");
        question = question.replace("[","");
        question = question.replace("]","");
        Label lblQuestion = new Label(question + "?");
        lblQuestion.setPadding(new Insets(10,0,0,20));

        String correctAnswer = triviaQuestions[triviaIndex][1];

        // copy all the answers for this question
        String answers[] = new String[4];
        int numberOfAnswers = 0;
        for(int i = 1; i < triviaQuestions[0].length ; i++){
            String answer = triviaQuestions[triviaIndex][i];
            if(answer == null || answer.equals("")){
                break;
            }
            answers[i - 1] = answer;
            numberOfAnswers++;
        }

        BorderPane questionPane = new BorderPane();
        questionPane.setTop(lblQuestion);

        // fill in an answer for each check box
        Random rnd = new Random();
        VBox answerPane = new VBox(5);
        for(int i = 0; i < numberOfAnswers; i++) {
            // generate a random number from 0 to 3
            int nextAnswerIndex = rnd.nextInt(numberOfAnswers);
            String nextAnswer = answers[nextAnswerIndex];
            while(nextAnswer.equals("")){
                // find an answer that hasn't already been picked
                nextAnswerIndex = rnd.nextInt(numberOfAnswers);
                nextAnswer = answers[nextAnswerIndex];
            }
            // remember that we used this answer
            answers[nextAnswerIndex] = "";

            CheckBox nextCheckBox = new CheckBox(nextAnswer);
            nextCheckBox.setSelected(false);
            //nextCheckBox.setAlignment(Pos.TOP_LEFT);
            if(nextAnswer.equals(correctAnswer)){

                nextCheckBox.setOnAction(e -> rightAnswer());
            } else {
                nextCheckBox.setOnAction(e -> wrongAnswer());
            }
            answerPane.getChildren().add(nextCheckBox);
        }
        answerPane.setAlignment(Pos.BASELINE_LEFT);

        HBox answerPaneCentered = new HBox();
        final Pane spacer1 = new Pane();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        spacer1.setMinSize(20, 1);

        final Pane spacer2 = new Pane();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        spacer2.setMinSize(20, 1);

        answerPaneCentered.getChildren().addAll(spacer1, answerPane, spacer2);
        answerPaneCentered.setPadding(new Insets(10,0,20,0));

        questionPane.setCenter(answerPaneCentered);

        Scene scene = new Scene(questionPane);
        questionStage.setScene(scene);
        lblQuestion.requestFocus();
        questionStage.showAndWait();
    }
}
