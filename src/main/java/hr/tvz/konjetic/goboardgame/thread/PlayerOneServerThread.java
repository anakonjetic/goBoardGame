package hr.tvz.konjetic.goboardgame.thread;

import hr.tvz.konjetic.goboardgame.GoController;
import hr.tvz.konjetic.goboardgame.model.GameState;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static hr.tvz.konjetic.goboardgame.GoBoardGame.*;

public class PlayerOneServerThread implements Runnable{

    @Override
    public void run() {
        playerTwoAcceptRequests();
    }

    private static void playerTwoAcceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PLAYER_ONE_SERVER_PORT)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                GoController newInstance = new GoController();
                //new Thread(() ->  processSerializableClient(clientSocket)).start();
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
            GoController.deactivateButtons(false);


            System.out.println("Player two received the game state!");
            oos.writeObject("Player two received the game state - confirmation");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
