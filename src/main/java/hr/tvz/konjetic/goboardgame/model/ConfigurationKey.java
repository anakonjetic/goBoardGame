package hr.tvz.konjetic.goboardgame.model;

import lombok.Getter;

@Getter
public enum ConfigurationKey {

    RMI_HOST("rmi.host"), RMI_PORT("rmi.port");

    private String key;

    private ConfigurationKey(String key) {
        this.key = key;
    }
}
