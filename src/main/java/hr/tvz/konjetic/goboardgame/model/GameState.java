package hr.tvz.konjetic.goboardgame.model;

import javafx.scene.paint.Color;
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

    public static String[][] covertGameStateWithColorsToString(Color[][] gameStateWithColors) {

        String[][] gameStateWithStrings = new String[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                gameStateWithStrings[i][j] = gameStateWithColors[i][j].toString();
            }
        }

        return gameStateWithStrings;
    }

    public static Color[][] covertGameStateWithStringToColor(String[][] gameStateWithStrings) {

        Color[][] gameStateWithColors = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                gameStateWithColors[i][j] = Color.valueOf(gameStateWithStrings[i][j]);
            }
        }

        return gameStateWithColors;
    }

}
