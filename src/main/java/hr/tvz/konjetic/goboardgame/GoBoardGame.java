package hr.tvz.konjetic.goboardgame;

import hr.tvz.konjetic.goboardgame.exception.WrongPlayerNameException;
import hr.tvz.konjetic.goboardgame.jndi.ConfigurationReader;
import hr.tvz.konjetic.goboardgame.model.Player;
import hr.tvz.konjetic.goboardgame.thread.PlayerOneServerThread;
import hr.tvz.konjetic.goboardgame.thread.PlayerTwoServerThread;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class GoBoardGame extends Application {

    public static Player player;

    public static Stage mainStage;

    public static final String HOST = "localhost";
    public static final int PLAYER_TWO_SERVER_PORT = 1989;
    public static final int PLAYER_ONE_SERVER_PORT = 1990;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(GoBoardGame.class.getResource("go_board_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 650);
        stage.setTitle(player.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        String firstArgument = args[0];

        if (Player.valueOf(firstArgument).equals(Player.PLAYER_ONE)){
            player = Player.PLAYER_ONE;
            //pokreni server u novoj niti
            Thread serverStarter = new Thread(new PlayerOneServerThread());
            serverStarter.start();
        } else if( Player.valueOf(firstArgument).equals(Player.PLAYER_TWO)){
            player = Player.PLAYER_TWO;

            //kad je bez threada, onda ga starta i čeka odgovor, ne pokrene se prozor od P2
            //playerTwoAcceptRequests();

            //pokreni server u novoj niti
            Thread serverStarter = new Thread(new PlayerTwoServerThread());
            serverStarter.start();

        } else if(Player.valueOf(firstArgument).equals(Player.SINGLE_PLAYER)){
            player = Player.SINGLE_PLAYER;
        } else{
            throw new WrongPlayerNameException("The game was started with the player name: " + firstArgument
            + ", but only PLAYER_ONE and PLAYER_TWO are allowed.");
        }

        launch();

    }








}