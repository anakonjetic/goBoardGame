package hr.tvz.konjetic.goboardgame.thread;

import hr.tvz.konjetic.goboardgame.GoController;
import hr.tvz.konjetic.goboardgame.jndi.ConfigurationReader;
import hr.tvz.konjetic.goboardgame.model.ConfigurationKey;
import hr.tvz.konjetic.goboardgame.model.GameState;
import hr.tvz.konjetic.goboardgame.utils.MultiPlayerUtils;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class PlayerTwoServerThread implements Runnable{

    @Override
    public void run() {
        playerTwoAcceptRequests();
    }

    private static void playerTwoAcceptRequests() {
        String playerTwoPort = ConfigurationReader.getValue(ConfigurationKey.PLAYER2_PORT);
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(playerTwoPort))){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                GoController newInstance = new GoController();
                Platform.runLater(() -> processSerializableClient(clientSocket, newInstance));
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket, GoController controller) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            GameState gameState = (GameState)ois.readObject();

            //možda da namjestim da se ovo sve u convertu riješi, ne dolje
            GoController.stoneBoard = GameState.covertGameStateWithStringToColor(gameState.getGameBoardState(), GoController.circleBoard);

            GoController.circleBoard = GameState.convertGameStateWithStringToCircle(gameState.getGameBoardState(), GoController.circleBoard);

            GoController.playerTurn = gameState.getCurrentPlayerColor();
            GoController.numberOfTurns = gameState.getNumberOfTurns();
            MultiPlayerUtils.deactivateButtons(false, GoController.circleBoard);

            System.out.println("Player two received the game state!");
            oos.writeObject("Player two received the game state - confirmation");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
