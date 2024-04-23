module hr.tvz.konjetic.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires static lombok;
    requires java.desktop;
    requires java.rmi;


    opens hr.tvz.konjetic.goboardgame to javafx.fxml;
    exports hr.tvz.konjetic.goboardgame;
    opens hr.tvz.konjetic.goboardgame.chat to java.rmi;
}