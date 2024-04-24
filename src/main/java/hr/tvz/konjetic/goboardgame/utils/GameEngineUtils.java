package hr.tvz.konjetic.goboardgame.utils;

import hr.tvz.konjetic.goboardgame.GoController;
import hr.tvz.konjetic.goboardgame.model.Player;
import hr.tvz.konjetic.goboardgame.model.PlayerColor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

import static hr.tvz.konjetic.goboardgame.model.GameState.BOARD_DIMENSIONS;

public class GameEngineUtils {

    public static Integer calculateSpecificPlayerTerritory(Color color, Color[][] stoneBoard) {
        Integer numberOfTerritories = 0;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                if (
                        stoneBoard[i][j].equals(color)
                ) {
                    numberOfTerritories++;
                } else if (stoneBoard[i][j].equals(PlayerColor.NOT_PLAYED.getColor())) {
                    {
                        List<int[]> neighbors = getPositionsNeighbors(i, j);
                        numberOfTerritories += calculateEmptyPositionsTerritoryPoints(color, neighbors, stoneBoard);
                    }
                }
            }
        }

        return numberOfTerritories;
    }

    //broje se prazna polja koja imaju obojane susjede
    public static Integer calculateEmptyPositionsTerritoryPoints(Color color, List<int[]> neighbors, Color[][] stoneBoard){
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
    public static List<int[]> getPositionsNeighbors(int row, int column){
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

    public static Circle getCircleByPosition(int row, int column, AnchorPane circleAnchorPane){

        Circle circle = (Circle) circleAnchorPane.lookup("#circle"+row+column);

        return circle;
    }

    public static void resetGame(AnchorPane circleAnchorPane) {
        circleAnchorPane.getChildren().forEach(node -> {
            if (node instanceof Circle circle) {
                circle.setFill(PlayerColor.NOT_PLAYED.getColor());
                circle.setStrokeWidth(0);
            }
        });

        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for(int j = 0; j < BOARD_DIMENSIONS; j++){
                GoController.stoneBoard[i][j] = PlayerColor.NOT_PLAYED.getColor();
            }
        }
    }

    public static boolean isPositionValid(int row, int column, Color[][] stoneBoard) {
        return stoneBoard[row][column].equals(PlayerColor.NOT_PLAYED.getColor());
    }

    public static void captureTerritory(Color playerColor, AnchorPane circleAnchorPane, PlayerColor playerTurn){
        for(int i = 0; i < BOARD_DIMENSIONS; i++){
            for(int j = 0; j < BOARD_DIMENSIONS; j++) {
                if (!GoController.stoneBoard[i][j].equals(PlayerColor.NOT_PLAYED.getColor()) && !GoController.stoneBoard[i][j].equals(playerColor)){
                    List<int[]> neighbors = GameEngineUtils.getPositionsNeighbors(i, j);
                    Integer dangerousNeighbors = 0;
                    for (int[] neighbor : neighbors) {
                        int r = neighbor[0];
                        int c = neighbor[1];
                        if (GoController.stoneBoard[r][c].equals(playerColor)) {
                            dangerousNeighbors++;
                        }
                        if (dangerousNeighbors == neighbors.size()) {
                            GoController.stoneBoard[i][j] = PlayerColor.NOT_PLAYED.getColor();
                            Circle capturedCircle = GameEngineUtils.getCircleByPosition(i, j, circleAnchorPane);
                            capturedCircle.setFill(PlayerColor.NOT_PLAYED.getColor());
                            capturedCircle.setStrokeWidth(0);
                            if (playerTurn.equals(PlayerColor.PLAYER_ONE)) {
                                GoController.capturedP2Stones++;
                            } else {
                                GoController.capturedP1Stones++;
                            }
                        }
                    }
                }

            }
        }
    }


    public static void calculateTerritory(Color[][] stoneBoard) {
        GoController.playerOneTerritory = GameEngineUtils.calculateSpecificPlayerTerritory(PlayerColor.PLAYER_ONE.getColor(), stoneBoard);
        GoController.playerTwoTerritory = GameEngineUtils.calculateSpecificPlayerTerritory(PlayerColor.PLAYER_TWO.getColor(), stoneBoard);
        System.out.println(Player.PLAYER_ONE.name() + " has: " + GoController.playerOneTerritory + " and " + Player.PLAYER_TWO.name() + " has: " + GoController.playerTwoTerritory);
    }

    public static void checkWinner(AnchorPane circleAnchorPane, Color[][] stoneBoard) {
        if ((GoController.playerOneTerritory + GoController.playerTwoTerritory) >= GoController.MAX_TERRITORY) {
            if ((GoController.playerOneTerritory + GoController.capturedP2Stones) > (GoController.playerTwoTerritory + GoController.capturedP1Stones)) {
                DialogUtils.showWinningDialog(Player.PLAYER_ONE);
            } else {
                DialogUtils.showWinningDialog(Player.PLAYER_TWO);
            }
            GameEngineUtils.resetGame(circleAnchorPane);
        }
    }

}
