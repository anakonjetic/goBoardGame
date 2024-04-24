package hr.tvz.konjetic.goboardgame.thread;

import hr.tvz.konjetic.goboardgame.model.GameMove;
import javafx.scene.control.Label;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable{

    private Label label;

    public GetLastGameMoveThread(Label label) {
        this.label = label;
    }


    @Override
    public void run() {
        GameMove lastGameMove = getLastGameMoveFromFile();

        label.setText("Last game move: " + lastGameMove.getPlayerColor().name() + "; ("
                +  lastGameMove.getPositionX() + ", "
                + lastGameMove.getPositionY() + ") "
                +lastGameMove.getLocalDateTime());

    }
}
