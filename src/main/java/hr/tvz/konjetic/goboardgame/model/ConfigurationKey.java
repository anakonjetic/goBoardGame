package hr.tvz.konjetic.goboardgame.model;

import lombok.Getter;

@Getter
public enum ConfigurationKey {

    RMI_HOST("rmi.host"), RMI_PORT("rmi.port"), PLAYER1_PORT("player1.port"), PLAYER2_PORT("player2.port"), CHAT_HOST("chat.host");

    private String key;

    ConfigurationKey(String key) {
        this.key = key;
    }
}
