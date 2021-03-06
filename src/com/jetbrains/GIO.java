//
// Wumpus Graphical Interface Object
//
package com.jetbrains;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;

import java.io.File;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static com.jetbrains.Cave.*;
import static com.jetbrains.CaveMap.isOpen;
import static com.jetbrains.Game.*;
import static com.jetbrains.Main.*;
import static com.jetbrains.Player.numberOfCoins;
import static com.jetbrains.Store.*;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.text.FontWeight.BOLD;

//
// NOTE there should only be one GIO object
//
class GIO {
    ////////////////////
    // GIO  constants //
    ////////////////////

    final int BP_TOP_HEIGHT = 20;

    //////////////////////////
    // GIO static variables //
    //////////////////////////

    static Group gioGroup = new Group();
    static GridPane statusGridPane;
    static Scene gioScene;
    static String newCaveName;
    static boolean cavePickerDblClicked;
    static RoomView singleRoomView;

    /////////////////
    // GIO methods //
    /////////////////

    static HBox centerLabelInHBox(Label label, Insets insets){
        HBox hbox = centerLabelInHBox(label);
        //hbox.setPadding(new Insets(20,0,10,0));
        hbox.setPadding(insets);
        return hbox;
    }

    static HBox centerLabelInHBox(Label label){
        final Pane spacerLeft = new Pane();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        spacerLeft.setMinSize(10, 1);

        final Pane spacerRight = new Pane();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        spacerRight.setMinSize(10, 1);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(spacerLeft, label, spacerRight);

        return hbox;
    }

    static HBox centerButtonInHBox(Button button){
        // create an "OK" button at the bottom of the dialog
        //Button btnOK = new Button();
        //btnOK.setText(text);

        final Pane spacerLeft = new Pane();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        spacerLeft.setMinSize(10, 1);

        final Pane spacerRight = new Pane();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        spacerRight.setMinSize(10, 1);

        HBox hbxButton = new HBox();
        hbxButton.getChildren().addAll(spacerLeft, button, spacerRight);
        hbxButton.setPadding(new Insets(10,0,20,0));

        return hbxButton;
    }

    int getDesiredRoomNumber() {
        int desiredRoom = getHowMany(false,0,1, 30, "Which room would you like to go to?");
        return desiredRoom;
    }

