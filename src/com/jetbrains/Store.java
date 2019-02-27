package com.jetbrains;

public final class Store {
    //
    // Store methods
    //
    public static boolean purchaseArrows(){
        int maxQuestions = 3;
        int maxCorrect = 2;
        boolean passed;
        passed = Trivia.ask(maxQuestions, maxCorrect);
        return passed;
    }
    //
    // Store class constructor
    //
    private Store(){
        // ensure that Store is a public singleton
    };
}
