package hr.tvz.konjetic.goboardgame;

import hr.tvz.konjetic.goboardgame.model.GameState;
import hr.tvz.konjetic.goboardgame.utils.DialogUtils;
import hr.tvz.konjetic.goboardgame.utils.DocumentationUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static hr.tvz.konjetic.goboardgame.model.GameState.BOARD_DIMENSIONS;

public class GoController {


    private static final int MAX_TERRITORY = 81;
    private static final Color COLOR_NOT_PLAYED = Color.TRANSPARENT;
    private static final Color COLOR_PLAYER_ONE = Color.BLACK;
    private static final Color COLOR_PLAYER_TWO = Color.WHITE;

    @FXML
    public AnchorPane circleAnchorPane;

    private static Boolean firstPlayerTurn = true;
    public static Color[][] stoneBoard = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];
    public static Circle[][] circleBoard = new Circle[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

    public Integer capturedP1Stones = 0;
    public Integer capturedP2Stones = 0;

    public Integer playerOneTerritory = 0;
    public Integer playerTwoTerritory = 0;

    public Integer numberOfTurns = 0;

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


    }

    @FXML
    public void placeStone(Event event) {

        if (event.getSource() instanceof Circle circle) {

            //iz imena izvuče koji je circle u pitanju
            int row = Character.getNumericValue(circle.getId().charAt(6));
            int column = Character.getNumericValue(circle.getId().charAt(7));



            //boja kuglu
            if (isPositionValid(row, column)) {
                stoneBoard[row][column] = firstPlayerTurn ? COLOR_PLAYER_ONE : COLOR_PLAYER_TWO;
                System.out.println(stoneBoard[row][column].toString());
                circle.setFill(stoneBoard[row][column]);
                circle.setStrokeWidth(1);
                captureTerritory(firstPlayerTurn ? COLOR_PLAYER_ONE : COLOR_PLAYER_TWO);
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


               /* for (int i = 0; i < BOARD_DIMENSIONS; i++) {
                    for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                        gameState.getGameBoardState()[i][j] = "#ffffff";
                    }
                }
                gameState.getGameBoardState()[0][0] = "#000000";*/
                gameState.setNumberOfTurns(numberOfTurns);

                //POŠALJI DRUGOM IGRAČU TEK KAD STISNEMO

                playerOneSendRequest(gameState);

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

                firstPlayerTurn = !firstPlayerTurn;
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
                        if (firstPlayerTurn) {
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

        GameState gameStateToSave = new GameState(stringGameState, numberOfTurns);

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

           for (int i = 0; i < BOARD_DIMENSIONS; i++){
               for (int j = 0; j < BOARD_DIMENSIONS; j++){
                   Circle circle = (Circle) circleAnchorPane.lookup("#circle"+i+j);
                   circle.setFill(stoneBoard[i][j]);
                   if (stoneBoard[i][j] != Color.valueOf("#000000")) {
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

    public void generateHTMLDocumentation(){
        DocumentationUtils.generateDocumentation();
        DialogUtils.showSuccessDialog("Documentation was successfully generated!");
    }

    private static void playerOneSendRequest( GameState gameState) {
        // Closing socket will also close the socket's InputStream and OutputStream.
        try (Socket clientSocket = new Socket(GoBoardGame.HOST, GoBoardGame.PLAYER_TWO_SERVER_PORT)){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequest(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());



        oos.writeObject(gameState);
        System.out.println("Game state sent to the Player two!");
    }


}