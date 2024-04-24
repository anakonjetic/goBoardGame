package hr.tvz.konjetic.goboardgame.utils;

import hr.tvz.konjetic.goboardgame.GoBoardGame;
import hr.tvz.konjetic.goboardgame.GoController;
import hr.tvz.konjetic.goboardgame.jndi.ConfigurationReader;
import hr.tvz.konjetic.goboardgame.model.ConfigurationKey;
import hr.tvz.konjetic.goboardgame.model.GameState;
import hr.tvz.konjetic.goboardgame.model.Player;
import hr.tvz.konjetic.goboardgame.model.PlayerColor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static hr.tvz.konjetic.goboardgame.model.GameState.BOARD_DIMENSIONS;

public class MultiPlayerUtils {

    public static void playerOneSendRequest( GameState gameState) {
        String chatHost = ConfigurationReader.getValue(ConfigurationKey.CHAT_HOST);
        String playerTwoPort = ConfigurationReader.getValue(ConfigurationKey.PLAYER2_PORT);
        try (Socket clientSocket = new Socket(chatHost, Integer.parseInt(playerTwoPort))){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            sendSerializableRequestToPlayerTwo(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void playerTwoSendRequest( GameState gameState) {
        String chatHost = ConfigurationReader.getValue(ConfigurationKey.CHAT_HOST);
        String playerOnePort = ConfigurationReader.getValue(ConfigurationKey.PLAYER1_PORT);
        try (Socket clientSocket = new Socket(chatHost, Integer.parseInt(playerOnePort))){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            sendSerializableRequestToPlayerOne(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void sendSerializableRequestToPlayerOne(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());



        oos.writeObject(gameState);
        System.out.println("Game state sent to the Player two!");
    }

    public static void sendSerializableRequestToPlayerTwo(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent to the Player one!");
    }

    public static void deactivateButtons(Boolean status, Circle[][] circleBoard){
        if (!GoBoardGame.player.name().equals(Player.SINGLE_PLAYER.name())) {
            for (int i = 0; i < BOARD_DIMENSIONS; i++) {
                for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                    circleBoard[i][j].setDisable(status);
                }
            }
        }
    }

    public static void updateGameStateAfterAction(Color[][] stoneBoard){
        GameState gameState = new GameState();
        gameState.setGameBoardState(new String[GameState.BOARD_DIMENSIONS][GameState.BOARD_DIMENSIONS]);
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                gameState.getGameBoardState()[i][j] = stoneBoard[i][j].toString();
            }
        }
        gameState.setNumberOfTurns(GoController.numberOfTurns);
        gameState.setCurrentPlayerColor(GoController.playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_TWO : PlayerColor.PLAYER_ONE);
        //POŠALJI DRUGOM IGRAČU TEK KAD STISNEMO
        if (GoBoardGame.player.name().equals(Player.PLAYER_ONE.name())) {
            MultiPlayerUtils.playerOneSendRequest(gameState);
        } else if (GoBoardGame.player.name().equals(Player.PLAYER_TWO.name())) {
            MultiPlayerUtils.playerTwoSendRequest(gameState);
        }
    }

}
