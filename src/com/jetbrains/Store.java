package com.jetbrains;

import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.Optional;
import java.util.Random;

import static com.jetbrains.Debug.message;
import static com.jetbrains.Main.useDefaults;
import static com.jetbrains.Player.numberOfArrows;
import static com.jetbrains.Player.numberOfCoins;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;

public final class Store {
    ////////////////
    // Store methods
    ////////////////

    public static void addMoreCoins(){
        numberOfCoins.set(numberOfCoins.get() + getHowManyCoins());
    }

    public static void buyArrows(){
        int maxQuestions = 3;
        int maxCorrect = 2;
        if(Trivia.ask(maxQuestions, maxCorrect, "To buy 2 arrows")){
            numberOfArrows.set(numberOfArrows.get() +2);
            message("Congratulations - you now have two more arrows");
        } else {
            message("BUMMER - no additional arrows for you");
        }
    }

    public static void buySecret(){

        int maxQuestions = 3;
        int maxCorrect = 2;
        if(Trivia.ask(maxQuestions, maxCorrect, "To buy a secret")){
            //UNDONE - give some sort of secret
            Random rnd = new Random();
            boolean anotherSecret = true;
            int numberOfTypesOfSecrets = 4;
            int secretIndex = rnd.nextInt(numberOfTypesOfSecrets);
            while(anotherSecret){
                switch (secretIndex){
                    case 0:
                        // tell the player where a bat is
                        int batRoomNumber = Cave.bats.roomWithBatInIt();
                        int batRoomIndex = rnd.nextInt(2);
                        if (batRoomIndex == 0) {
                        } else {
                            message("There is a bat in room " + batRoomNumber);
                        }
                        break;
                    case 1:
                        // tell the player where the Wumpus is
                        message("The Wumpus is in room " + Cave.wumpus.roomNumber);
                        anotherSecret = false;
                        break;
                    case 2:
                        // tell the player where a pit is
                        message("There is a pit in room " + Cave.pits.roomWithPitInIt());
                        anotherSecret = false;
                        break;
                    case 3:
                        // tell the player which room they are in
                        message("You are in room " + Cave.currentRoom);
                        anotherSecret = false;
                        break;

                        default:Debug.error("invalid secretIndex = " + secretIndex);
                }
            }
        } else {
            message("BUMMER - no secret for you");
        }
    }

    public static HBox purchasePane(){
        HBox pane = new HBox();

        Button btnBuyArrows = new Button("Buy Arrows");
        btnBuyArrows.setOnAction(e -> buyArrows());

        Button btnBuySecret = new Button("Buy A Secret");
        btnBuySecret.setAlignment(Pos.CENTER_RIGHT);
        btnBuySecret.setOnAction(e -> buySecret());

        final Pane spacer1 = new Pane();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        spacer1.setMinSize(10, 1);

        final Pane spacer2 = new Pane();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        spacer2.setMinSize(10, 1);

        final Pane spacer3 = new Pane();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        spacer3.setMinSize(10, 1);

        pane.getChildren().addAll(spacer1, btnBuyArrows, spacer2, btnBuySecret, spacer3);
        pane.setPadding(new Insets(0,0,10,0));
        return pane;
    }

    ///////////////////////////
    // Store class constructor
    //////////////////////////
    private Store(){
        // ensure that Store is a public singleton
    };

    //////////////////////////
    // Store helper functions
    /////////////////////////
    public static int getHowManyCoins() {
        int numberOfCoins = 0;
        Dialog dialog = new Dialog<>();
        //dialog.setTitle("Sign In");
        //dialog.setHeaderText("How many coins would you like");
        dialog.setResizable(false);

        Label userNameLabel = new Label("How many coins would you like:");
        TextField userNameField = new TextField();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 35, 20, 35));
        grid.add(userNameLabel, 1, 1);
        grid.add(userNameField, 2, 1);
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

        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
            userNameField.requestFocus();
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                numberOfCoins = Integer.parseInt(userNameField.getText());
            } catch (Exception e) {
                numberOfCoins = 0;
            }
        }
        return numberOfCoins;
    }
}
