package hr.tvz.konjetic.goboardgame.utils;

import hr.tvz.konjetic.goboardgame.model.Player;
import hr.tvz.konjetic.goboardgame.model.PlayerColor;
import javafx.scene.control.Alert;

public class DialogUtils {

    public static void showWinningDialog (Player player){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WINNER!!!");
        alert.setHeaderText(player.name() + " WON!");
        alert.setContentText("Game finished.");
        alert.show();
    }

    public static void showSuccessDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("The action was successful!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
