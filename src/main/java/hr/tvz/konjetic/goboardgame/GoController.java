package hr.tvz.konjetic.goboardgame;

import hr.tvz.konjetic.goboardgame.chat.ChatServer;
import hr.tvz.konjetic.goboardgame.chat.ChatService;
import hr.tvz.konjetic.goboardgame.model.GameState;
import hr.tvz.konjetic.goboardgame.model.Player;
import hr.tvz.konjetic.goboardgame.model.PlayerColor;
import hr.tvz.konjetic.goboardgame.utils.DialogUtils;
import hr.tvz.konjetic.goboardgame.utils.DocumentationUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import static hr.tvz.konjetic.goboardgame.model.GameState.BOARD_DIMENSIONS;

public class GoController {


    private static final int MAX_TERRITORY = 81;
    public static final Color COLOR_NOT_PLAYED = Color.TRANSPARENT;
    private static final Color COLOR_PLAYER_ONE = Color.BLACK;
    private static final Color COLOR_PLAYER_TWO = Color.WHITE;

    private static ChatService stub;

    @FXML
    public AnchorPane circleAnchorPane;

    @FXML
    private TextField chatTextField;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private Button sendChatButton;

    public static PlayerColor playerTurn = PlayerColor.PLAYER_ONE;
    public static Color[][] stoneBoard = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];
    public static Circle[][] circleBoard = new Circle[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

    public Integer capturedP1Stones = 0;
    public Integer capturedP2Stones = 0;

    public Integer playerOneTerritory = 0;
    public Integer playerTwoTerritory = 0;

    public static Integer numberOfTurns = 0;

    //postavljanje vrijednosti prazne boje na sva polja
    @FXML
    public void initialize() {
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                stoneBoard[i][j] = COLOR_NOT_PLAYED;
            }
        }

        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for (int j = 0; j < BOARD_DIMENSIONS; j++){
                Circle circle = (Circle) circleAnchorPane.lookup("#circle"+i+j);
                circleBoard[i][j] = circle;
            }
        }

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", ChatServer.RMI_PORT);
            stub = (ChatService) registry.lookup(ChatService.REMOTE_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshChatTextArea()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    @FXML
    public void sendChatMessage(){

        String chatMessage = chatTextField.getText();

        String playerName = GoBoardGame.player.name();

        try {
            stub.sendChatMessage(playerName + ": " + chatMessage);
           refreshChatTextArea();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }

    private void refreshChatTextArea(){
        List<String> chatHistory = null;
        try {
            chatHistory = stub.returnChatHistory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();

        for (String s : chatHistory) {
            sb.append(s);
            sb.append("\n");
        }

        chatTextArea.setText(sb.toString());
    }

    @FXML
    public void placeStone(Event event) {

        if (event.getSource() instanceof Circle circle) {

            //iz imena izvuče koji je circle u pitanju
            int row = Character.getNumericValue(circle.getId().charAt(6));
            int column = Character.getNumericValue(circle.getId().charAt(7));



            //boja kuglu
            if (isPositionValid(row, column)) {
                stoneBoard[row][column] = playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_ONE.getColor() : PlayerColor.PLAYER_TWO.getColor();
                System.out.println(stoneBoard[row][column].toString());
                circle.setFill(stoneBoard[row][column]);
                circle.setStrokeWidth(1);
                captureTerritory(playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_ONE.getColor() : PlayerColor.PLAYER_TWO.getColor());
                numberOfTurns++;

                //OVDJE JE UPDATEAN GAME STATE
                GameState gameState = new GameState();

                gameState.setGameBoardState(new String[GameState.BOARD_DIMENSIONS][GameState.BOARD_DIMENSIONS]);

                //stoneBoard = GameState.covertGameStateWithStringToColor(gameState.getGameBoardState());

                for (int i = 0; i < BOARD_DIMENSIONS; i++){
                    for (int j = 0; j < BOARD_DIMENSIONS; j++){
                      gameState.getGameBoardState()[i][j] = stoneBoard[i][j].toString();
                    }
                }

                gameState.setNumberOfTurns(numberOfTurns);
                gameState.setCurrentPlayerColor(playerTurn.equals(PlayerColor.PLAYER_ONE) ? PlayerColor.PLAYER_TWO : PlayerColor.PLAYER_ONE);

                //POŠALJI DRUGOM IGRAČU TEK KAD STISNEMO

                if (GoBoardGame.player.name().equals(Player.PLAYER_ONE.name())) {
                    playerOneSendRequest(gameState);
                } else if (GoBoardGame.player.name().equals(Player.PLAYER_TWO.name())){
                    playerTwoSendRequest(gameState);
                }

                deactivateButtons(true);

                calculateTerritory();

                if ((playerOneTerritory + playerTwoTerritory) >= MAX_TERRITORY){
                    if((playerOneTerritory + capturedP2Stones) > (playerTwoTerritory + capturedP1Stones)){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("WINNER!!!");
                        alert.setHeaderText("Player 1 WON!");
                        alert.setContentText("Game finished.");
                        alert.show();
                    } else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("WINNER!!!");
                        alert.setHeaderText("Player 2 WON!");
                        alert.setContentText("Game finished.");
                        alert.show();
                    }

                    resetGame();
                }

                playerTurn = playerTurn.equals(PlayerColor.PLAYER_ONE) ?  PlayerColor.PLAYER_TWO : PlayerColor.PLAYER_ONE;
            }
        }


    }

    private boolean isPositionValid(int row, int column) {
        return stoneBoard[row][column].equals(COLOR_NOT_PLAYED);
    }

    private void calculateTerritory(){
        playerOneTerritory = calculateSpecificPlayerTerritory(COLOR_PLAYER_ONE);
        playerTwoTerritory = calculateSpecificPlayerTerritory(COLOR_PLAYER_TWO);

        System.out.println("Player one has: " + playerOneTerritory + " and player two has: " + playerTwoTerritory);

    }

    private Integer calculateSpecificPlayerTerritory(Color color){
        Integer numberOfTerritories = 0;
        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for (int j = 0; j < BOARD_DIMENSIONS; j++){
                if (
                        stoneBoard[i][j].equals(color)
                ){
                    numberOfTerritories++;
                }else if(stoneBoard[i][j].equals(COLOR_NOT_PLAYED)){
                    {
                        List<int[]> neighbors = getPositionsNeighbors(i, j);
                        numberOfTerritories += calculateEmptyPositionsTerritoryPoints(color, neighbors);
                    }
                }
            }
        }

        return numberOfTerritories;
    }
    //broje se orazna polja koja imaju obojane susjede
    private Integer calculateEmptyPositionsTerritoryPoints(Color color, List<int[]> neighbors){
        Integer territoryPoints = 0;

        for (int[] neighbor : neighbors){
            int row = neighbor[0];
            int column = neighbor[1];

            if (stoneBoard[row][column].equals(color)){
                territoryPoints++;
            }
        }

        return territoryPoints;
    }

    //traženje susjeda
    private List<int[]> getPositionsNeighbors(int row, int column){
        List<int[]> neighbors = new ArrayList<>();

        if (row - 1 >= 0) {
            neighbors.add(new int[]{row - 1, column});
        }
        if (row + 1 < BOARD_DIMENSIONS) {
            neighbors.add(new int[]{row + 1, column});
        }
        if (column - 1 >= 0) {
            neighbors.add(new int[]{row, column - 1});
        }
        if (column + 1 < BOARD_DIMENSIONS) {
            neighbors.add(new int[]{row, column + 1});
        }

        return neighbors;
    }

    //brisanje okupiranih
    private void captureTerritory(Color playerColor){

        for(int i = 0; i < BOARD_DIMENSIONS; i++){
            for(int j = 0; j < BOARD_DIMENSIONS; j++) {
                if (!stoneBoard[i][j].equals(COLOR_NOT_PLAYED) && !stoneBoard[i][j].equals(playerColor)){
                    List<int[]> neighbors = getPositionsNeighbors(i, j);
                Integer dangerousNeighbors = 0;
                for (int[] neighbor : neighbors) {
                    int r = neighbor[0];
                    int c = neighbor[1];
                    if (stoneBoard[r][c].equals(playerColor)) {
                        dangerousNeighbors++;
                        System.out.println("dn" + dangerousNeighbors);
                    }
                    if (dangerousNeighbors == neighbors.size()) {
                        stoneBoard[i][j] = COLOR_NOT_PLAYED;
                        Circle capturedCircle = getCircleByPosition(i, j);
                        capturedCircle.setFill(COLOR_NOT_PLAYED);
                        capturedCircle.setStrokeWidth(0);
                        if (playerTurn.equals(PlayerColor.PLAYER_ONE)) {
                            capturedP2Stones++;
                        } else {
                            capturedP1Stones++;
                        }
                    }
                }
            }

            }
        }
    }

    private Circle getCircleByPosition(int row, int column){

        Circle circle = (Circle) circleAnchorPane.lookup("#circle"+row+column);

        return circle;
    }

    private void resetGame() {
        circleAnchorPane.getChildren().forEach(node -> {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                circle.setFill(COLOR_NOT_PLAYED);
                circle.setStrokeWidth(0);
            }
        });

        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for(int j = 0; j < BOARD_DIMENSIONS; j++){
                stoneBoard[i][j] = COLOR_NOT_PLAYED;
            }
        }
    }

    public void saveGame(){
        String[][] stringGameState = GameState.covertGameStateWithColorsToString(stoneBoard);

        GameState gameStateToSave = new GameState(stringGameState, numberOfTurns, playerTurn);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("saveGame/gameSave.dat"))){
            oos.writeObject(gameStateToSave);
            DialogUtils.showSuccessDialog("Game was successfully saved!");
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }

    public void loadGame(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("saveGame/gameSave.dat"))){
           GameState gameStateToLoad = (GameState) ois.readObject();

           stoneBoard = GameState.covertGameStateWithStringToColor(gameStateToLoad.getGameBoardState(), circleBoard);
           numberOfTurns = gameStateToLoad.getNumberOfTurns();
           playerTurn = gameStateToLoad.getCurrentPlayerColor();


           for (int i = 0; i < BOARD_DIMENSIONS; i++){
               for (int j = 0; j < BOARD_DIMENSIONS; j++){
                   Circle circle = (Circle) circleAnchorPane.lookup("#circle"+i+j);
                   circle.setFill(stoneBoard[i][j]);
                   if (stoneBoard[i][j].equals(COLOR_NOT_PLAYED)) {
                       circle.setStrokeWidth(1);
                   } else {
                       circle.setStrokeWidth(0);
                   }
               }
           }

           DialogUtils.showSuccessDialog("Game was successfully loaded!");

        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public static void deactivateButtons(Boolean status){
        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for (int j = 0; j < BOARD_DIMENSIONS; j++){
                circleBoard[i][j].setDisable(status);
            }
        }
    }


    public void generateHTMLDocumentation(){
        DocumentationUtils.generateDocumentation();
        DialogUtils.showSuccessDialog("Documentation was successfully generated!");
    }

    private static void playerOneSendRequest( GameState gameState) {
        // Closing socket will also close the socket's InputStream and OutputStream.
        try (Socket clientSocket = new Socket(GoBoardGame.HOST, GoBoardGame.PLAYER_TWO_SERVER_PORT)){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequestToPlayerTwo(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void playerTwoSendRequest( GameState gameState) {
        // Closing socket will also close the socket's InputStream and OutputStream.
        try (Socket clientSocket = new Socket(GoBoardGame.HOST, GoBoardGame.PLAYER_ONE_SERVER_PORT)){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequestToPlayerOne(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void sendSerializableRequestToPlayerOne(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());



        oos.writeObject(gameState);
        System.out.println("Game state sent to the Player two!");
    }

    private static void sendSerializableRequestToPlayerTwo(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());



        oos.writeObject(gameState);
        System.out.println("Game state sent to the Player one!");
    }




}