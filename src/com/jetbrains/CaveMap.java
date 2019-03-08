package com.jetbrains;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.jetbrains.Cave.rooms;
import static com.jetbrains.WumpusEquates.INNER_WALL;
import static com.jetbrains.WumpusEquates.OUTER_WALL;
import static com.jetbrains.WumpusEquates.*;

public final class CaveMap {

    //
    // CaveMap "equates"
    private static final int smallDeltaY = 32;
    private static final int smallDeltaX1 = 20;
    private static final int smallDeltaX2 = 36;

    //
    // Cavemap Instance variables
    //
    private static boolean initialized;

    //
    // CaveMap methods
    //

    static void draw(){
        Group group = new Group();
        double walls[][][] = new double [2][7][2];
        int mapTop = 40;
        int mapLeft = 30;
        double scaleFactor = .2;
        Point topLeft = new Point(mapLeft,mapTop);
        RoomView smallView = new RoomView(group,true,scaleFactor, Color.ALICEBLUE, topLeft);

        int top = mapTop + smallDeltaY;
        int left = mapLeft;
        drawSmallRoom(group, walls, 30, smallView);

        //top = mapTop;
        //left = mapLeft + smallDeltaX1 + smallDeltaX2 -6;
        smallView.topLefts[OUTER_WALL].x = mapLeft + smallDeltaX1 + smallDeltaX2 + 2;
        smallView.topLefts[OUTER_WALL].y = mapTop - smallDeltaY ;
        drawSmallRow(group,walls,25,6,smallView);

        top = mapTop + 3 * smallDeltaY - 6;
        left = mapLeft;
        smallView.topLefts[OUTER_WALL].x = mapLeft;
        smallView.topLefts[OUTER_WALL].y = mapTop + 2* smallDeltaY ;
        drawSmallColumn(group,walls,6,5,smallView);

        smallView.topLefts[OUTER_WALL].x = mapLeft + smallDeltaX1 + smallDeltaX2 + 2;
        smallView.topLefts[OUTER_WALL].y -= smallDeltaY ;
        drawSmallRow(group,walls,1,6,smallView);

        smallView.topLefts[OUTER_WALL].x = mapLeft + 7 * smallDeltaX1 + 7* smallDeltaX2 + 3;
        smallView.topLefts[OUTER_WALL].y = mapTop +  smallView.wallDeltas[OUTER_WALL][Y1];
        drawSmallColumn(group,walls,1,5,smallView);

        //smallView.topLefts[OUTER_WALL].x = mapLeft +  smallDeltaX1 + smallDeltaX2;
        //smallView.topLefts[OUTER_WALL].y += smallDeltaY ;
        drawSmallRoom(group, walls, 1, smallView);

/*
        top += 9 * smallDeltaY;
        drawSmallRoom(group, walls, Color.ALICEBLUE, 1, smallView);

        smallView.floorColor = Color.LIGHTGOLDENRODYELLOW;
        top = mapTop + 2 * smallDeltaY - 6;
        left = mapLeft + smallDeltaX1 + smallDeltaX2 - 6;
        int firstRoomNumber = 1;
        int numberOfRooms = 6;
        for(int i = 1; i <= 5; i++) {
            drawSmallRow(group, walls, Color.LIGHTGOLDENRODYELLOW, firstRoomNumber, numberOfRooms, smallView);
            top += 2 * smallDeltaY - 6;
            firstRoomNumber += numberOfRooms;
        }

        top = mapTop + 2 * smallDeltaY - 3;
        left = mapLeft + 4 * (smallDeltaX2 + 2 * smallDeltaX1) + 2* smallDeltaX2 - smallDeltaX1 + 1;
        drawSmallColumn(group,walls,Color.ALICEBLUE,1,5,smallView);
*/
        Scene smallScene = new Scene(group);

        Stage smallStage = new Stage(StageStyle.UTILITY);
        smallStage.setTitle("Cave Map");
        smallStage.setWidth(525);
        smallStage.setHeight(520);

        smallStage.setScene(smallScene);
        smallStage.show();

    }

