package com.example.mediaplayer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class MediaPlayerApp {

    private MediaController mediaController;
    private PlaylistManager playlistManager;
    private ListView<String> playlistView;
    private Slider volumeSlider;
    private Slider timeSlider;
    private Label timeLabel;
    private Label nowPlayingLabel;

    public void start(Stage stage) {

        // --- Core logic ---
        mediaController = new MediaController();
        playlistManager = new PlaylistManager();

        // ============================================================
        // HEADER BAR — brand at top of window (no emoji logo)
        // ============================================================
        Label appTitle = new Label("JOEL MEDIA PLAYER");
        appTitle.getStyleClass().add("app-title");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        nowPlayingLabel = new Label("No media loaded");
        nowPlayingLabel.getStyleClass().add("now-playing");
        nowPlayingLabel.setMaxWidth(400);

        HBox headerBar = new HBox(12, appTitle, headerSpacer, nowPlayingLabel);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.getStyleClass().add("header-bar");
        headerBar.setPadding(new Insets(12, 18, 12, 18));

        // --- Video display ---
        MediaView mediaView = new MediaView();
        mediaView.setFitWidth(680);
        mediaView.setFitHeight(380);
        mediaView.setPreserveRatio(true);

        ImageView musicPlaceholder = new ImageView();
        var musicUrl = getClass().getResource("/icons/music.png");
        if (musicUrl != null) {
            musicPlaceholder.setImage(new Image(musicUrl.toExternalForm()));
        }
        musicPlaceholder.setFitWidth(320);
        musicPlaceholder.setFitHeight(320);
        musicPlaceholder.setPreserveRatio(true);
        musicPlaceholder.setOpacity(0.95);
        musicPlaceholder.setVisible(false);

        Label emptyHint = new Label("Add a file to get started");
        emptyHint.getStyleClass().add("empty-hint");

        StackPane videoPane = new StackPane(emptyHint, musicPlaceholder, mediaView);
        videoPane.getStyleClass().add("video-pane");
        videoPane.setFocusTraversable(true);
        videoPane.setPrefSize(680, 380);

        // --- Playlist ---
        playlistView = new ListView<>(playlistManager.getPlaylist());
        playlistView.setPrefWidth(280);
        playlistView.setPrefHeight(220);
        playlistView.getStyleClass().add("playlist-view");

        Label playlistLabel = new Label("PLAYLIST");
        playlistLabel.getStyleClass().add("section-title");

        VBox playlistBox = new VBox(8, playlistLabel, playlistView);
        playlistBox.getStyleClass().add("panel");
        VBox.setVgrow(playlistView, Priority.ALWAYS);

        // Right panel
        VBox rightPanel = new VBox(12, playlistBox);
        rightPanel.setPadding(new Insets(0, 0, 0, 12));
        rightPanel.setPrefWidth(300);

        // --- Buttons ---
        Button playBtn   = iconButton("/icons/play.png",     "Play",   true);
        Button pauseBtn  = iconButton("/icons/pause.png",    "Pause",  false);
        Button stopBtn   = iconButton("/icons/stop.png",     "Stop",   false);
        Button prevBtn   = iconButton("/icons/previous.png", "Prev",   false);
        Button nextBtn   = iconButton("/icons/next.png",     "Next",   false);
        Button addBtn    = iconButton("/icons/add.png",      "Add",    false);
        Button removeBtn = iconButton("/icons/remove.png",   "Remove", false);

        playBtn.getStyleClass().addAll("icon-button", "primary", "big");
        pauseBtn.getStyleClass().addAll("icon-button");
        stopBtn.getStyleClass().addAll("icon-button", "danger");
        prevBtn.getStyleClass().addAll("icon-button");
        nextBtn.getStyleClass().addAll("icon-button");
        addBtn.getStyleClass().addAll("icon-button", "success");
        removeBtn.getStyleClass().addAll("icon-button", "danger");

        // --- Volume ---
        volumeSlider = new Slider(0, 1, 0.7);
        volumeSlider.setPrefWidth(160);
        volumeSlider.setFocusTraversable(false);

        Label volumeIcon = new Label("\uD83D\uDD0A");
        volumeIcon.getStyleClass().add("volume-icon");

        // --- Time slider + label ---
        timeSlider = new Slider(0, 100, 0);
        timeSlider.setFocusTraversable(false);

        timeLabel = new Label("00:00 / 00:00");
        timeLabel.getStyleClass().add("time-label");
        timeLabel.setMinWidth(100);

        HBox timeBox = new HBox(12, timeSlider, timeLabel);
        timeBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMaxWidth(Double.MAX_VALUE);

        // --- Control row ---
        HBox controlRow = new HBox(10,
                playBtn, pauseBtn, stopBtn,
                makeSeparator(),
                prevBtn, nextBtn,
                makeSeparator(),
                addBtn, removeBtn,
                makeSeparator(),
                volumeIcon, volumeSlider
        );
        controlRow.setAlignment(Pos.CENTER);
        controlRow.setPadding(new Insets(4, 0, 0, 0));

        VBox bottomBox = new VBox(12, timeBox, controlRow);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getStyleClass().add("controls-panel");
        bottomBox.setPadding(new Insets(16));

        // --- Root layout ---
        BorderPane root = new BorderPane();
        root.setTop(headerBar);
        root.setCenter(videoPane);
        root.setRight(rightPanel);
        root.setBottom(bottomBox);
        root.setPadding(new Insets(12, 16, 16, 16));
        root.getStyleClass().add("root");

        BorderPane.setMargin(videoPane, new Insets(12, 0, 12, 0));

        Scene scene = new Scene(root, 1180, 760);

        // --- Connect controllers ---
        mediaController.attach(mediaView, volumeSlider, timeSlider, timeLabel);
        mediaController.setMusicPlaceholder(musicPlaceholder);
        playlistManager.attach(stage, playlistView, mediaController);

        // Update "Now Playing" when playlist selection changes
        playlistView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) -> {
                    if (newV != null) {
                        nowPlayingLabel.setText("Now Playing:  " + newV);
                    } else {
                        nowPlayingLabel.setText("No media loaded");
                    }
                });

        // --- Button actions ---
        playBtn.setOnAction(e -> { mediaController.play(); videoPane.requestFocus(); });
        pauseBtn.setOnAction(e -> { mediaController.pause(); videoPane.requestFocus(); });
        stopBtn.setOnAction(e -> { mediaController.stop(); videoPane.requestFocus(); });
        addBtn.setOnAction(e -> playlistManager.addFile());
        removeBtn.setOnAction(e -> playlistManager.removeSelected());
        prevBtn.setOnAction(e -> playlistManager.playPrevious());
        nextBtn.setOnAction(e -> playlistManager.playNext());

        // --- Keyboard ---
        KeyboardController keyboard = new KeyboardController(mediaController, playlistManager);
        keyboard.attach(scene);

        // --- Stylesheet ---
        var cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("Joel Media Player");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        videoPane.requestFocus();
        stage.show();
    }

    private Region makeSeparator() {
        Region sep = new Region();
        sep.getStyleClass().add("control-separator");
        sep.setMinWidth(1);
        sep.setPrefWidth(1);
        sep.setMaxWidth(1);
        sep.setMinHeight(24);
        sep.setPrefHeight(24);
        return sep;
    }

    private Button iconButton(String resourcePath, String text, boolean big) {
        Button btn = new Button(text);
        var url = getClass().getResource(resourcePath);
        if (url != null) {
            ImageView iv = new ImageView(new Image(url.toExternalForm()));
            iv.setFitWidth(big ? 22 : 18);
            iv.setFitHeight(big ? 22 : 18);
            iv.setPreserveRatio(true);
            btn.setGraphic(iv);
        }
        return btn;
    }
}