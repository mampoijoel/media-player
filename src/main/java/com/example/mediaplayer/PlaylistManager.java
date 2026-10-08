package com.example.mediaplayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PlaylistManager {

    private final ObservableList<File> files = FXCollections.observableArrayList();
    private final ObservableList<String> displayNames = FXCollections.observableArrayList();

    private Stage stage;
    private ListView<String> listView;
    private MediaController mediaController;
    private int currentIndex = -1;

    public void attach(Stage stage,
                       ListView<String> listView,
                       MediaController mediaController) {
        this.stage = stage;
        this.listView = listView;
        this.mediaController = mediaController;

        // When the user clicks a playlist item, auto-play it
        listView.getSelectionModel()
                .selectedIndexProperty()
                .addListener((obs, oldI, newI) -> {
                    int idx = newI.intValue();
                    if (idx >= 0 && idx != currentIndex) {
                        currentIndex = idx;
                        mediaController.load(files.get(idx));
                    }
                });
    }

    public ObservableList<String> getPlaylist() {
        return displayNames;
    }

    public void addFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Media File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio/Video",
                        "*.mp3", "*.mp4", "*.wav", "*.m4a", "*.aac", "*.m4v"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            files.add(file);
            displayNames.add(file.getName());
        }
    }

    public void removeSelected() {
        int idx = listView.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;

        files.remove(idx);
        displayNames.remove(idx);

        if (idx == currentIndex) {
            mediaController.stop();
            currentIndex = -1;
        } else if (idx < currentIndex) {
            currentIndex--;
        }
    }

    public void playNext() {
        if (files.isEmpty()) return;
        int next = (currentIndex + 1) % files.size();
        listView.getSelectionModel().select(next);
    }

    public void playPrevious() {
        if (files.isEmpty()) return;
        int prev = (currentIndex - 1 + files.size()) % files.size();
        listView.getSelectionModel().select(prev);
    }
}
