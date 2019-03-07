package com.jetbrains;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import com.jetbrains.WumpusEquates;

import static com.jetbrains.WumpusEquates.OUTER_WALL;

public class RoomView {
    Group group;
    double  scaleFactor;
    Color floorColor;
    Point topLefts[] = new Point[2];
    boolean showRoomNumber;

    public RoomView(Group group, boolean showRoomNumber, double scaleFactor, Color floorColor, Point topLeft){
        this.group = group;
        this.scaleFactor = scaleFactor;
        this.showRoomNumber = showRoomNumber;
        this.floorColor = floorColor;
        topLefts[OUTER_WALL] = topLeft;
    }
}
