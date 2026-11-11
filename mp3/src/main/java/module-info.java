module mp3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens mp3 to javafx.fxml;
    exports mp3;
}
