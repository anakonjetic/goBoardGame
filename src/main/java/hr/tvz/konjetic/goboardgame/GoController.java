package hr.tvz.konjetic.goboardgame;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class GoController {

    private static final int BOARD_DIMENSIONS = 9;
    private static final Color COLOR_NOT_PLAYED = Color.TRANSPARENT;
    private static final Color COLOR_PLAYER_ONE = Color.BLACK;
    private static final Color COLOR_PLAYER_TWO = Color.WHITE;


    @FXML
    private Circle circle00;
    @FXML
    private Circle circle01;
    @FXML
    private Circle circle02;
    @FXML
    private Circle circle03;
    @FXML
    private Circle circle04;
    @FXML
    private Circle circle05;
    @FXML
    private Circle circle06;
    @FXML
    private Circle circle07;
    @FXML
    private Circle circle08;
    @FXML
    private Circle circle10;
    @FXML
    private Circle circle11;
    @FXML
    private Circle circle12;
    @FXML
    private Circle circle13;
    @FXML
    private Circle circle14;
    @FXML
    private Circle circle15;
    @FXML
    private Circle circle16;
    @FXML
    private Circle circle17;
    @FXML
    private Circle circle18;
    @FXML
    private Circle circle20;
    @FXML
    private Circle circle21;
    @FXML
    private Circle circle22;
    @FXML
    private Circle circle23;
    @FXML
    private Circle circle24;
    @FXML
    private Circle circle25;
    @FXML
    private Circle circle26;
    @FXML
    private Circle circle27;
    @FXML
    private Circle circle28;
    @FXML
    private Circle circle30;
    @FXML
    private Circle circle31;
    @FXML
    private Circle circle32;
    @FXML
    private Circle circle33;
    @FXML
    private Circle circle34;
    @FXML
    private Circle circle35;
    @FXML
    private Circle circle36;
    @FXML
    private Circle circle37;
    @FXML
    private Circle circle38;
    @FXML
    private Circle circle40;
    @FXML
    private Circle circle41;
    @FXML
    private Circle circle42;
    @FXML
    private Circle circle43;
    @FXML
    private Circle circle44;
    @FXML
    private Circle circle45;
    @FXML
    private Circle circle46;
    @FXML
    private Circle circle47;
    @FXML
    private Circle circle48;
    @FXML
    private Circle circle50;
    @FXML
    private Circle circle51;
    @FXML
    private Circle circle52;
    @FXML
    private Circle circle53;
    @FXML
    private Circle circle54;
    @FXML
    private Circle circle55;
    @FXML
    private Circle circle56;
    @FXML
    private Circle circle57;
    @FXML
    private Circle circle58;
    @FXML
    private Circle circle60;
    @FXML
    private Circle circle61;
    @FXML
    private Circle circle62;
    @FXML
    private Circle circle63;
    @FXML
    private Circle circle64;
    @FXML
    private Circle circle65;
    @FXML
    private Circle circle66;
    @FXML
    private Circle circle67;
    @FXML
    private Circle circle68;
    @FXML
    private Circle circle70;
    @FXML
    private Circle circle71;
    @FXML
    private Circle circle72;
    @FXML
    private Circle circle73;
    @FXML
    private Circle circle74;
    @FXML
    private Circle circle75;
    @FXML
    private Circle circle76;
    @FXML
    private Circle circle77;
    @FXML
    private Circle circle78;
    @FXML
    private Circle circle80;
    @FXML
    private Circle circle81;
    @FXML
    private Circle circle82;
    @FXML
    private Circle circle83;
    @FXML
    private Circle circle84;
    @FXML
    private Circle circle85;
    @FXML
    private Circle circle86;
    @FXML
    private Circle circle87;
    @FXML
    private Circle circle88;

    private static Boolean firstPlayerTurn = true;
    private Color[][] stoneBoard = new Color[BOARD_DIMENSIONS][BOARD_DIMENSIONS];

    //postavljanje vrijednosti prazne boje na sva polja
    @FXML
    public void initialize() {
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                stoneBoard[i][j] = COLOR_NOT_PLAYED;
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
                firstPlayerTurn = !firstPlayerTurn;
            }
        }


    }

    //validno je ako je neobojano dosad
    private boolean isPositionValid(int row, int column) {
        return stoneBoard[row][column].equals(COLOR_NOT_PLAYED);
    }
}