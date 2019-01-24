package com.jetbrains;

public class Main {

    public static void main(String[] args) {
        Game game = new Game(1);
        if(game.cave.valid){
            System.out.println("Cave " + game.cave.number + " loaded");
        }
        else{
            System.out.println("Cave NOT loaded");
        }
    }
}
