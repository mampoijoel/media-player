package com.example.mediaplayer;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyboardController {

    private final MediaController mediaController;
    private final PlaylistManager playlistManager;

    public KeyboardController(MediaController mediaController,
                              PlaylistManager playlistManager) {
        this.mediaController = mediaController;
        this.playlistManager = playlistManager;
    }

    public void attach(Scene scene) {
        
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();

            switch (code) {
                case SPACE:
                    mediaController.togglePlayPause();
                    event.consume();
                    break;
                case S:
                    mediaController.stop();
                    event.consume();
                    break;
                case N:
                    playlistManager.playNext();
                    event.consume();
                    break;
                case P:
                    playlistManager.playPrevious();
                    event.consume();
                    break;
                case UP:
                    mediaController.increaseVolume();
                    event.consume();
                    break;
                case DOWN:
                    mediaController.decreaseVolume();
                    event.consume();
                    break;
                case M:
                    mediaController.toggleMute();
                    event.consume();
                    break;
                default:
                  
            }
        });
    }
}