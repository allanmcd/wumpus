package com.jetbrains;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.jetbrains.GIO.centerLabelInHBox;
import static com.jetbrains.GIO.message;
import static com.jetbrains.Game.gio;
import static com.jetbrains.Main.useDefaults;
import static com.jetbrains.Player.numberOfCoins;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;

public final class Trivia {

    //////////////////////
    // Trivia CONSTANTS //
    //////////////////////

    static final int ALREADY_ASKED = 0;
    static final int QUESTION = 1;
    static final int CORRECT_ANSWER = 2;

    static final String HEADER ="// format:  R/W/N, question, correct answer, one to three wrong answers\n"+
                                "//\n"+
                                "//   R: question has been asked and answered correctly (Right)\n" +
                                "//   W: question has been asked and answered incorrectly (Wrong)\n" +
                                "//   N: question has Not been asked\n" +
                                "//\n"+
                                "// to produce a question, the following will occur\n"+
                                "//   the curly brackets will be removed\n"+
                                "//   < and > and the text within them will be removed\n"+
                                "//   left and right brackets [] will be removed\n"+
                                "//\n"+
                                "// to produce a statement, the following will occur\n"+
                                "//   The text within {} will be replaced with the correct answer\n"+
                                "//   text within <> will be included\n"+
                                "//   characters between [] will be deleted\n"+
                                "//\n"+
                                "// &COMMA& will be replaced with a ,\n"+
                                "//\n";

    ///////////////////////////////
    // Trivia Instance variables //
    ///////////////////////////////

    static boolean answerAllCorrect = false;
    static boolean answerAllWrong = false;
    static boolean bypassTrivia;
    static Random triviaRnd = new Random();
    static TextArea txtTrivia;

    // these have to be non-local because of scoping issues
    // or because they are used in an event handler
    static private boolean displayNext;
    static private boolean nextButtonClicked;
    static private boolean nextButtonPressed;
    static private int questionIndex;
    static private BorderPane questionPane;
    static private int triviaHeaderSize;
    static private int triviaIndex;
    static private VBox vbStatement;


    // making the triviaPane static is probably not a good idea
    // needs further investigation
    static VBox triviaPane;

    //////////////////////////////
    // Trivia private variables //
    //////////////////////////////

    private static int maxTriviaAnswers = 4;
    private static Stage answerStage;
    private static int rightAnswers;
    private static ArrayList<String[]> triviaQuestions = new ArrayList<String[]>();
    private static int wrongAnswers;

    ////////////////////
    // Trivia methods //
    ////////////////////

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
        HBox hbxPrompt = centerLabelInHBox(lbl, new Insets(10,20,0,20));

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
        pane.getChildren().addAll(hbxPrompt
                , btnOK);

