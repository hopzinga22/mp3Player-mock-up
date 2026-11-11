package mp3;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class mp3_controller implements Initializable {

    @FXML
    private Pane pane;
    @FXML
    private Label songDisplay;
    @FXML 
    private Button playBtn, pauseBtn, stopBtn, fowBtn, prevBtn;
    @FXML
    private ProgressBar songProgress;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    private Slider volumeSlider;

    private File directory;
    private File[] files;
    private ArrayList<File> song;

    private int songNumber; 
    private boolean playing;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        song = new ArrayList<File>();
        File projectDir = new File("music");
        directory = projectDir.getAbsoluteFile();
        files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                    song.add(file);
                    System.out.println(file.getName());
                }
            }
        }

        if (volumeSlider != null) {
            volumeSlider.setValue(50.0);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> volume());
        }

        if (song.isEmpty()) {
            if (songDisplay != null) {
                songDisplay.setText("No MP3 files found");
            }
            return;
        }

        loadSong(songNumber);
    }

    private void loadSong(int index) {
        if (song == null || song.isEmpty()) {
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        File selectedFile = song.get(index);
        media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volumeSlider != null ? volumeSlider.getValue() / 100.0 : 0.5);
        mediaPlayer.setOnEndOfMedia(this::playNextSong);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> songProgress());

        if (songDisplay != null) {
            songDisplay.setText(selectedFile.getName());
        }

        if (songProgress != null) {
            songProgress.setProgress(0.0);
        }
    }

    public void play() {
        if (mediaPlayer == null) {
            if (songDisplay != null) {
                songDisplay.setText("No MP3 file loaded");
            }
            return;
        }
        mediaPlayer.play();
        playing = true;
    }

    public void pause() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.pause();
        playing = false;
    }

    public void stop() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        playing = false;
        if (songProgress != null) {
            songProgress.setProgress(0.0);
        }
    }

    public void foward() {
        playNextSong();
    }

    public void previous() {
        if (song == null || song.isEmpty()) {
            return;
        }

        songNumber = (songNumber - 1 + song.size()) % song.size();
        loadSong(songNumber);
        if (playing) {
            mediaPlayer.play();
        }
    }

    public void playNextSong() {
        if (song == null || song.isEmpty()) {
            return;
        }

        songNumber = (songNumber + 1) % song.size();
        loadSong(songNumber);
        if (playing || mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void songProgress() {
        if (mediaPlayer == null || mediaPlayer.getTotalDuration() == null || mediaPlayer.getTotalDuration().isUnknown()) {
            return;
        }

        double total = mediaPlayer.getTotalDuration().toMillis();
        if (total <= 0) {
            return;
        }

        double current = mediaPlayer.getCurrentTime().toMillis();
        double progress = Math.min(1.0, Math.max(0.0, current / total));
        if (songProgress != null) {
            songProgress.setProgress(progress);
        }
    }

    public void volume() {
        if (mediaPlayer != null && volumeSlider != null) {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
        }
    }
}
