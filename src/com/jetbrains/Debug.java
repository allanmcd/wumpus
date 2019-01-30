package com.jetbrains;

class Debug {
    static boolean isEnabled = false;

    static void log(String msg){
        if(isEnabled){
            System.out.println(msg);
        }
    }
}
