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
import static com.jetbrains.Debug.message;
import static com.jetbrains.Game.*;
import static com.jetbrains.Main.*;
import static com.jetbrains.Player.numberOfCoins;
import static com.jetbrains.Store.*;
import static java.awt.SystemColor.infoText;
import static javafx.scene.input.KeyCode.ENTER;

//
// NOTE there should only be one GIO object
//
class GIO {
    //
    // GIO  constants
    //
    final int BP_TOP_HEIGHT =  20;

    //
    // GIO static variables
    //
    static Group gioGroup = new Group();
    static GridPane statusGridPane;
    static Scene gioScene;
    static String newCaveName;
    static boolean cavePickerDblClicked;
    static RoomView singleRoomView;
    //
    // GIO methods
    //
    int getDesiredRoomNumber(){
        int desiredRoom = getHowMany(1, 30,"Which room would you like to go to?");
        return desiredRoom;
    }

    int getHowMany(int minAmt, int maxAmt, String text) {
        int howMany = 0;
        Dialog dialog = new Dialog<>();
        dialog.setResizable(false);

        Label howManyLabel = new Label(text);
        TextField howManyField = new TextField();
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
            if(keyCode == ENTER){
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
                try {
                    howMany = Integer.parseInt(howManyField.getText());
                    if(howMany < minAmt|| howMany > maxAmt) {
                        message("Please pick a number betweeen " + minAmt + " and " + maxAmt);
                    } else {
                        invalidNumber = false;
                    }
                } catch (Exception e) {
                    message("Please pick a number betweeen " + minAmt + " and " + maxAmt);
                }
            } else if(result.get() == ButtonType.CANCEL) {
                howMany = 0;
                invalidNumber = false;
            }
        }
        return howMany;
    }

    void gotoRoom(int roomNumber, String msgPrefix) {
        // you get a coin every time you enter a room - for any reason
        stats.addCoin();
        gioGroup.getChildren().removeAll(gioGroup.getChildren());
        Cave.currentRoom = 0;
        stats.txtInfo.setText(msgPrefix + " room " + roomNumber);
        stats.txtHint.setText("");
        Trivia.txtTrivia.setText(Trivia.randomStatement());

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
        numberPane.setPadding(new Insets(0,0,10,0));

        Player.roomNumber = roomNumber;

        cave.currentRoom = roomNumber;

        gioGroup.getChildren().add(numberPane);

        Game.cave.rooms[roomNumber].draw(singleRoomView);

        BorderPane.setAlignment(gioGroup, Pos.CENTER);

        bpGame.setCenter(gioGroup);

        Game.stage.setScene(gioScene);

        Game.stage.show();

        if (roomNumber == Wumpus.roomNumber) {
            boolean success = Trivia.ask(5,3, "You have found the Wumpus");
            if(success){
                message("You have angered the Wumpus and it has fled");
                Wumpus.moveToRandomRoom();
                Cave.rooms[currentRoom].draw(gio.singleRoomView);
            } else {
                Game.youLost("The Wumpus ate you");
                Player.isDead = true;
                Cave.rooms[currentRoom].draw(gio.singleRoomView);
            }
        //} else if (Cave.rooms[roomNumber].hasBat()) {
        } else if (Cave.bats.isInRoom(roomNumber)) {
            relocatePlayer();
            Cave.bats.bats[0].relocateBatFrom(currentRoom);
        } else if (Cave.rooms[roomNumber].hasPit) {
            // FEATURE would be nice if the player spun and vanished
            String askMsgPrefix = "You have fallen into a pit.  To get out";
            if(Trivia.ask(3,2, askMsgPrefix)){
                gotoRoom(initialRoom, "You have been returned to");
            } else {
                Game.youLost("You fell into a bottomless pit");
            }
        }
        // any interesting objects nearby
        if(Wumpus.inAdjacentRoom() && bats.inAdjacentRoom() && pits.inAdjacentRoom()) {
            updateHint("Wings flapping nearby with foul odor in the air and cool breeze");
        } else if(Wumpus.inAdjacentRoom() && bats.inAdjacentRoom()){
            updateHint("Wings flapping nearby and there is a foul odor in the air");
        } else if(pits.inAdjacentRoom()){
            updateHint("Pit - I feel a draft");
        } else if(Wumpus.inAdjacentRoom()){
            updateHint("Wumpus - I smell a Wumpus");
        } else if (bats.inAdjacentRoom()) {
            updateHint("Bat - Bat nearby ");
        }
        else{
            updateHint("");
        }

        Cave.currentRoom = roomNumber;

        // refresh the cave map if it is open
        if(CaveMap.isOpen){
            CaveMap.refresh();
        }
    }

    void updateInfo(String infoText) {
        stats.txtInfo.setText(infoText);
    }

    void updateHint(String hintText) {
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

    static String cavePicker() {
        // ADVANCED - need to implement scroll bars
        if(useDefaults){return "cave1";}
        Dialog dialog = new Dialog<>();
        dialog.setHeaderText("Pick a cave");
        dialog.setResizable(false);
        dialog.setHeight(250);

        List<String> caveNames = new ArrayList<String>();

        File directory = new File("src/");

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

    //
    // GIO constructor
    //
    GIO(String caveName) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Game.quit();
            }
        });

        // set up the room views
        Color floorColor = Color.LIGHTGRAY;
        double roomTop = 40;
        double roomLeft = 0;
        Point topLeft = new Point(roomLeft, roomTop);
        double scaleFactor = 1.0;
        boolean showRoomNumber = false;
        singleRoomView = new RoomView(gioGroup, showRoomNumber, scaleFactor, floorColor, topLeft);

        tfRoomNumber.setAlignment(Pos.CENTER);
        // set up the sceeen display area
        gioScene = new Scene(bpGame, 400, 400);

        stage.setWidth(600);
        stage.setHeight(700);

        // display the Wumpus image
        addSplash(bpGame, "src/wumpus.png");
        Game.stage.setScene(gioScene);
        Game.stage.show();

        // build the menu bar

        //-- Build the Game menu and its menu items --//
        Menu gameMenu = new Menu("Game");

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

        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Game.quit();
            }
        });

        gameMenu.getItems().addAll(newGameMenuItem, replayMenuItem, quitMenuItem);

        //-- create the Store menu and it's menu items --//
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

        MenuItem moreCoinsMenuItem = new MenuItem("add coins");
        moreCoinsMenuItem.setOnAction(e -> {
            Store.addMoreCoins();
        });

        MenuItem changeScoreMenuItem = new MenuItem("change score");
        changeScoreMenuItem.setOnAction(e -> {
            Game.stats.modifyScore();
        });

        MenuItem gotoRoomMenuItem = new MenuItem("Go To Room");
        gotoRoomMenuItem.setOnAction(e -> {
            int desiredRoomNumber = gio.getDesiredRoomNumber();
            gio.gotoRoom(desiredRoomNumber, "You have been moved to ");
        });

        MenuItem ignoreTriviaMenuItem = new MenuItem("ignore trivia questions");
        ignoreTriviaMenuItem.setOnAction(e -> {
            Trivia.ignoreTrivia = !Trivia.ignoreTrivia;
        });


        MenuItem showCaveMapMenuItem = new MenuItem("show cave map");
        showCaveMapMenuItem.setOnAction(e -> {
           CaveMap.draw();
        });

        Menu debugMenu = new Menu("Cheat");
        debugMenu.getItems().addAll(moreCoinsMenuItem, gotoRoomMenuItem, changeScoreMenuItem, ignoreTriviaMenuItem, showCaveMapMenuItem);

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
        Image bowImage = bow.imageView.getImage();

        // convert the bow image Top and Left coordinates to bpGame relative
        Point2D bowTopLeft = Game.bow.imageView.localToScene(bow.imageView.getX(), bow.imageView.getY());

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

    //
    // javafx controls
    //
    TextField tfRoomNumber = new TextField();

    BorderPane bpGame = new BorderPane();

    Label lblCaveName;

    //
    // GIO helper functions
    //

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
