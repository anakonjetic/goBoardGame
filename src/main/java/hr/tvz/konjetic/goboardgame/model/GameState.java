package hr.tvz.konjetic.goboardgame.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState implements Serializable {

    public static final int BOARD_DIMENSIONS = 9;

    //stanje na ploci igre
    //trenutni broj poteza

    private String[][] gameBoardState;
    private Integer numberOfTurns;
    private PlayerColor currentPlayerColor;

    public static String[][] covertGameStateWithColorsToString(Color[][] gameStateWithColors) {

        String[][] gameStateWithStrings = new String[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                gameStateWithStrings[i][j] = gameStateWithColors[i][j].toString();
            }
        }

        return gameStateWithStrings;
    }

    public static Color[][] covertGameStateWithStringToColor(String[][] gameStateWithStrings, Circle[][] circleBoard) {

        Color[][] gameStateWithColors = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                gameStateWithColors[i][j] = Color.valueOf(gameStateWithStrings[i][j]);
            }
        }

        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for (int j = 0; j < BOARD_DIMENSIONS; j++){
                circleBoard[i][j].setFill(gameStateWithColors[i][j]);

                if (circleBoard[i][j].getFill() != Color.valueOf("#000000")) {
                    circleBoard[i][j].setStrokeWidth(1);
                } else {
                    circleBoard[i][j].setStrokeWidth(0);
                }
            }
        }

        return gameStateWithColors;
    }

    public static Circle[][] convertGameStateWithStringToCircle(String[][] gameStateWithStrings, Circle[][] circleBoard) {

        Color[][] gameStateWithColors = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                gameStateWithColors[i][j] = Color.valueOf(gameStateWithStrings[i][j]);
            }
        }

        for (int i = 0; i < BOARD_DIMENSIONS; i++){
            for (int j = 0; j < BOARD_DIMENSIONS; j++){
                circleBoard[i][j].setFill(gameStateWithColors[i][j]);

                if (gameStateWithColors[i][j].equals(PlayerColor.NOT_PLAYED.getColor())) {
                    circleBoard[i][j].setStrokeWidth(0);
                } else {
                    circleBoard[i][j].setStrokeWidth(1);
                }
            }
        }

        return circleBoard;

    }

}
