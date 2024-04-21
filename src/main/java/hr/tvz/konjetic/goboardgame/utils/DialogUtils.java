package hr.tvz.konjetic.goboardgame.utils;

import javafx.scene.control.Alert;

public class DialogUtils {

    public static void showSuccessDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("The action was successful!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
