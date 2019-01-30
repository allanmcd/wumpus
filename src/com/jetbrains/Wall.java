package com.jetbrains;

public class Wall {
    int adjacentRoom;
    boolean hasTunne1;
    int number;
    // [inner,outer] [wall,tunnel] [x1,y1,x2,y2]
    double[][][] xy = new double[2][2][4] ;
    // [inner/outer][point]
    Object[] tunnel = new Object[4];

}
