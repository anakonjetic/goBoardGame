package hr.tvz.konjetic.goboardgame;

import hr.tvz.konjetic.goboardgame.chat.ChatService;
import hr.tvz.konjetic.goboardgame.jndi.ConfigurationReader;
import hr.tvz.konjetic.goboardgame.model.*;
import hr.tvz.konjetic.goboardgame.thread.GetLastGameMoveThread;
import hr.tvz.konjetic.goboardgame.thread.SaveNewGameMoveThread;
import hr.tvz.konjetic.goboardgame.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;

import static hr.tvz.konjetic.goboardgame.model.GameState.BOARD_DIMENSIONS;

public class GoController {
    public static final int MAX_TERRITORY = 50;
    public static PlayerColor playerTurn = PlayerColor.PLAYER_ONE;
    public static Color[][] stoneBoard = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];
    public static Circle[][] circleBoard = new Circle[BOARD_DIMENSIONS][BOARD_DIMENSIONS];
    public static Integer capturedP1Stones = 0;
    public static Integer capturedP2Stones = 0;
    public static Integer numberOfTurns = 0;
    private static ChatService stub;
    @FXML
    public AnchorPane circleAnchorPane;
    public static Integer playerOneTerritory = 0;
    public static Integer playerTwoTerritory = 0;
    @FXML
    private TextField chatTextField;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private Label lastGameMoveLabel;

    @FXML
    public void initialize() {
        //postavljanje vrijednosti prazne boje na sva polja
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                stoneBoard[i][j] = PlayerColor.NOT_PLAYED.getColor();
            }
        }
        //inicijalizacija polja krugova
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                Circle circle = (Circle) circleAnchorPane.lookup("#circle" + i + j);
                circleBoard[i][j] = circle;
            }
        }
        //postavljanje chat servisa za multiplayer
        if (!GoBoardGame.player.name().equals(Player.SINGLE_PLAYER.name())) {
            try {
                String rmiPort = ConfigurationReader.getValue(ConfigurationKey.RMI_PORT);
                String serverName = ConfigurationReader.getValue(ConfigurationKey.RMI_HOST);
                Registry registry = LocateRegistry.getRegistry(serverName, Integer.parseInt(rmiPort));
                stub = (ChatService) registry.lookup(ChatService.REMOTE_OBJECT_NAME);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> ChatUtils.refreshChatTextArea(stub, chatTextArea)));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
        }
        //postavljanje labele koja se stalno refresha
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> Platform.runLater(new GetLastGameMoveThread(lastGameMoveLabel))));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    @FXML
    public void sendChatMessage() {
        String chatMessage = chatTextField.getText();
        String playerName = GoBoardGame.player.name();

        try {
            stub.sendChatMessage(playerName + ": " + chatMessage);
            ChatUtils.refreshChatTextArea(stub, chatTextArea);
            chatTextField.setText("");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void placeStone(Event event) {

        if (event.getSource() instanceof Circle circle) {
            //iz imena izvuče koji je circle u pitanju
            int row = Character.getNumericValue(circle.getId().charAt(6));
            int column = Character.getNumericValue(circle.getId().charAt(7));
            //boja kuglu
            if (GameEngineUtils.isPositionValid(row, column, stoneBoard)) {
                stoneBoard[row][column] = playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_ONE.getColor() : PlayerColor.PLAYER_TWO.getColor();
                System.out.println(stoneBoard[row][column].toString());
                circle.setFill(stoneBoard[row][column]);
                circle.setStrokeWidth(1);
                GameEngineUtils.captureTerritory(playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_ONE.getColor() : PlayerColor.PLAYER_TWO.getColor(), circleAnchorPane, playerTurn);
                numberOfTurns++;
                //postavljanje gamemovea i spremanje u datoteku poteza
                GameMove newGameMove = new GameMove(playerTurn, row, column, LocalDateTime.now());
                //spremanje preko threada
                SaveNewGameMoveThread saveNewGameMoveThread = new SaveNewGameMoveThread(newGameMove);
                Thread starter = new Thread(saveNewGameMoveThread);
                starter.start();
                //OVDJE JE UPDATEAN GAME STATE
                MultiPlayerUtils.updateGameStateAfterAction(stoneBoard);
                MultiPlayerUtils.deactivateButtons(true, circleBoard);
                GameEngineUtils.calculateTerritory(stoneBoard);
                GameEngineUtils.checkWinner(circleAnchorPane, stoneBoard);
                //UPDATE GAME STATEA
                MultiPlayerUtils.updateGameStateAfterAction(stoneBoard);
                playerTurn = playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_TWO : PlayerColor.PLAYER_ONE;
            }
        }
    }

    public void restartGame(){
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                stoneBoard[i][j] = PlayerColor.NOT_PLAYED.getColor();
                circleBoard[i][j].setFill(stoneBoard[i][j]);
                circleBoard[i][j].setStrokeWidth(0);
            }
        }
        numberOfTurns = 0;
        capturedP1Stones = 0;
        capturedP2Stones = 0;
        playerOneTerritory = 0;
        playerTwoTerritory = 0;
        playerTurn = PlayerColor.PLAYER_ONE;
        if (!GoBoardGame.player.name().equals(Player.SINGLE_PLAYER.name())){
            MultiPlayerUtils.updateGameStateAfterAction(stoneBoard);
        }
        DialogUtils.showSuccessDialog("The game has been restarted!");
    }

    public void saveGame() {
        String[][] stringGameState = GameState.covertGameStateWithColorsToString(stoneBoard);
        GameState gameStateToSave = new GameState(stringGameState, numberOfTurns, playerTurn);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("saveGame/gameSave.dat"))) {
            oos.writeObject(gameStateToSave);
            DialogUtils.showSuccessDialog("Game was successfully saved!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("saveGame/gameSave.dat"))) {
            GameState gameStateToLoad = (GameState) ois.readObject();
            stoneBoard = GameState.covertGameStateWithStringToColor(gameStateToLoad.getGameBoardState(), circleBoard);
            numberOfTurns = gameStateToLoad.getNumberOfTurns();
            playerTurn = gameStateToLoad.getCurrentPlayerColor();

            for (int i = 0; i < BOARD_DIMENSIONS; i++) {
                for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                    Circle circle = (Circle) circleAnchorPane.lookup("#circle" + i + j);
                    circle.setFill(stoneBoard[i][j]);
                    if (!stoneBoard[i][j].equals(PlayerColor.NOT_PLAYED.getColor())) {
                        circle.setStrokeWidth(1);
                    } else {
                        circle.setStrokeWidth(0);
                    }
                }
            }
            if (!GoBoardGame.player.name().equals(Player.SINGLE_PLAYER.name())){
                MultiPlayerUtils.updateGameStateAfterAction(stoneBoard);
            }
            DialogUtils.showSuccessDialog("Game was successfully loaded!");

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void generateHTMLDocumentation() {
        DocumentationUtils.generateDocumentation();
        DialogUtils.showSuccessDialog("Documentation was successfully generated!");
    }
}