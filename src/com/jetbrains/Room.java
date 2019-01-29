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
    private static final int INNER_WALL = 0;
    private static final int OUTER_WALL = 1;

    private static final int WALL_XY = 0;
    private static final int TUNNEL_XY = 1;

    private static final int X = 0;
    private static final int Y = 1;
    private static final int X0 = 0;
    private static final int Y0 = 0;
    private static final int X1 = 1;
    private static final int Y1 = 1;
    private static final int X2 = 2;
    private static final int Y2 = 2;

    private static final int point0 = 0;
    private static final int point1 = 1;
    private static final int point2 = 2;
    private static final int point3 = 3;
    private static final int point4 = 4;
    private static final int point5 = 5;
    private static final int point6 = 6;

    public Wall walls[] = {new Wall(), new Wall(), new Wall(), new Wall(), new Wall(), new Wall()};
    public int roomNumber;
    public boolean hasPit;
    public boolean hasBat;
    public boolean hasBeenVisited;
    public Stage gameStage;
    // hexagon[inner/outer][points][x/y];
    double[][][]hexagon = new double[2][7][2];


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

        initRoomXY();
        initRoomHexagon(hexagon[INNER_WALL],100,40,100, 140,180); 
    }

    private void initRoomHexagon(double[][] hexagon,int hexLeft, int hexTop, int deltaX1, int deltaX2, int deltaY){
        final double x0 = hexLeft;
        final double x1 = x0 + .4 * deltaX1;
        final double x2 = x0 + .6 * deltaX1;
        final double x3 = x0 + deltaX1;
        final double x4 = x3 + .4 * deltaX2;
        final double x5 = x3 + .6 * deltaX2;
        final double x6 = x3 + deltaX2;
        final double x7 = x6 + .4 * deltaX1;
        final double x8 = x6 + .6 * deltaX1;
        final double x9 = x6 + deltaX1;

        final double y0 = hexTop;
        final double y1 = y0 + .4 * deltaY;
        final double y2 = y0 + .6 * deltaY;
        final double y3 = y0 + deltaY;
        final double y4 = y3 + .4 * deltaY;
        final double y5 = y3 + .6 * deltaY;
        final double y6 = y3 + deltaY;

        double wallLeft = hexLeft + deltaX1;
        double wallRight = wallLeft + deltaX2;
        double wallTop = hexTop;
        double wallBottom = hexTop;

        double point0X = hexLeft + deltaX1;
        double point0Y = hexTop;
        hexagon[0][X] = point0X;
        hexagon[0][Y] = point0Y;

        double point1X = point0X + deltaX2;
        double point1Y = point0Y;
        hexagon[1][X] = point1X;
        hexagon[1][Y] = point1Y;

        double point2X = point1X + deltaX1;
        double point2Y = hexTop + deltaY;
        hexagon[2][X] = point2X;
        hexagon[2][Y] = point2Y;

        double point3Y = point2Y + deltaY;
        hexagon[3][X] = point1X;
        hexagon[3][Y] = point3Y;

        hexagon[4][X] = point0X;
        hexagon[4][Y] = point3Y;

        hexagon[5][X] = hexLeft;
        hexagon[5][Y] = point2Y;

        hexagon[6][X] = point0X;
        hexagon[6][Y] = point0Y;
    }

    // define the wall xy array indexes
    private void initRoomXY(){
        final int xStart = 100;
        final int yStart = 40;
        final int delta1 = 100;
        final int delta2 = 180;
        final int delta3 = 160;

        int wallLeft = xStart + delta1;
        int wallRight = wallLeft + delta2;
        int wallTop = yStart;
        int wallBottom = yStart;
        
        Wall wall = walls[0];
        double[][] innerWall = wall.xy[INNER_WALL];

        innerWall[WALL_XY][X] = wallLeft;
        innerWall[WALL_XY][Y] = wallTop;
        innerWall[WALL_XY][X2] = wallRight;
        innerWall[WALL_XY][Y2] = wallTop;

        innerWall[TUNNEL_XY][X] = tunnelLeft(innerWall);
        innerWall[TUNNEL_XY][Y] = wallTop;
        innerWall[TUNNEL_XY][X2] = tunnelRight(innerWall);
        innerWall[TUNNEL_XY][Y2] = wallTop;

/*
        double tunnelLeft = wallLeft + .4 * delta2;
        double tunnelRight = wallLeft + .6 * delta2;
        innerWall[TUNNEL_XY][X1] = tunnelLeft;
        innerWall[TUNNEL_XY][Y1] = wallTop;
        innerWall[TUNNEL_XY][X2] = tunnelRight;
        innerWall[TUNNEL_XY][Y2] = wallTop;
*/
    }
    
    private double tunnelLeft(double[][] wall){
        double wallLeft = wall[WALL_XY][X];
        double wallRight = wall[WALL_XY][Y];
        double wallWidth = wallRight - wallLeft;
        double tunnelWidth = .25 * wallWidth;
        return wallLeft + wallWidth/2 - tunnelWidth/2;
    }

    private double tunnelRight(double[][] wall){
        double wallLeft = wall[WALL_XY][X];
        double wallRight = wall[WALL_XY][Y];
        double wallWidth = wallRight - wallLeft;
        double tunnelWidth = .25 * wallWidth;
        return wallLeft + wallWidth/2 - tunnelWidth/2;
    }

    public void draw(Stage gameStage){

        Group group = new Group();
        drawPolyWalls(group, hexagon[INNER_WALL]);
        //drawWalls(group,100, 40, 100, 180, 160);
        //drawWalls(group,85, 30, 110, 190, 170);

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

    private void drawPolyWalls(Group group, double[][] hexPoints){
        Polygon hexagon = new Polygon();
        hexagon.getPoints().addAll(new Double[]{
                hexPoints[point0][X], hexPoints[point0][Y],
                hexPoints[point1][X], hexPoints[point1][Y],
                hexPoints[point2][X], hexPoints[point2][Y],
                hexPoints[point3][X], hexPoints[point3][Y],
                hexPoints[point4][X], hexPoints[point4][Y],
                hexPoints[point5][X], hexPoints[point5][Y],
                hexPoints[point6][X], hexPoints[point6][Y],
        });
        group.getChildren().addAll(hexagon);
    }

    private void drawWalls(Group group, int xStart, int yStart, int delta1, int delta2, int delta3){

        double[][] hexWall = hexagon[INNER_WALL];
        drawPolyWalls(group, hexWall);
        //double lineLeft = innerWall[WALL_XY][X1];
        //double lineTop = innerWall[WALL_XY][Y1];
        //double lineRight = innerWall[WALL_XY][X2];
        //double lineBottom = innerWall[WALL_XY][Y2];
        //group.getChildren().add(new Line(lineLeft,lineTop,lineRight,lineBottom));
        

        /*
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

        double wallLeft = xStart + delta1;
        double wallRight = wallLeft + delta2;
        double wallTop = yStart;
        double wallBottom = yStart;

        group.getChildren().add(new Line(wallLeft, wallTop, wallRight, wallBottom));

        double tunnelLeft = wallLeft + .4 * delta2;
        double tunnelRight = wallLeft + .6 * delta2;

        Line tunnelLine = new Line(tunnelLeft, wallTop, tunnelRight, wallBottom);
        tunnelLine.setStroke(Color.WHITE);
        tunnelLine.setStrokeWidth(2);
        group.getChildren().add(tunnelLine);

//        group.getChildren().add(new Line(x3,y0,x4,y0));
//        group.getChildren().add(new Line(x4,y0,x5,y0));
//        group.getChildren().add(new Line(x5,y0,x6,y0));

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
