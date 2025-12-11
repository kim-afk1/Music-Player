import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.IOException;

public class PlaybackController {
    private AudioPlayer player;
    private List<Song> currentPlaylist;
    private int currentIndex = -1;
    private boolean shuffle = false;
    private boolean repeat = false;
    private List<Integer> shuffleOrder;
    private javax.swing.Timer progressTimer;
    private List<PlaybackListener> listeners;

    public PlaybackController() {
        player = AudioPlayer.getInstance();
        currentPlaylist = new ArrayList<>();
        listeners = new ArrayList<>();
        shuffleOrder = new ArrayList<>();

        progressTimer = new javax.swing.Timer(100, e -> notifyProgressUpdate());
    }

    public void setPlaylist(List<Song> playlist) {
        currentPlaylist = new ArrayList<>(playlist);
        generateShuffleOrder();
        if (!currentPlaylist.isEmpty()) {
            currentIndex = 0;
        }
    }

    public void playSong(int index) {
        if (index >= 0 && index < currentPlaylist.size()) {
            currentIndex = index;
            try {
                Song song = getCurrentSong();
                player.loadSong(song.getFile());
                player.play();
                progressTimer.start();
                notifyListeners();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error playing song: " + e.getMessage(),
                        "Playback Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            progressTimer.stop();
        } else {
            player.play();
            progressTimer.start();
        }
        notifyListeners();
    }

    public void next() {
        if (currentPlaylist.isEmpty()) return;

        if (shuffle) {
            int currentShufflePos = shuffleOrder.indexOf(currentIndex);
            int nextShufflePos = (currentShufflePos + 1) % shuffleOrder.size();
            playSong(shuffleOrder.get(nextShufflePos));
        } else {
            playSong((currentIndex + 1) % currentPlaylist.size());
        }
    }

    public void previous() {
        if (currentPlaylist.isEmpty()) return;

        if (shuffle) {
            int currentShufflePos = shuffleOrder.indexOf(currentIndex);
            int prevShufflePos = (currentShufflePos - 1 + shuffleOrder.size()) % shuffleOrder.size();
            playSong(shuffleOrder.get(prevShufflePos));
        } else {
            playSong((currentIndex - 1 + currentPlaylist.size()) % currentPlaylist.size());
        }
    }

    public void toggleShuffle() {
        shuffle = !shuffle;
        if (shuffle) {
            generateShuffleOrder();
        }
        notifyListeners();
    }

    public void toggleRepeat() {
        repeat = !repeat;
        notifyListeners();
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public Song getCurrentSong() {
        return currentIndex >= 0 && currentIndex < currentPlaylist.size() ?
                currentPlaylist.get(currentIndex) : null;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    private void generateShuffleOrder() {
        shuffleOrder.clear();
        for (int i = 0; i < currentPlaylist.size(); i++) {
            shuffleOrder.add(i);
        }
        Collections.shuffle(shuffleOrder);
    }

    public void addListener(PlaybackListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (PlaybackListener listener : listeners) {
            listener.onPlaybackStateChanged();
        }
    }

    private void notifyProgressUpdate() {
        for (PlaybackListener listener : listeners) {
            listener.onProgressUpdate();
        }

        if (player.getDuration() > 0 && player.getCurrentPosition() >= player.getDuration() - 100000) {
            if (repeat) {
                player.setPosition(0);
                player.play();
            } else {
                next();
            }
        }
    }

    public interface PlaybackListener {
        void onPlaybackStateChanged();
        void onProgressUpdate();
    }
}