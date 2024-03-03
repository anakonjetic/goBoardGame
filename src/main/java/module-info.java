module hr.tvz.konjetic.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens hr.tvz.konjetic.goboardgame to javafx.fxml;
    exports hr.tvz.konjetic.goboardgame;
}