        if(bypassTrivia) {
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

    static void displayTriviaQuestions(){
        int firstIndex = gio.getHowMany(true, 0, 0,
                                        triviaQuestions.size(),
                                        "Index of first trivia question to display (0 based)");
        triviaIndex = firstIndex;

        // create a dialog to display the question on
        Stage triviaStage = new Stage(StageStyle.UTILITY);
        triviaStage.initModality(Modality.APPLICATION_MODAL);
        triviaStage.setAlwaysOnTop(true);
        triviaStage.setMinWidth(250);

        // create a Border pane to put the question and answers in
        boolean enableEvents = false;
        boolean addStatement = true;
        displayNext = true;
        boolean continueDisplayingTrivia = true;

        do {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                if (displayNext) {
                    displayNext = false;
                    nextButtonClicked = false;
                    nextButtonPressed = false;
                    questionPane = createQuestionPane(triviaIndex, enableEvents, addStatement);

                    // now display the question and answer dialog
                    Scene scene = new Scene(questionPane);
                    triviaStage.setScene(scene);
                    questionPane.getTop().requestFocus();

                    // add a "Next" button
                    Button btnNext = new Button("Next");
                    HBox hbxNext = GIO.centerButtonInHBox(btnNext);

                    btnNext.setOnAction(e -> {
                        displayNext = true;
                        triviaIndex++;
                        nextButtonClicked = true;
                    });

                    scene.setOnKeyPressed(e -> {
                        KeyCode keyCode = e.getCode();
                        if(keyCode == ENTER){

                            displayNext = true;
                            triviaIndex++;
                            nextButtonPressed = true;
                            triviaStage.close();
                        } else if(keyCode == ESCAPE){
                            triviaStage.close();
                        }
                    });

                    VBox vbxDisplayBottom = new VBox();
                    vbxDisplayBottom.getChildren().add( hbxNext);
                    questionPane.setBottom(vbxDisplayBottom);

                    String title = "Trivia.csv line number  " + (triviaIndex + triviaHeaderSize +1);
                    title = title + " (Index " + triviaIndex + ")";

                    triviaStage.setTitle(title);
                    triviaStage.showAndWait();
                    if(nextButtonClicked == false && nextButtonPressed == false){
                        continueDisplayingTrivia = false;
                    }
                }
           } catch (InterruptedException ex) {
            }
        } while (continueDisplayingTrivia && triviaIndex < triviaQuestions.size()) ;
    }

