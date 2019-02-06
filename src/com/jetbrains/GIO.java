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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.jetbrains.Game.*;
import static com.jetbrains.Main.game;
import static com.jetbrains.Main.primaryStage;

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
    static Group gioGroup;
    static Label lblInfo;
    static Scene gioScene;
    static String newCaveName;
    static boolean cavePickerDblClicked;

    //
    // GIO methods
    //
    void gotoRoom(int roomNumber) {
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        Label lblRoomNumber = new Label("Room " + roomNumber);
        lblRoomNumber.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        GridPane.setHalignment(lblRoomNumber, HPos.CENTER);
        gridpane.add(lblRoomNumber, 15, 0);

        Label lblBlankLine = new Label("");
        lblBlankLine.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        gridpane.add(lblBlankLine, 23, 1);

        lblInfo = new Label();
        lblInfo.setFont(Font.font("Verdana", 18));
        gridpane.add(lblInfo, 0, 35);

        game.player.roomNumber = roomNumber;

        gioGroup = new Group();

        gioGroup.getChildren().add(gridpane);
        Game.cave.rooms[roomNumber].draw();

        BorderPane.setAlignment(gioGroup, Pos.CENTER);

        bpGame.setCenter(gioGroup);

        Game.gameStage.setScene(gioScene);

        Game.gameStage.show();

        if (roomNumber == Cave.wumpus.roomNumber) {
            Game.youLost("The Wumpus got you");
        } else if (Cave.rooms[roomNumber].hasBat()) {
            relocatePlayer();
        } else if (Cave.rooms[roomNumber].hasPit) {
            // FEATURE would be nice if the player spun and vanished
            Game.youLost("You fell into a bottomless pit");
        }
    }

    void updateInfo(String infoText) {
        gio.lblInfo.setText(infoText);
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
        Dialog dialog = new Dialog<>();
        dialog.setHeaderText("Pick a cave");
        dialog.setResizable(false);
        dialog.setHeight(250);

        List<String> caveNames = new ArrayList<String>();

        File directory = new File("src/");

        // get all the files from the "src" directory
        File[] fList = directory.listFiles();
        for(File file :fList){
            if (file.isFile() && fileExtension(file).equals("cave")) {
                String nextCaveName =file.getName();
                // strip off the extension
                nextCaveName = nextCaveName.substring(0,nextCaveName.length()- ".cave".length());
                // add cave name to list of other cave names
                caveNames.add(nextCaveName);
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

        Optional<String> result = dialog.showAndWait();
        String dialogResult = result.toString();

        // the emply if and if else statements are used to try to clarify the users action
        // a logical expression using negatives would be harder to understand
        if(cavePickerDblClicked){
            // Player double clicked - newCaveName was updated - don't need to do anything
        } else if(dialogResult.contains("OK_DONE")){
            // Player clicked on OK button - newCaveName was updated (though it may be "") - don't need to do anything
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
        tfRoomNumber.setAlignment(Pos.CENTER_RIGHT);
        // set up the sceeen display area
        gioScene = new Scene(bpGame, 400, 250);

        gameStage.setWidth(600);
        gameStage.setHeight(600);

        // display the wumpus image as the splash screen
        addSplash(bpGame, "src/wumpus.png");
        Game.gameStage.setScene(gioScene);
        Game.gameStage.show();

        // build the menu bar
        //Build the first menu.
        Menu gameMenu = new Menu("Wumpus");

        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                String caveName = cavePicker();
                Main.newGame(caveName);
            }
        });

        MenuItem replayMenuItem = new MenuItem("Replay Current Game");
        replayMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                game = new Game(Cave.name, primaryStage);
                if(game.cave.valid){game.play();};
            }
        });

        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });

        gameMenu.getItems().addAll(newGameMenuItem, replayMenuItem, quitMenuItem);

        MenuBar gameMenuBar = new MenuBar();
        gameMenuBar.getMenus().add(gameMenu);

        // create the cave name label for the TOP area of the Borderpane
        Label lblCaveName = new Label(caveName);
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

        // have to examine all mouse clicks because clicking on the transparent part of
        // the mow does not generate a mouseclick event for the bow image
        gioScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent evt) {
                // mouse coordinates are relative to bpGame
                double mouseX = evt.getX();
                double mouseY = evt.getY();

                // need the bow Image to get the width and height
                Image bowImage= bow.imageView.getImage();

                // convert the bow image Top and Left coordinates to bpGame relative
                Point2D bowTopLeft = game.bow.imageView.localToScene(bow.imageView.getX(), bow.imageView.getY());

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
                        bow.fired = true;
                        System.out.println("arrow notched");
                        evt.consume();
                    }
                }
            }
        });
    }

    //
    // javafx controls
    //
    TextField tfRoomNumber = new TextField();

    BorderPane bpGame = new BorderPane();

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
        gio.gotoRoom(nextEmptyRoom());
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

            if (nextEmptyRoomNumber == Cave.wumpus.roomNumber) {
                // not empty - wumpus in room
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
            // UNDONE should probably add code to display "e"
            Debug.error(("could not add \"" + imageFileName + "\" to the splash page"));
        }
    }
}
