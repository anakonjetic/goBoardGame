package hr.tvz.konjetic.goboardgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameMove implements Serializable {

    private PlayerColor playerColor;
    private Integer positionX;
    private Integer positionY;
    private LocalDateTime localDateTime;

}
