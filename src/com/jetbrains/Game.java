package com.jetbrains;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import javafx.stage.Stage;


public class Game {
    int caveNumber = 1;

    Stage primaryStage;

    Cave cave = new Cave( caveNumber);

    public Game(int caveNumber, Stage primaryStage){
        caveNumber = caveNumber;
        this.primaryStage = primaryStage;
    }

    public void Play(Cave cave){
        cave.rooms[1].draw();
    }

    public void play(){
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Find The Wumpus");

/*
        VBox box = new VBox();
        Scene scene = new Scene(box,300,250);
        scene.setFill(null);
*/

        Line line = new Line(10.0,10.0,280.0, 240.0);

        //box.getChildren().add(line);


        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(line);
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));


        //primaryStage.setScene(new Scene(root, 300, 275));
        //primaryStage.setScene(scene);
        primaryStage.show();

    }
}

