module hr.tvz.konjetic.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens hr.tvz.konjetic.goboardgame to javafx.fxml;
    exports hr.tvz.konjetic.goboardgame;
}