    static boolean init(){
        if(useDefaults){
            bypassTrivia = true;
        }

        // figure out the size of the Trival.csv header - comments prior to trivia questions
        triviaHeaderSize = HEADER.split( "\n").length;

        // assume that the initialization will succeed
        boolean initSuceeded = true;
        String triviaFileName = "src/trivia.csv";
        BufferedReader br;
        try {
            // cave CSV format is:
            // beenused(Y/N), question, correct answer, one to three wrong answers
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
                    String triviaQnA[] = new String[1 + 1 + maxTriviaAnswers];

                    // has this question been asked yet
                    String beenUsed = args[0].trim();
                    triviaQnA[ALREADY_ASKED] = beenUsed;

                    String question = args[1].trim();
                    triviaQnA[QUESTION] = question;

                    // remember the correct answer
                    String correctAnswer = args[2].trim();
                    triviaQnA[CORRECT_ANSWER] = correctAnswer;

                    // process all the wrong answers for the current line of the file
                    int numberOfArgs = args.length <7?args.length:6;
                    for (int argsIndex = 3; argsIndex < numberOfArgs; argsIndex++) {
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
        triviaPane = new VBox();

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

        triviaPane.getChildren().addAll(hboxTriviaLabel, txtTrivia);

        return triviaPane;
    }

    static String randomStatement()
    {
       String[] randomTriviaQnA;
       int numberOfTriviaQuestions = triviaQuestions.size();
       int rndTriviaIndex = (int)Math.floor((Math.random() * numberOfTriviaQuestions));

       System.out.println("rndTriviaIndex = " + rndTriviaIndex);
       randomTriviaQnA = triviaQuestions.get(rndTriviaIndex);

       // get rid of brackets [] and the text between them
       String triviaStatement = formatStatement(randomTriviaQnA);
       return triviaStatement;
    }

    public static void saveTriviaQuestions(){
        String fileName = "src/trivia.csv";
        try {
            // process all the trivia questions
            FileWriter triviaFileWriter = new FileWriter(fileName, false);

            triviaFileWriter.write(HEADER);

            for (int questionIndex = 0; questionIndex < triviaQuestions.size(); questionIndex++) {
                String formattedTriviaQnA = Arrays.toString(triviaQuestions.get(questionIndex));
                // Arrays.toString will produce [n1, n2, n3, n4] or possilby [n1,n2,n3,null]
                // get rid of leading bracket
                formattedTriviaQnA = formattedTriviaQnA.substring(1);
                // get rid of trailing bracket
                formattedTriviaQnA = formattedTriviaQnA.substring(0, formattedTriviaQnA.length() - 1);
                // remove any null answers - some questions have less than 4 answers
                formattedTriviaQnA = formattedTriviaQnA.replace(", null","");
                // write out the next trivia question in CSV format
                triviaFileWriter.write(formattedTriviaQnA + "\n");
                System.out.println("trivia line " + questionIndex + " = " + "\"" + formattedTriviaQnA + "\"");
            }

            triviaFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////
    // Trivia constructor //
    ////////////////////////

    private Trivia(){
        // make sure Trivia is a singleton
    }

    /////////////////////////////
    // Trivia helper functions //
    /////////////////////////////

    private static boolean askQuestions(int maxQuestions, int minCorrect){
        boolean answeredMinCorrect = false;
        int questionsAsked = 0;
        int stillNeed = minCorrect;
        int remaining = maxQuestions;
        while(wrongAnswers <= maxQuestions - minCorrect && rightAnswers < minCorrect){
            // each question costs a coin - make sure the player has at least one
            if(numberOfCoins.get() < 1){
                message("Sorry, you don't have any coins left and you still need to answer " + (minCorrect - rightAnswers) + " more question(s)");
                return false;
            }
            int triviaIndex = nextTriviaIndex();
            remaining = maxQuestions - rightAnswers - wrongAnswers -1;
            stillNeed = minCorrect - rightAnswers;
            askQuestion(triviaIndex, stillNeed, remaining);
            questionsAsked++;
        }
        if(questionsAsked < maxQuestions && rightAnswers != minCorrect) {
            message("Sorry, you can't answer " + stillNeed + " questions correctly with your remaining " + remaining + " questions");
        } else if (rightAnswers == minCorrect) {
                answeredMinCorrect = true;
        }

        return answeredMinCorrect;
    }

    private static void askQuestion(int triviaIndex, int stillNeed, int remaining){
        numberOfCoins.set(numberOfCoins.get() - 1);

        // create a dialog to display the question on
        answerStage = new Stage(StageStyle.UNDECORATED);
        answerStage.initModality(Modality.APPLICATION_MODAL);
        answerStage.setAlwaysOnTop(true);
        answerStage.setMinWidth(250);

        // create a Border pane to put the question and answers in
        boolean enableEvents = true;
        boolean showStatement = false;
        BorderPane questionPane = createQuestionPane(triviaIndex, enableEvents, showStatement );

        Label lblStillNeed = new Label("You still need to answer " + stillNeed + " more questions correctly");
        HBox hbxStillNeed = centerLabelInHBox(lblStillNeed);

        Label lblRemaining = new Label("You have " + remaining + " more questions after this one");
        HBox hbxRemaining = centerLabelInHBox(lblRemaining, new Insets(5,0,10,0));
        hbxRemaining.setPadding(new Insets(5,0,10,0));

        VBox vbStillNeed = new VBox();
        vbStillNeed.getChildren().addAll(hbxStillNeed, hbxRemaining);
        questionPane.setBottom(vbStillNeed);

        // now display the question and answer dialog
        Scene scene = new Scene(questionPane);
        answerStage.setScene(scene);
        questionPane.getTop().requestFocus();
        answerStage.showAndWait();
    }

    private static void closeQuestionStage(){
        answerStage.close();
    }

    private static BorderPane createQuestionPane(int triviaIndex, boolean setCheckBoxEvents, boolean showStatement){
        // get the trivia question and all the answers
        String[] triviaQnA = triviaQuestions.get(triviaIndex);

        // get the trivia question and clean it up for displaying
        String question = triviaQnA[QUESTION];
        question = question.replace("{","");
        question = question.replace("}","");
        question = question.replace("[","");
        question = question.replace("]","");
        question = question.replace("%COMMA%",",");

        // get rid of < and >  and delete the text between them
        // ADVANCED - could do this with regular expressions
        int lessThanOffset = question.indexOf("<");
        int greaterThanOffset = question.indexOf(">");
        while(lessThanOffset > -1 && greaterThanOffset > -1 && greaterThanOffset > lessThanOffset){
            // remove < and > as well as the text between them
            String phrase = question.substring(lessThanOffset,greaterThanOffset + 1);
            question = question.replace(phrase,"");
            lessThanOffset = question.indexOf("<");
            greaterThanOffset = question.indexOf(">");
        }

        Label lblQuestion = new Label(question + "?");
        lblQuestion.setPadding(new Insets(10,0,0,20));

        // get the corrrect answer
        String correctAnswer = triviaQnA[CORRECT_ANSWER];

        // copy all the wrong answers for this question (up to maxTriviaAnswers)
        String answers[] = new String[maxTriviaAnswers];
        int numberOfAnswers = 0;
        for(int i = 2; i < triviaQnA.length ; i++){
            String answer = triviaQnA[i];
            if(answer == null || answer.equals("")){
                break;
            }
            answers[i - 2] = answer;
            numberOfAnswers++;
        }

        // create a Border pane to put the question and answers in
        BorderPane questionPane = new BorderPane();
        questionPane.setTop(centerLabelInHBox(lblQuestion, new Insets(0,20,0,20)));

        // create check boxes for each answer and fill it in
        VBox answerPane = new VBox(5);
        for(int i = 0; i < numberOfAnswers; i++) {
            // generate a random number from 0 to 3
            questionIndex = triviaRnd.nextInt(numberOfAnswers);
            String nextAnswer = answers[questionIndex];
            while(nextAnswer.equals("")){
                // find an answer that hasn't already been picked
                questionIndex = triviaRnd.nextInt(numberOfAnswers);
                nextAnswer = answers[questionIndex];
            }
            // remember that we used this answer
            answers[questionIndex] = "";

            CheckBox nextCheckBox = new CheckBox(nextAnswer);
            nextCheckBox.setSelected(false);
            if(setCheckBoxEvents) {
                if (nextAnswer.equals(correctAnswer)) {

                    nextCheckBox.setOnAction(e -> rightAnswer(triviaQnA, triviaIndex));
                } else {
                    nextCheckBox.setOnAction(e -> wrongAnswer(triviaQnA, triviaIndex));
                }
            } else {
                nextCheckBox.setOnAction(e -> nextCheckBox.setSelected(false));
                questionPane.getTop().requestFocus();
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

        if(showStatement){
            VBox vbxAnswerPane = new VBox();
            vbxAnswerPane.getChildren().addAll(answerPaneCentered, createStatementPane(triviaIndex));
            questionPane.setCenter(vbxAnswerPane);
        } else{
            questionPane.setCenter(answerPaneCentered);
        }

        return questionPane;
    }

    private static VBox createStatementPane(int triviaIndex){
        String triviaStatement = formatStatement(triviaQuestions.get(triviaIndex));
        Label lblStatement = new Label(triviaStatement);
        HBox hbxStatement = centerLabelInHBox(lblStatement, new Insets(5,0,10,0));
        hbxStatement.setPadding(new Insets(5,0,10,0));

        VBox vbStatement = new VBox();
        vbStatement.getChildren().add( hbxStatement);

        return vbStatement;
    }

    private static String formatStatement(String[] triviaQnA){
        // get rid of brackets [] and the text between them
        String triviaStatement = triviaQnA[QUESTION];

        // put and escaped commas back
        triviaStatement = triviaStatement.replace("%COMMA%",",");


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
            String statementPhrase = triviaQnA[CORRECT_ANSWER];  // AKA correct answer
            triviaStatement = triviaStatement.replace(questionPhrase,statementPhrase);
        }

        // get rid of < > leaving the text in between them
        triviaStatement = triviaStatement.replaceAll("<","");
        triviaStatement = triviaStatement.replaceAll(">","");

        return triviaStatement;
    }

    private static int nextTriviaIndex(){
        int numberOfTriviaQuestions = triviaQuestions.size();
        String[] nextTriviaQnA = new String[6];
        int nextTriviaIndex;
        boolean questionAlreadyAsked = false;
        int questionsRemaining = numberOfTriviaQuestions + 1;
        do{
            nextTriviaIndex = (int)Math.floor((Math.random() * numberOfTriviaQuestions));
            nextTriviaQnA = triviaQuestions.get(nextTriviaIndex);
            questionsRemaining--;
            String alreadyAsked = nextTriviaQnA[ALREADY_ASKED];
            questionAlreadyAsked = alreadyAsked.equals("R") || alreadyAsked.equals("W");
        }while(questionAlreadyAsked && questionsRemaining > 0);

        if(questionsRemaining == 0) {
            // we've already asked all the trivia questions
            recycleTriviaQuestions();

            // this shouldn't go into an infinite loop
            return nextTriviaIndex();
        } else {
            // we found one that hasn't been used yet - mark it as used
            nextTriviaQnA[ALREADY_ASKED] = "Y";
            triviaQuestions.set(nextTriviaIndex, nextTriviaQnA);
        }

        return nextTriviaIndex;
    }

    private static void recycleTriviaQuestions(){
        // we've already asked all the questions so lets ask them again
        System.out.println("recycling the trivia questions");
        for(int nQuestion = 0; nQuestion < triviaQuestions.size(); nQuestion++) {
            String nextTriviaQnA[] = triviaQuestions.get(nQuestion);
                nextTriviaQnA[ALREADY_ASKED] = "N";
                triviaQuestions.set(nQuestion, nextTriviaQnA);
                saveTriviaQuestions();
        }

    }

    private static void rightAnswer(String[] triviaQnA, int questionIndex ){
        rightAnswers++;
        gradeAnswer("R", triviaQnA, questionIndex);
    }

    private static void showCorrectAnswer(String[] triviaQnA){
        // create a dialog to display the question on
        Stage correctAnswerStage = new Stage(StageStyle.UNDECORATED);
        correctAnswerStage.initModality(Modality.APPLICATION_MODAL);
        correctAnswerStage.setAlwaysOnTop(true);
        correctAnswerStage.setMinWidth(250);

        Label lblHeader = new Label("Sorry, that is NOT correct");
        lblHeader.setFont(Font.font("Verdana", BOLD, 12));
        HBox hbxHeader = centerLabelInHBox(lblHeader);
        hbxHeader.setPadding(new Insets(20,0,10,0));

        Label lblCorrectAnswer = new Label(formatStatement(triviaQnA));
        HBox hbxCorrrectAnswer = centerLabelInHBox(lblCorrectAnswer);
        hbxCorrrectAnswer.setPadding(new Insets(20,20,10,20));

        Button btnOK = new Button("OK");
        HBox hbxOK = GIO.centerButtonInHBox(btnOK);
        btnOK.setOnAction(e -> correctAnswerStage.close());

        // display the correct answer and wait for player to click OK button
        Scene correctAnswerScene = new Scene(hbxOK);

        Platform.runLater(() -> {
            // hide the answer dialog to avoid confussion
            answerStage.hide();
        });

        correctAnswerStage.setScene(correctAnswerScene);
        correctAnswerStage.showAndWait();
    }

    private static void wrongAnswer(String[] triviaQnA, int questionIndex ){
        wrongAnswers++;
        showCorrectAnswer(triviaQnA);
        gradeAnswer("W", triviaQnA, questionIndex);
        System.out.println("wrongAnswer - questionIndex = "+questionIndex);
    }

    private static void gradeAnswer(String rightWrong, String[] triviaQnA, int questionIndex ){
        triviaQnA[ALREADY_ASKED] = rightWrong;
        triviaQuestions.set(questionIndex, triviaQnA);
        closeQuestionStage();
    }
}
