package com.jetbrains;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

import static com.jetbrains.Debug.message;
import static com.jetbrains.Main.useDefaults;
import static com.jetbrains.Player.numberOfCoins;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;

public final class Trivia {

    ////////////////////////////
    // Trivia Instance variables
    ////////////////////////////
    static TextArea txtTrivia;
    static boolean ignoreTrivia;
    static boolean answerAllCorrect = false;
    static boolean answerAllWrong = false;


    ////////////////////////////
    // Trivia private variables
    ////////////////////////////
    private static int maxTriviaAnswers = 4;
    private static Stage questionStage;
    private static int rightAnswers;
    private static ArrayList<String[]> triviaQuestions = new ArrayList<String[]>();
    private static int wrongAnswers;

    /////////////////
    // Trivia methods
    /////////////////

    static boolean ask (int maxQuestions, int minCorrect, String msgPreText)
    {
        // make sure the player has enough coins to answer the required questions
        if(minCorrect > numberOfCoins.get())
        {
            message("Sorry, you don't have enough coins to answer " + minCorrect + " questions");
            return false;
        }
        // set up a dialog to inform the player of the min correct and max questions
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Trivia Dialog");
        stage.setMinWidth(250);
        Label lbl = new Label();
        lbl.setText(msgPreText + " you must answer " + minCorrect + " out of " + maxQuestions + " questions correctly");

        // create an "OK" button at the bottom of the dialog
        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> stage.close());

        // create an "Pass" button to sucessfully skip all trivia questions
        HBox answerAllCorrectHbox = new HBox();
        Label lblPass = new Label("simulate all correct answers  ");
        Button btnPass = new Button();
        btnPass.setText("Pass");

        final Pane spacer1 = new Pane();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        spacer1.setMinSize(10, 1);

        final Pane spacer2 = new Pane();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        spacer2.setMinSize(10, 1);

        answerAllCorrectHbox.getChildren().addAll(spacer1, lblPass, btnPass,spacer2);

        btnPass.setOnAction(e -> {
            e.consume();
            answerAllCorrect = true;
            Player.numberOfCoins.set(numberOfCoins.get() - minCorrect - 1);
            stage.close();
        });

        // create an "Fail" button to simulate a trivia question failure
        HBox answerAllWrongHbox = new HBox();
        Label lblFail = new Label("simulate all wrong answeers");
        Button btnFail = new Button();
        btnFail.setText("Fail");
        btnFail.setOnAction(e -> {
            e.consume();
            answerAllWrong = true;
            Player.numberOfCoins.set(numberOfCoins.get() - maxQuestions - minCorrect - 2);
            stage.close();
        });

        final Pane spacer3 = new Pane();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        spacer3.setMinSize(10, 1);

        final Pane spacer4 = new Pane();
        HBox.setHgrow(spacer4, Priority.ALWAYS);
        spacer4.setMinSize(10, 1);


        answerAllWrongHbox.getChildren().addAll(spacer3, lblFail, btnFail, spacer4);


        Label blankLine = new Label("");
        blankLine.setMaxHeight(10);

        VBox pane = new VBox(20);
        VBox.setMargin(btnOK,new Insets(0,0,10,0));
        pane.getChildren().addAll(lbl, btnOK);

        if(useDefaults) {
            pane.getChildren().add(answerAllCorrectHbox);
            pane.getChildren().add(answerAllWrongHbox);
        }
        pane.setAlignment(Pos.CENTER);

