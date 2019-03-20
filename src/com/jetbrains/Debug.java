package com.jetbrains;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

//
// NOTE there should only be one Debug object
//
class Debug {
    //
    // Debug static variables
    //
    static boolean isEnabled = false;

    static void log(String msg){
        if(isEnabled){
            System.out.println(msg);
        }
    }

    //
    // Debug methods
    //

    static void error(String alertMsg){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.ERROR,alertMsg, ButtonType.OK);
        alert.showAndWait();
    }

    static void warning(String alertMsg){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.WARNING,alertMsg, ButtonType.OK);
        alert.showAndWait();
    }

}
