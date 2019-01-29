package com.jetbrains;

//import com.sun.prism.paint.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
//import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Color.*;
import javafx.scene.shape.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;


import javafx.stage.Stage;

public class Room {
    public Wall walls[] = {new Wall(), new Wall(), new Wall(), new Wall(), new Wall(), new Wall()};
    public int roomNumber;
    public boolean hasPit;
    public boolean hasBat;
    public boolean hasBeenVisited;
    public Stage gameStage;

    public Room (int newRoomNumber,
                 int adjacentRoom1,
                 int adjacentRoom2,
                 int adjacentRoom3,
                 int adjacentRoom4,
                 int adjacentRoom5,
                 int adjacentRoom6
                 )
    {
        roomNumber  = newRoomNumber;
        walls[0].adjacentRoom = adjacentRoom1;
        walls[1].adjacentRoom = adjacentRoom2;
        walls[2].adjacentRoom = adjacentRoom3;
        walls[3].adjacentRoom = adjacentRoom4;
        walls[4].adjacentRoom = adjacentRoom5;
        walls[5].adjacentRoom = adjacentRoom6;
    }


    public void draw(Stage gameStage){

        Group group = new Group();
        drawWalls(group,100, 40, 100, 180, 160);
        drawWalls(group,85, 30, 110, 190, 170);

        Button btn = new Button();
        btn.setLayoutX(100);
        btn.setLayoutY(40);
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
//        Rectangle r = new Rectangle(1,298,200,200);
        Rectangle r = new Rectangle(299.0, 249.0, Color.TRANSPARENT);
        r.setStroke(Color.BLACK);

        gameStage.setScene(new Scene(group, 300, 250));

        gameStage.show();

    }

    private void drawWalls(Group group, int xStart, int yStart, int delta1, int delta2, int delta3){
        final double x0 = xStart;
        final double x1 = x0 + .4 * delta1;
        final double x2 = x0 + .6 * delta1;
        final double x3 = x0 + delta1;
        final double x4 = x3 + .4 * delta2;
        final double x5 = x3 + .6 * delta2;
        final double x6 = x3 + delta2;
        final double x7 = x6 + .4 * delta1;
        final double x8 = x6 + .6 * delta1;
        final double x9 = x6 + delta1;

        final double y0 = yStart;
        final double y1 = y0 + .4 * delta3;
        final double y2 = y0 + .6 * delta3;
        final double y3 = y0 + delta3;
        final double y4 = y3 + .4 * delta3;
        final double y5 = y3 + .6 * delta3;
        final double y6 = y3 + delta3;

        group.getChildren().add(new Line(x3,y0,x4,y0));
//        group.getChildren().add(new Line(x4,y0,x5,y0));
        group.getChildren().add(new Line(x5,y0,x6,y0));

        group.getChildren().add( new Line(x6,y0,x7,y1));
//        group.getChildren().add( new Line(x7,y1,x8,y2));
        group.getChildren().add( new Line(x8,y2,x9,y3));


        group.getChildren().add( new Line(x9,y3,x8,y4));
//        group.getChildren().add( new Line(x8,y4,x7,y5));
        group.getChildren().add( new Line(x7,y5,x6,y6));

        group.getChildren().add( new Line(x6,y6,x5,y6));
//        group.getChildren().add( new Line(x5,y6,x4,y6));
        group.getChildren().add( new Line(x4,y6,x3,y6));

        group.getChildren().add( new Line(x3,y6,x2,y5));
//        group.getChildren().add( new Line(x2,y5,x1,y4));
        group.getChildren().add( new Line(x1,y4,x0,y3));

        group.getChildren().add( new Line(x0,y3,x1,y2));
//        group.getChildren().add( new Line(x1,y2,x2,y1));
        group.getChildren().add( new Line(x2,y1,x3,y0));
/*
        group.getChildren().add( new Line(x0,y0,x0,y6));
        group.getChildren().add( new Line(x1,y0,x1,y6));
        group.getChildren().add( new Line(x2,y0,x2,y6));
        group.getChildren().add( new Line(x3,y0,x3,y6));
        group.getChildren().add( new Line(x4,y0,x4,y6));
        group.getChildren().add( new Line(x5,y0,x5,y6));
        group.getChildren().add( new Line(x6,y0,x6,y6));
        group.getChildren().add( new Line(x7,y0,x7,y6));
        group.getChildren().add( new Line(x8,y0,x8,y6));
        group.getChildren().add( new Line(x9,y0,x9,y6));
*/
//        group.getChildren().add(new Line(x1,y0,x2,y0));
//        group.getChildren().add( new Line(x2,y0,x3,y1));
//        group.getChildren().add( new Line(x3,y1,x2,y2));
//        group.getChildren().add( new Line(x1,y2,x2,y2));
//        group.getChildren().add( new Line(x1,y2,x0,y1));
//        group.getChildren().add( new Line(x0,y1,x1,y0));

    }
}
