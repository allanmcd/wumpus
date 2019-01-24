package com.jetbrains;

public class Debug {
    public static boolean isEnabled = false;

    public static void log(String msg){
        if(isEnabled){
            System.out.println(msg);
        }
    }
}
