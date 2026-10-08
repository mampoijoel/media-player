package com.example.mediaplayer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;

public class MediaController {

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Slider volumeSlider;
    private Slider timeSlider;
    private Label timeLabel;
    private ImageView musicPlaceholder;
    private double currentVolume = 0.7;

   
    public void attach(MediaView mediaView,
                       Slider volumeSlider,
                       Slider timeSlider,
                       Label timeLabel) {
        this.mediaView = mediaView;
        this.volumeSlider = volumeSlider;
        this.timeSlider = timeSlider;
        this.timeLabel = timeLabel;

        
        volumeSlider.valueProperty().addListener((obs, oldV, newV) -> {
            currentVolume = newV.doubleValue();
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(currentVolume);
            }
        });

        currentVolume = volumeSlider.getValue();
    }

   
    public void setMusicPlaceholder(ImageView placeholder) {
        this.musicPlaceholder = placeholder;
    }

    /** Returns true for common audio extensions (no video stream). */
    private boolean isAudioFile(String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".mp3")
                || lower.endsWith(".wav")
                || lower.endsWith(".m4a")
                || lower.endsWith(".aac");
    }

    /** Load a file, build a new MediaPlayer, and play it. */
    public void load(File file) {
        if (file == null) return;

        stop();
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaView.setMediaPlayer(mediaPlayer);

        // Show music icon for audio files
        if (musicPlaceholder != null) {
            musicPlaceholder.setVisible(isAudioFile(file.getName()));
        }

        mediaPlayer.setVolume(currentVolume);

        mediaPlayer.currentTimeProperty().addListener((obs, oldT, newT) -> {
            if (!timeSlider.isValueChanging()) {
                Duration total = mediaPlayer.getTotalDuration();
                if (total != null && total.toMillis() > 0) {
                    timeSlider.setValue(newT.toMillis() / total.toMillis() * 100.0);
                }
                timeLabel.setText(format(newT) + " / " + format(total));
            }
        });

        // When ready, load embedded album art and auto-play
        mediaPlayer.setOnReady(() -> {
            if (isAudioFile(file.getName())) {
                loadEmbeddedArtwork(media);
            }
            mediaPlayer.play();
        });
    }

   //Loads embedded album artwork from the MP3's ID3 tag.
     
    private void loadEmbeddedArtwork(Media media) {
        if (musicPlaceholder == null) return;

        // Try embedded artwork first
        if (media.getMetadata().containsKey("image")) {
            Object art = media.getMetadata().get("image");
            if (art instanceof Image) {
                setArtwork((Image) art);
                return;
            }
        }

        // Fallback: default music icon
        var defaultUrl = getClass().getResource("/icons/music.png");
        if (defaultUrl != null) {
            setArtwork(new Image(defaultUrl.toExternalForm()));
        }
    }

    /** Applies an Image to the placeholder at a fixed size. */
    private void setArtwork(Image img) {
        musicPlaceholder.setImage(img);
        musicPlaceholder.setFitWidth(320);
        musicPlaceholder.setFitHeight(320);
        musicPlaceholder.setPreserveRatio(true);
    }

    public void play() {
        if (mediaPlayer != null &&
                mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public void toggleMute() {
        if (mediaPlayer != null) {
            mediaPlayer.setMute(!mediaPlayer.isMute());
        }
    }

    public void increaseVolume() {
        currentVolume = Math.min(1.0, currentVolume + 0.05);
        applyVolume();
    }

    public void decreaseVolume() {
        currentVolume = Math.max(0.0, currentVolume - 0.05);
        applyVolume();
    }

    private void applyVolume() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(currentVolume);
        }
        volumeSlider.setValue(currentVolume);
    }

    private String format(Duration d) {
        if (d == null) return "00:00";
        int totalSec = (int) d.toSeconds();
        int m = totalSec / 60;
        int s = totalSec % 60;
        return String.format("%02d:%02d", m, s);
    }
}