    int getHowMany(boolean showDefault, int defaultAmt, int minAmt, int maxAmt, String text) {
        int howMany = 0;
        Dialog dialog = new Dialog<>();
        dialog.setResizable(false);

        Label howManyLabel = new Label(text);
        TextField howManyField = new TextField();
        if(showDefault){
            howManyField.setPromptText(Integer.toString(defaultAmt));
            howManyField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background,-40%);");
        }
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 35, 20, 35));
        grid.add(howManyLabel, 1, 1);
        grid.add(howManyField, 2, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        okButton.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if (keyCode == ENTER) {
                dialog.setResult(ButtonType.OK);
            }
        });

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        boolean invalidNumber = true;
        while (invalidNumber) {
            Platform.runLater(() -> {
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                Window window = dialog.getDialogPane().getScene().getWindow();
                window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
                window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
                howManyField.requestFocus();
            });
            howManyField.setText("");

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String howManyText = new String();
                howManyText = howManyField.getText();
                if(howManyText.length() > 0) {
                    // looks like Player typed in some text
                    try {
                        howMany = Integer.parseInt(howManyField.getText());
                        if (howMany < minAmt || howMany > maxAmt) {
                            message("Please pick a number betweeen " + minAmt + " and " + maxAmt);
                        } else {
                            invalidNumber = false;
                        }
                    } catch (Exception e) {
                        message("Please pick a number betweeen " + minAmt + " and " + maxAmt);
                    }
                } else{
                    // Player didn't enter a value but clicked OK
                    // use the prompt value as the default
                    howMany = Integer.parseInt(howManyField.getPromptText());
                    invalidNumber = false;
                }
            } else if (result.get() == ButtonType.CANCEL) {
                howMany = 0;
                invalidNumber = false;
            }
        }
        return howMany;
    }

    void gotoRoom(int roomNumber, String msgPrefix) {
        Cave.currentRoom = roomNumber;
        Player.roomNumber = roomNumber;
        // you get a coin every time you enter a room - for any reason
        stats.addCoin();
        gioGroup.getChildren().removeAll(gioGroup.getChildren());
        stats.txtInfo.setText(msgPrefix + " room " + roomNumber);
        stats.txtHint.setText("");
        Trivia.txtTrivia.setText(Trivia.randomStatement());

        Wumpus.updateDistanceFrom();

        stats.anotherTurn();

        HBox numberPane = new HBox();

        Label lblRoomNumber = new Label("Room " + roomNumber);
        lblRoomNumber.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        lblRoomNumber.setAlignment(Pos.CENTER);

        final Pane spacer1 = new Pane();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        spacer1.setMinSize(150, 1);

        final Pane spacer2 = new Pane();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        spacer2.setMinSize(150, 1);

        numberPane.getChildren().addAll(spacer1, lblRoomNumber, spacer2);
        numberPane.setPadding(new Insets(0, 0, 10, 0));

        gioGroup.getChildren().add(numberPane);

        singleRoomView.currentRoomNumber = roomNumber;
        Game.cave.rooms[roomNumber].draw(singleRoomView);

        BorderPane.setAlignment(gioGroup, Pos.CENTER);

        bpGame.setCenter(gioGroup);

        Game.stage.setScene(gioScene);

        Game.stage.show();
        currentStage = Game.stage;

        if (roomNumber == Wumpus.roomNumber) {
                // update the cave map if it is open
                CaveMap.refresh();
            boolean success = Trivia.ask(5, 3, "You have found the Wumpus");
            if (success) {
                Wumpus.flee();
                // update the cave map if it is open
                CaveMap.refresh();
                Wumpus.updateDistanceFromText();

                message("You have angered the Wumpus and it has fled");

                // you bested the Wumpus now check to see if the room has a pit
                if (Cave.rooms[roomNumber].hasPit) {
                    Pits.fellIn();
                }
            } else {
                Game.youLost("The Wumpus ate you");
                Player.isDead = true;
            }
            Cave.rooms[currentRoom].draw(gio.singleRoomView);
        } else if(Cave.bats.isInRoom(roomNumber)){
            relocatePlayer();
            Cave.bats.bats[0].relocateBatFrom(currentRoom);
        } else if(Cave.rooms[roomNumber].hasPit){
            Pits.fellIn();
        }

        // any interesting objects nearby
        updateHint();

        Cave.currentRoom = roomNumber;

        // refresh the cave map if it is open
        if(isOpen){
            CaveMap.refresh();
        }
    }

    static void message(String alertMsg){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.NONE,alertMsg, ButtonType.OK);
        alert.showAndWait();
    }

    static void message(String alertMsg, String titleMsg){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.NONE,alertMsg, ButtonType.OK);
        alert.setTitle(titleMsg);
        alert.setHeaderText("");
        //alert.setGraphic(null);
        alert.showAndWait();
    }

    static void message(String alertMsg, String titleMsg, String headerMsg){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.NONE,alertMsg, ButtonType.OK);
        alert.setHeaderText(headerMsg);
        alert.setTitle(titleMsg);
        alert.setGraphic(null);
        alert.showAndWait();
    }

    void updateHint() {
        // any interesting objects nearby
        if (Wumpus.inAdjacentRoom() && bats.inAdjacentRoom() && pits.inAdjacentRoom()) {
            updateHintText("Wings flapping nearby with foul odor in the air and cool breeze");
        } else if (Wumpus.inAdjacentRoom() && bats.inAdjacentRoom()) {
            updateHintText("Wings flapping nearby and there is a foul odor in the air");
        } else if (Wumpus.inAdjacentRoom() && pits.inAdjacentRoom()) {
            updateHintText("I feel a draft and there is a foul odor in the air");
        } else if (pits.inAdjacentRoom()) {
            updateHintText("Pit - I feel a draft");
        } else if (Wumpus.inAdjacentRoom()) {
            updateHintText("Wumpus - I smell a Wumpus");
        } else if (bats.inAdjacentRoom()) {
            updateHintText("Bat - Bat nearby ");
        } else {
            updateHintText("");
        }
    }

    void updateInfo(String infoText) {
        stats.txtInfo.setText(infoText);
    }

    void updateHintText(String hintText) {
        stats.txtHint.setText(hintText);
    }

    void showDialog(String dlgTitle, String dlgMsg) {
        Label msgLabel = new Label(dlgMsg);

        Dialog dialog = new Dialog<>();
        dialog.setHeaderText(dlgTitle);
        dialog.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 35, 20, 35));
        grid.add(msgLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2 + 100);
        });

        dialog.showAndWait();
    }

    static void addEnterHint(BorderPane bp) {
        addEnterHint(bp, "Press the ENTER key to play");
    }

    static void addEnterHint(BorderPane bp, String enterText){
        Label lblEnterToPlay = new Label(enterText);
        lblEnterToPlay.setFont(Font.font("Verdana", BOLD, 18));

        Label lblEscToQuit = new Label("Press the ESC key to quit");
        lblEscToQuit.setFont(Font.font("Verdana", BOLD, 18));

        VBox splashBottomPanel = new VBox();
        splashBottomPanel.setAlignment(Pos.CENTER);
        splashBottomPanel.getChildren().addAll(lblEnterToPlay,lblEscToQuit);

        bp.setBottom(splashBottomPanel);
    }

    static String cavePicker() {
        // ADVANCED - need to implement scroll bars
        if(useDefaults){return "cave3";}
        Dialog dialog = new Dialog<>();
        dialog.setHeaderText("Pick a cave");
        dialog.setResizable(false);
        dialog.setHeight(250);

        List<String> caveNames = new ArrayList<String>();

        File directory = new File("Caves/");

        // get all the files from the "src" directory
        File[] fList = directory.listFiles();
        String firstCave="";
        for(File file :fList){
            if (file.isFile() && fileExtension(file).equals("cave")) {
                String nextCaveName =file.getName();
                // strip off the extension
                nextCaveName = nextCaveName.substring(0,nextCaveName.length()- ".cave".length());
                // add cave name to list of other cave names
                caveNames.add(nextCaveName);
                if(firstCave.equals("")){
                    // UNDONe - figure out how to pre-select the 1st cave name
                }
            }
        }

        ObservableList<String> names = FXCollections.observableArrayList(caveNames);
        ListView<String> caveView = new ListView<String>(names);
        caveView.setStyle("-fx-border-color: black;");
        caveView.setLayoutY(200);
        cavePickerDblClicked = false;
        caveView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                newCaveName = caveView.getSelectionModel().getSelectedItem();
                if (click.getClickCount() == 2) {
                    cavePickerDblClicked = true;
                    //Use ListView's getSelected Item
                    dialog.close();
                }
            }
        });

        dialog.getDialogPane().setContent(caveView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2 + 100);
        });

        caveView.getSelectionModel().selectFirst();

        Optional<String> result = dialog.showAndWait();
        String dialogResult = result.toString();

        // the emply if and if else statements are used to try to clarify the users action
        // a logical expression using negatives would be harder to understand
        if(cavePickerDblClicked){
            // Player double clicked - newCaveName was updated - don't need to do anything
        } else if(dialogResult.contains("OK_DONE")){
            // Player clicked on OK button - newCaveName was updated (though it may be "") - don't need to do anything
            newCaveName = caveView.getSelectionModel().getSelectedItem();
        } else {
            // Player clicked on CANCEL button or closed the dialog - no cave name to return
            newCaveName = "";
        }
        return newCaveName;
    }

    /////////////////////
    // GIO constructor //
    /////////////////////

    GIO(String caveName) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Game.exit();
            }
        });

        // set up the room views
        Color floorColor = Color.LIGHTGRAY;
        double roomTop = 60;
        double roomLeft = 0;
        Point topLeft = new Point(roomLeft, roomTop);
        double scaleFactor = 1.0;
        boolean showRoomNumber = false;
        singleRoomView = new RoomView(gioGroup, showRoomNumber, scaleFactor, floorColor, topLeft);
        singleRoomView.isBorderRoom = false;
        singleRoomView.isForCaveMap = false;
        singleRoomView.bowImageView = new ImageView();
        singleRoomView.tunnelColor = Color.DARKGRAY;


        tfRoomNumber.setAlignment(Pos.CENTER);
        // set up the sceeen display area
        gioScene = new Scene(bpGame, 400, 400);

        stage.setWidth(600);
        stage.setHeight(700);

        // display the Wumpus image
        addSplash(bpGame, "src/wumpus.png");
        Game.stage.setScene(gioScene);
        Game.stage.show();
        currentStage = stage;

        // build the menu bar

        //-- Build the Game menu and its menu items --//
        Menu gameMenu = new Menu("Game");

        MenuItem newPlayerMenuItem = new MenuItem("New Player");
        newPlayerMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Game.signIn();
                Stats.loadHighScores();
                SplashScreen.playGame(name);
            }
        });

        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                SplashScreen.newGame();
            }
        });

        MenuItem replayMenuItem = new MenuItem("Replay Current Game");
        if (caveName.contentEquals("")) {
            replayMenuItem.setDisable(true);
        }
        replayMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                SplashScreen.replayGame();
            }
        });

        MenuItem showHighScores = new MenuItem("Show High Scores");
        showHighScores.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Stats.showHighScores();
            }
        });

        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Game.quit();
            }
        });

        gameMenu.getItems().addAll(newPlayerMenuItem, newGameMenuItem, replayMenuItem, showHighScores, quitMenuItem);

        MenuItem moreArrrowsMenuItem = new MenuItem("2 More Arrows");
        moreArrrowsMenuItem.setOnAction(e -> {
            buyArrows();
        });

        MenuItem buySecretMenuItem = new MenuItem("Buy A Secret");
        buySecretMenuItem.setOnAction(e -> {
            buySecret();
        });

        //--- create the Debug menu and its menu items ---//
        Menu storeMenu = new Menu("Store");
        storeMenu.getItems().addAll(buySecretMenuItem);

        //-- create the Store menu and it's menu items --//
        CheckMenuItem useDefaultsMenuItem = new CheckMenuItem("Use default values");
        useDefaultsMenuItem.setSelected(useDefaults);
        useDefaultsMenuItem.setOnAction(e -> {
            useDefaults = ! useDefaults;
        });

        MenuItem winTheGameMenuItem = new MenuItem("Win the game");
        winTheGameMenuItem.setOnAction(e -> {
            Game.youWon();
        });

        MenuItem loseTheGameMenuItem = new MenuItem("Lose the game");
        loseTheGameMenuItem.setOnAction(e -> {
            Game.youLost("better luck next time");
        });

        MenuItem answerTriviaMenuItem = new MenuItem("answer trivia questions");
        answerTriviaMenuItem.setOnAction(e -> {
            Trivia.ask(5,3,"");
        });

        MenuItem moreCoinsMenuItem = new MenuItem("add coins");
        moreCoinsMenuItem.setOnAction(e -> {
            Store.addMoreCoins();
        });

        Menu setPreferedSecretMenu = new Menu("set Prefered Type of Secret");
        CheckMenuItem cmiRandom = new CheckMenuItem("Random");
        CheckMenuItem cmiBatLocations = new CheckMenuItem("Bat Locations");
        CheckMenuItem cmiWumpusLocation = new CheckMenuItem("Wumpus Location");
        CheckMenuItem cmiPitLocations = new CheckMenuItem("Pit Locations");
        CheckMenuItem cmiYourLocation = new CheckMenuItem("Your Current Location");
        CheckMenuItem cmiShortestPath = new CheckMenuItem("Shortest Path To Wumpus");
        setPreferedSecretMenu.getItems().addAll(cmiRandom, cmiBatLocations, cmiWumpusLocation,
                                                cmiPitLocations, cmiShortestPath);

        setPreferedSecretMenu.setOnAction(e -> {
            if(e.getTarget().equals(cmiRandom)){
                preferedSecretIndex = -1;
            } else if(e.getTarget().equals(cmiBatLocations)){
                preferedSecretIndex = 0;
            } else if(e.getTarget().equals(cmiWumpusLocation)){
                preferedSecretIndex = 1;
            }else if(e.getTarget().equals(cmiPitLocations)){
                preferedSecretIndex = 2;
            }else if(e.getTarget().equals(cmiYourLocation)){
                preferedSecretIndex = 3;
            }else if(e.getTarget().equals(cmiShortestPath)){
                preferedSecretIndex = 4;
            } else{
                Debug.error("Invallid Set Secret Preference target returned");
            }
        });

        MenuItem changeScoreMenuItem = new MenuItem("Modify the Score");
        changeScoreMenuItem.setOnAction(e -> {
            Game.stats.modifyScore();
        });

        MenuItem gotoRoomMenuItem = new MenuItem("Move Player To Room");
        gotoRoomMenuItem.setOnAction(e -> {
            int desiredRoomNumber = gio.getDesiredRoomNumber();
            gio.gotoRoom(desiredRoomNumber, "You have been moved to ");
        });

        MenuItem moveWumpusToRoom = new MenuItem("Move Wumpus To Room");
        moveWumpusToRoom.setOnAction(e -> {
            Wumpus.roomNumber = getHowMany(false,0,1,30,"move Wumpus to which room");
            CaveMap.refresh();
        });

        MenuItem updateDistanceFrom = new MenuItem("Update the distance from the Wumpus in each room");
        updateDistanceFrom.setOnAction(e -> {
            Wumpus.updateDistanceFrom();
            CaveMap.refresh();
        });

        MenuItem ignoreTriviaMenuItem = new MenuItem("Bypass Trivia Questions");
        ignoreTriviaMenuItem.setOnAction(e -> {
            Trivia.bypassTrivia = true;
        });

        MenuItem displayTriviaMenuItem = new MenuItem("Display Trivia Questions");
        displayTriviaMenuItem.setOnAction(e -> {
            Trivia.displayTriviaQuestions();
        });

        MenuItem showCaveMapMenuItem = new MenuItem("Show Cave Map");
        showCaveMapMenuItem.setOnAction(e -> {
           CaveMap.draw();
        });

        Menu debugMenu = new Menu("Testing");
        debugMenu.getItems().addAll(useDefaultsMenuItem, winTheGameMenuItem, loseTheGameMenuItem,
                                    moreCoinsMenuItem,  setPreferedSecretMenu,
                                    gotoRoomMenuItem, moveWumpusToRoom, updateDistanceFrom, changeScoreMenuItem,
                                    answerTriviaMenuItem, displayTriviaMenuItem, ignoreTriviaMenuItem,
                                    showCaveMapMenuItem);

        MenuBar gameMenuBar = new MenuBar();
        gameMenuBar.getMenus().addAll(gameMenu, storeMenu, debugMenu);

        // create the cave name label for the TOP area of the Borderpane
        lblCaveName = new Label(caveName);
        lblCaveName.setFont(Font.font("Verdana", FontWeight.BOLD, 24));

        TilePane tpTop = new TilePane();
        tpTop.setPrefRows(2);
        tpTop.setVgap(5);

        // make each tile the entire width of the game window
        // effectively making it a vertical list of single horizontal tiles
        double stageWidth = primaryStage.getWidth();
        tpTop.setPrefTileWidth(stageWidth);
        tpTop.setPrefTileHeight(BP_TOP_HEIGHT);
        tpTop.getChildren().add(gameMenuBar);
        tpTop.getChildren().add(lblCaveName);
        bpGame.setTop(tpTop);

        if(caveName != null && caveName.length() > 0 ) {
            VBox bottomPane = new VBox();
            bottomPane.getChildren().addAll(stats.pane(),Store.purchasePane(), Trivia.pane());
            bpGame.setBottom(bottomPane);

            // have to examine all mouse clicks because clicking on the transparent part of
            // the bow does not generate a mouseclick event for the bow image
            gioScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent evt) {
                    handleMouseEvent(evt);
                }
            });
        }
    }

    private void handleMouseEvent(MouseEvent evt){
        // mouse coordinates are relative to bpGame
        double mouseX = evt.getX();
        double mouseY = evt.getY();

        // need the bow Image to get the width and height
        Image bowImage = singleRoomView.bowImageView.getImage();

        // convert the bow image Top and Left coordinates to bpGame relative
        //Point2D bowTopLeft = Game.bow.imageView.localToScene(bow.imageView.getX(), bow.imageView.getY());
        ImageView bowImageView = singleRoomView.bowImageView;
        Point2D bowTopLeft = bowImageView.localToScene(bowImageView.getX(), bowImageView.getY());

        // calculate the BorderPane relative values for the bow Top, Left, Bottom & Right
        // UNDONE try to figure out a way to GET the ImageView margin
        double apparentImageViewTopMargin = 5;
        double bowTop = bowTopLeft.getY() + apparentImageViewTopMargin;
        double bowLeft = bowTopLeft.getX();
        double bowBottom = bowTopLeft.getY() + bowImage.getHeight();
        double bowRight = bowTopLeft.getX() + bowImage.getWidth();

        // see if the mouse click occured inside the bow image
        if (mouseX > bowLeft && mouseX < bowRight) {
            if (mouseY > bowTop && mouseY < bowBottom) {
                bow.drawn = true;
                bow.draw(singleRoomView);
                System.out.println("the bow is drawn - an arrow is nocked");
                evt.consume();
            }
        }
    }

    /////////////////////
    // javafx controls //
    /////////////////////

    TextField tfRoomNumber = new TextField();

    BorderPane bpGame = new BorderPane();

    Label lblCaveName;

    //////////////////////////
    // GIO helper functions //
    //////////////////////////

    static private String fileExtension(File file){
        String extension = "";
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            extension = fileName.substring(fileName.lastIndexOf(".")+1);
        }
        return extension;
    }

    private void relocatePlayer() {
        showDialog("A bat has captured you", "It will transport you to another room");
        gio.gotoRoom(nextEmptyRoom(), "You have been transported to");
        // counteract the +1 for entering a room
        numberOfCoins.set(numberOfCoins.get() - 1);
    }

    private int nextEmptyRoom() {
        Random random = new Random();
        int nextEmptyRoomNumber = random.nextInt(29) + 1;

        boolean generateAnotherRoomNumber;
        do {
            // assume the current room number is OK
            generateAnotherRoomNumber = false;

            if (Cave.rooms[nextEmptyRoomNumber].hasBat()) {
                // not empty - bat in room
                generateAnotherRoomNumber = true;
            }

            if (Cave.rooms[nextEmptyRoomNumber].hasPit) {
                // not empty - pit in room
                generateAnotherRoomNumber = true;
            }

            if (nextEmptyRoomNumber == Wumpus.roomNumber) {
                // not empty - Wumpus in room
                generateAnotherRoomNumber = true;
            }

            if (generateAnotherRoomNumber) {
                // generate another room number to test
                nextEmptyRoomNumber = random.nextInt(29) + 1;
            }
        } while (generateAnotherRoomNumber);

        return nextEmptyRoomNumber;
    }

    void addSplash(BorderPane bpGame, String imageFileName) {
        try
        {
            Image splashImage = new Image(new FileInputStream(imageFileName));
            ImageView splashImageView = new ImageView(splashImage);
            splashImageView.setPreserveRatio(true);
            splashImageView.setFitWidth(300);
            bpGame.setCenter(splashImageView);
        }
        catch (FileNotFoundException e)
        {
            Debug.error(("could not add \"" + imageFileName + "\" to the splash page"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
