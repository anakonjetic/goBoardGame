package hr.tvz.konjetic.goboardgame;

import hr.tvz.konjetic.goboardgame.model.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GoBoardGame extends Application {

    public static Player player;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GoBoardGame.class.getResource("go_board_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 650);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        String firstArgument = args[0];

        if (Player.valueOf(firstArgument).equals(Player.PLAYER_ONE)){
            player = Player.PLAYER_ONE;
        } else if( Player.valueOf(firstArgument).equals(Player.PLAYER_TWO)){
            player = Player.PLAYER_TWO;
        }

        launch();
    }
}