    //////////////////////////////
    // CaveMap helper functions //
    //////////////////////////////

    private static void drawSmallRow(Group group, double walls[][][], int firstRoomNumber, int numberOfRooms, RoomView roomView){
        RoomView rowRoomView = new RoomView(roomView);
        int firstTop = (int)rowRoomView.topLefts[OUTER_WALL].y;
        int lastRoomNumber = firstRoomNumber + numberOfRooms - 1;
        for(int roomNumber = firstRoomNumber; roomNumber <= lastRoomNumber; roomNumber++){
            boolean even = roomNumber % 2 == 0;
            rowRoomView.topLefts[OUTER_WALL].y = (roomNumber % 2 == 0)? firstTop + smallDeltaY:firstTop;
            drawSmallRoom(group, walls, roomNumber, roomView);
            rowRoomView.topLefts[OUTER_WALL].x += smallDeltaX1 + smallDeltaX2;
        }
    }

    private static void drawSmallColumn(Group group, double walls[][][], int firstRoomNumber, int numberOfRooms, RoomView roomView){
        RoomView columnRoomView = new RoomView(roomView);
        int roomTop = (int)roomView.topLefts[OUTER_WALL].y;
        int lastRoomNumber = firstRoomNumber + 6 * (numberOfRooms - 1);
        for(int roomNumber = firstRoomNumber; roomNumber <= lastRoomNumber; roomNumber += 6){
            drawSmallRoom(group, walls, roomNumber, roomView);
            roomView.topLefts[OUTER_WALL].y += 2 * smallDeltaY;
        }
    }

    private static void drawSmallRoom(Group group, double walls[][][], int roomNumber, RoomView roomView){
        //initWallPoints(roomView);
        Room room = rooms[roomNumber];

        //initRoomHexagon(roomView, OUTER_WALL,walls);
        //initRoomHexagon(roomView, INNER_WALL,walls);

        room.draw(roomView);
        int roomLeft = (int) roomView.topLefts[OUTER_WALL].x;
        int roomTop = (int) roomView.topLefts[OUTER_WALL].y;
        /*
        roomView.topLefts[INNER_WALL].x = roomLeft + 5;
        int roomTop = (int)roomView.topLefts[OUTER_WALL].y;
        roomView.topLefts[INNER_WALL].y = roomTop + 3;
        //initRoomHexagon(OUTER_WALL,walls,smallDeltaX1, smallDeltaX2,smallDeltaY);
        //initRoomHexagon(INNER_WALL,walls,smallDeltaX1 - 3, smallDeltaX2 -4,smallDeltaY - 4);

        initRoomHexagon(roomView, OUTER_WALL,walls);
        initRoomHexagon(roomView, INNER_WALL,walls);

        drawHexagonWalls(group, OUTER_WALL, walls, Color.BLACK);
        drawHexagonWalls(group, INNER_WALL, walls, fillColor);
        */
        Label lblRoomNumber = new Label(Integer.toString(roomNumber));
        //lblRoomNumber.setFont(Font.font("Verdana", EXTRA_LIGHT, 36));
        lblRoomNumber.setStyle("-fx-text-fill: rgba(50, 100, 100, 0.5); -fx-font-size: 36px;");
        lblRoomNumber.setAlignment(Pos.CENTER);
        VBox roomNumberPane = new VBox();
        roomNumberPane.setPrefSize(50, 50);
        roomNumberPane.getChildren().add(lblRoomNumber);
        roomNumberPane.setLayoutX(roomLeft + smallDeltaX1 - 8);
        roomNumberPane.setLayoutY(roomTop - 8 + smallDeltaY / 2);
        roomNumberPane.setAlignment(Pos.CENTER);
        //roomNumberPane.setStyle("-fx-border-color: black");

        group.getChildren().add(roomNumberPane);
    }

    //
    // CaveMap constructor
    //
    private CaveMap(){
        // make RoomViews a singleton - simulate a static top level class
    }
}
