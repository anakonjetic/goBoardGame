package hr.tvz.konjetic.goboardgame.model;

import javafx.scene.paint.Color;

public enum PlayerColor {
    PLAYER_ONE(Color.BLACK), PLAYER_TWO(Color.WHITE), NOT_PLAYED(Color.TRANSPARENT);

    private final Color color;

    PlayerColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
