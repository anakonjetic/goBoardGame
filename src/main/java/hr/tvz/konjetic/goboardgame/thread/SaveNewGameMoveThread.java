package hr.tvz.konjetic.goboardgame.thread;

import hr.tvz.konjetic.goboardgame.model.GameMove;
import hr.tvz.konjetic.goboardgame.utils.GameMoveUtils;

public class SaveNewGameMoveThread extends GameMoveThread implements Runnable{

    private GameMove gameMove;

    public SaveNewGameMoveThread(GameMove newGameMove){
        this.gameMove = newGameMove;
    }

    @Override
    public void run() {
        saveNewGameMoveToFile(gameMove);
    }
}
