package com.jetbrains;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.Random;

import static com.jetbrains.GIO.message;
import static com.jetbrains.Game.gio;
import static com.jetbrains.Player.numberOfArrows;
import static com.jetbrains.Player.numberOfCoins;

public final class Store {
    //////////////////////////////
    // Store Instance variables //
    //////////////////////////////

    // making the pane static is probably not a good idea
    // needs further investigation
    static HBox pane;

    // can be used during testing to specify the type of secret you want
    // otherwise it's random
    static int preferedSecretIndex = -1;

    ///////////////////
    // Store methods //
    ///////////////////

    public static void addMoreCoins(){
        int coinDelta = gio.getHowMany(true, 10, -10,10,"Add/Subtract how many coins:");
        numberOfCoins.set(numberOfCoins.get() + coinDelta);
        Game.stats.update();
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
        boolean answeredMaxCorrect = Trivia.ask(maxQuestions, maxCorrect, "To buy a secret");
        if(answeredMaxCorrect){
            //UNDONE - add more types of secrets
            Random rnd = new Random();
            int numberOfTypesOfSecrets = 4;
            int secretIndex;
            if(preferedSecretIndex == -1) {
                secretIndex = rnd.nextInt(numberOfTypesOfSecrets);
            } else {
                secretIndex = preferedSecretIndex;
            }
            switch (secretIndex){
                case 0:
                    // tell the player where a bat is
                    int batRoomNumber = Cave.bats.roomWithBatInIt();
                    if (batRoomNumber == 0) {
                        message("There are no bats in the cave");
                    } else {
                        message("There is a bat in room " + batRoomNumber);
                    }
                    break;
                case 1:
                    // tell the player where the Wumpus is
                    message("The Wumpus is in room " + Wumpus.roomNumber);
                    break;
                case 2:
                    // tell the player where a pit is
                    message("There is a pit in room " + Cave.pits.roomWithPitInIt());
                    break;
                case 3:
                    // tell the player which room they are in
                    message("You are in room " + Cave.currentRoom);
                    break;
                case 4:
                    // tell the player which room leads to the shortest path to the Wumpus
                    int shortestPath = Cave.rooms[Player.roomNumber].distaceFromWumpus;
                    int shortestPathRoomNumber = 0;
                    Wall[] roomWalls = Cave.rooms[Player.roomNumber].walls;
                    for(int wallNumber = 0; wallNumber < 6; wallNumber ++){
                        Wall wall = roomWalls[wallNumber];
                        if(wall.hasTunne1){
                            Room adjacentRoom = Cave.rooms[wall.adjacentRoom];
                            if(adjacentRoom.distaceFromWumpus < shortestPath){
                                shortestPath = adjacentRoom.distaceFromWumpus;
                                shortestPathRoomNumber = adjacentRoom.roomNumber;
                            }
                        }
                    }
                    if(Cave.rooms[Cave.currentRoom].distaceFromWumpus == 1){
                        // the Wumpus is in the next room
                        message("The Wumpus is in room " + shortestPathRoomNumber);
                    } else {
                        message("Going to Room " + shortestPathRoomNumber + " will put you closer to the Wumpus");
                    }
                    break;

                    default:Debug.error("invalid secretIndex = " + secretIndex);
            }
        } else {
            message("BUMMER - no secret for you");
        }
    }

    public static HBox purchasePane(){
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

        pane = new HBox();
        pane.getChildren().addAll(spacer1, btnBuyArrows, spacer2, btnBuySecret, spacer3);
        pane.setPadding(new Insets(0,0,10,0));
        return pane;
    }

    /////////////////////////////
    // Store class constructor //
    /////////////////////////////
    private Store(){
        // ensure that Store is a public singleton
    };

}
