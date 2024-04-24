package hr.tvz.konjetic.goboardgame.thread;

import hr.tvz.konjetic.goboardgame.model.GameMove;
import hr.tvz.konjetic.goboardgame.utils.GameMoveUtils;

public abstract class GameMoveThread {

    private static Boolean gameMoveFileAccessInProgress = false;

    protected synchronized void saveNewGameMoveToFile(GameMove gameMove){
        while(gameMoveFileAccessInProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        gameMoveFileAccessInProgress = true;

        GameMoveUtils.saveNewGameMove(gameMove);

        gameMoveFileAccessInProgress = false;

        notifyAll();
    }

    //synch je tu da se metode ne preklapaju, nego da budu sinkronizirane
    protected synchronized GameMove getLastGameMoveFromFile(){
        while(gameMoveFileAccessInProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        gameMoveFileAccessInProgress = true;

        GameMove lastGameMove =  GameMoveUtils.getNewGameMove();

        gameMoveFileAccessInProgress = false;

        notifyAll();

        return lastGameMove;
    }

}
