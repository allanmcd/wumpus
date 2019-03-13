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
import static com.jetbrains.Game.gio;
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
    static boolean isOpen = false;
    //
    // Cavemap private Instance variables
    //
    private static boolean initialized;
    private static Stage caveMapStage;

    //
    // CaveMap methods
    //

    static void draw(){
        Group caveMapGroup = new Group();
        double walls[][][] = new double [2][7][2];
        int mapTop = 40;
        int mapLeft = 30;
        double scaleFactor = .2;
        Point topLeft = new Point(mapLeft,mapTop);
        boolean showRoomNumber = true;
        boolean showPlayer = false;

        RoomView smallView = new RoomView(caveMapGroup,showRoomNumber,scaleFactor, Color.ALICEBLUE, topLeft);
        smallView.pitImageOpacity = .4;

        // draw a ring of 'shadow' rooms around the actual array of cave rooms
        smallView.showPlayer = false;

        drawSmallRoom(caveMapGroup, walls, 30, smallView);

        smallView.topLefts[OUTER_WALL].x = mapLeft + smallDeltaX1 + smallDeltaX2 + 2;
        smallView.topLefts[OUTER_WALL].y = mapTop - smallDeltaY ;
        drawSmallRow(caveMapGroup,walls,25,6,smallView);

        smallView.topLefts[OUTER_WALL].x = mapLeft;
        smallView.topLefts[OUTER_WALL].y = mapTop + 2* smallDeltaY ;
        drawSmallColumn(caveMapGroup,walls,6,5,smallView);

        smallView.topLefts[OUTER_WALL].x = mapLeft + smallDeltaX1 + smallDeltaX2 + 2;
        smallView.topLefts[OUTER_WALL].y -= smallDeltaY ;
        drawSmallRow(caveMapGroup,walls,1,6,smallView);

        smallView.topLefts[OUTER_WALL].x = mapLeft + 7 * smallDeltaX1 + 7* smallDeltaX2 + 3;
        smallView.topLefts[OUTER_WALL].y = mapTop +  smallView.wallDeltas[OUTER_WALL][Y1];
        drawSmallColumn(caveMapGroup,walls,1,5,smallView);

        drawSmallRoom(caveMapGroup, walls, 1, smallView);

        // now draw the small version of the cave rooms
        smallView.showPlayer = true;

        smallView.floorColor = Color.LIGHTGOLDENRODYELLOW;
        int rowLeft = mapLeft + smallDeltaX1 + smallDeltaX2 + 3;
        smallView.topLefts[OUTER_WALL].y = mapTop + smallDeltaY;
        int firstRoomNumber = 1;
        int numberOfRooms = 6;
        for(int i = 1; i <= 5; i++) {
            smallView.topLefts[OUTER_WALL].x = rowLeft;
            drawSmallRow(caveMapGroup,walls,firstRoomNumber,6,smallView);
            smallView.topLefts[OUTER_WALL].y +=  smallDeltaY;
            firstRoomNumber += numberOfRooms;
        }

        Scene caveMapScene = new Scene(caveMapGroup);

        if(isOpen == false) {
            caveMapStage = new Stage(StageStyle.UTILITY);
            caveMapStage.setOnCloseRequest(e -> {
                isOpen = false;
            });
            caveMapStage.setTitle("Cave Map");
            caveMapStage.setWidth(525);
            caveMapStage.setHeight(520);
        }

        caveMapStage.setScene(caveMapScene);
        caveMapStage.setX(Main.primaryStage.getX() + Main.primaryStage.getWidth() + 50);
        caveMapStage.setY(Main.primaryStage.getY() - 50);
        caveMapStage.show();
        isOpen = true;
    }

    static void refresh(){
        // turns out we don't need to do anything other than draw
        // but that may change if we want to reduce flicker
        if(isOpen) {
            draw();
        }
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

        roomView.currentRoom = roomNumber;
        room.draw(roomView);
        int roomLeft = (int) roomView.topLefts[OUTER_WALL].x;
        int roomTop = (int) roomView.topLefts[OUTER_WALL].y;
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
