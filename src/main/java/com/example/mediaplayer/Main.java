package com.example.mediaplayer;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MediaPlayerApp app = new MediaPlayerApp();
        app.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
