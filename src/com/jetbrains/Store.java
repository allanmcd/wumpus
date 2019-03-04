package com.jetbrains;

import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.Random;

import static com.jetbrains.Debug.message;
import static com.jetbrains.Player.numberOfArrows;
import static com.jetbrains.Player.numberOfCoins;

public final class Store {
    ////////////////
    // Store methods
    ////////////////

    public static void addMoreCoins(int howMany){
        numberOfCoins.set(numberOfCoins.get() + howMany);
    }

    public static void buyArrows(){
        int maxQuestions = 3;
        int maxCorrect = 2;
        if(Trivia.ask(maxQuestions, maxCorrect)){
            numberOfArrows.set(numberOfArrows.get() +2);
            message("Congratulations - you now have two more arrows");
        } else {
            message("BUMMER - no additional arrows for you");
        }
    }

    public static void buySecret(){

        int maxQuestions = 3;
        int maxCorrect = 2;
        if(Trivia.ask(maxQuestions, maxCorrect)){
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
}