        // create the scene to display the dialog contents
        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if(keyCode == ENTER){
                stage.close();
            }
        });

        // display the dialog and wait for player to click OK button
        stage.setScene(scene);
        stage.showAndWait();

        boolean retVal;
        if(answerAllCorrect || answerAllWrong) {
            // the Pass or Fail button must have been pressed
            Player.numberOfCoins.set(numberOfCoins.get() - minCorrect);
        }

        if(answerAllCorrect){
            retVal = true;
        } else if(answerAllWrong){
            retVal = false;
        } else {
            // start asking questions
            rightAnswers = 0;
            wrongAnswers = 0;
            retVal = askQuestions(maxQuestions, minCorrect);
        }
        return retVal;
    }

    static boolean init(){
        if(useDefaults){
            ignoreTrivia = true;
        }
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
                    String triviaQnA[] = new String[1 + maxTriviaAnswers];

                    String question = args[0].trim();
                    triviaQnA[0] = question;

                    // remember the correct answer
                    String correctAnswer = args[1].trim();
                    triviaQnA[1] = correctAnswer;

                    // process all the wrong answers for the current line of the file
                    int numberOfArgs = args.length <6?args.length:5;
                    for (int argsIndex = 2; argsIndex < numberOfArgs; argsIndex++) {
                        String nextWrongAnswer = args[argsIndex].trim();
                        triviaQnA[argsIndex] = nextWrongAnswer;
                    }
                    triviaQuestions.add(triviaQnA);
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

    static VBox pane(){
        VBox triviaPanel = new VBox();

        HBox hboxTriviaLabel = new HBox();
        Label lblTrivia = new Label("Trivia");
        lblTrivia.setFont(Font.font("Verdana", BOLD, 18));
        hboxTriviaLabel.getChildren().add(lblTrivia);
        hboxTriviaLabel.setAlignment(Pos.CENTER);

        txtTrivia = new TextArea();
        txtTrivia.setWrapText(true);
        txtTrivia.setPrefColumnCount(40);
        txtTrivia.setPrefRowCount(2);
        txtTrivia.setFont(Font.font("Verdana", NORMAL, 16));
        txtTrivia.setMaxHeight(50);
        txtTrivia.setMinHeight(50);

        triviaPanel.getChildren().addAll(hboxTriviaLabel, txtTrivia);

        return triviaPanel;
    }

    static String randomStatement()
    {
       String[] randomTriviaQnA;
       Random rnd = new Random();
       int numberOfTriviaQuestions = triviaQuestions.size();
       int rndTriviaIndex = rnd.nextInt(numberOfTriviaQuestions);
       randomTriviaQnA = triviaQuestions.get(rndTriviaIndex);

        // get rid of brackets [] and the text between them
       String triviaStatement = randomTriviaQnA[0];
       // ADVANCED - could do this with regular expressions
       int leftBracketOffset = triviaStatement.indexOf("[");
       int rightBracketOffset = triviaStatement.indexOf("]");
       if(leftBracketOffset > -1 && rightBracketOffset > -1){
           // remove the left and right brackets [] and the text between them
           String questionPhrase = triviaStatement.substring(leftBracketOffset,rightBracketOffset + 1);
           triviaStatement = triviaStatement.replace(questionPhrase,"");
       }

        // get rid of curly braces {} and replace the text between them with the correct answer
       // ADVANCED - could do this with regular expressions
       int leftCurlyOffset = triviaStatement.indexOf("{");
       int rightCurlyOffset = triviaStatement.indexOf("}");
       if(leftCurlyOffset > -1 && rightCurlyOffset > -1){
           // substitute the answer for the text inside the curly braces
           String questionPhrase = triviaStatement.substring(leftCurlyOffset,rightCurlyOffset + 1);
           String statementPhrase = randomTriviaQnA[1];  // AKA correct answer
           triviaStatement = triviaStatement.replace(questionPhrase,statementPhrase);
       }

        // get rid of < > leaving the text in between them
        triviaStatement = triviaStatement.replace("<","");
        triviaStatement = triviaStatement.replace(">","");

       return triviaStatement;
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

    private static boolean askQuestions(int maxQuestions, int minCorrect){
        while(wrongAnswers <= maxQuestions - minCorrect && rightAnswers < minCorrect){
            // each question costs a coin - make sure the player has at least one
            if(numberOfCoins.get() < 1){
                message("Sorry, you don't have any coins left and you still need to answer " + (minCorrect - rightAnswers) + " more question(s)");
                return false;
            }
            int triviaIndex = Player.nextTriviaIndex++;
            askQuestion(triviaIndex);
        }
        if(rightAnswers == minCorrect){
            return true;
        }
        return false;
    }

    private static void askQuestion(int triviaIndex){
        numberOfCoins.set(numberOfCoins.get() - 1);

        // create a dialog to display the question on
        questionStage = new Stage(StageStyle.UNDECORATED);
        questionStage.initModality(Modality.APPLICATION_MODAL);
        questionStage.setAlwaysOnTop(true);
        questionStage.setTitle("Trivia Dialog");
        questionStage.setMinWidth(250);

        // get the trivia question and all the answers
        String[] triviaQnA = triviaQuestions.get(triviaIndex);

        // get the trivia question and clean it up for displaying
        String question = triviaQnA[0];
        question = question.replace("{","");
        question = question.replace("}","");
        question = question.replace("[","");
        question = question.replace("]","");

        // get rid of < and >  and delete the text between them
        // ADVANCED - could do this with regular expressions
        int lessThanOffset = question.indexOf("<");
        int greaterThanOffset = question.indexOf(">");
        if(lessThanOffset > -1 && greaterThanOffset > -1){
            // remove < and > as well as the text between them
            String phrase = question.substring(lessThanOffset,greaterThanOffset + 1);
            question = question.replace(phrase,"");
        }

        Label lblQuestion = new Label(question + "?");
        lblQuestion.setPadding(new Insets(10,0,0,20));

        // get the corrrect answer
        String correctAnswer = triviaQnA[1];

        // copy all the wrong answers for this question (up to maxTriviaAnswers)
        String answers[] = new String[maxTriviaAnswers];
        int numberOfAnswers = 0;
        for(int i = 1; i < triviaQnA.length ; i++){
            String answer = triviaQnA[i];
            if(answer == null || answer.equals("")){
                break;
            }
            answers[i - 1] = answer;
            numberOfAnswers++;
        }

        // create a Border pane to put the question and answers in
        BorderPane questionPane = new BorderPane();
        questionPane.setTop(lblQuestion);

        // create check boxes for each answer and fill it in
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

        // put the questions and answers in a properly aligned pane
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

        // now display the question and answer dialog
        Scene scene = new Scene(questionPane);
        questionStage.setScene(scene);
        lblQuestion.requestFocus();
        questionStage.showAndWait();
    }

    private static void closeQuestionStage(){
        questionStage.close();
    }

    private static void rightAnswer(){
        rightAnswers++;
        closeQuestionStage();
    }

    private static void wrongAnswer(){
        wrongAnswers++;
        closeQuestionStage();
    }

}
