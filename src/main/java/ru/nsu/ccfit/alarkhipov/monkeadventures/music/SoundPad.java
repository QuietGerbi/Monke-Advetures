package ru.nsu.ccfit.alarkhipov.monkeadventures.music;

import javazoom.jl.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundPad {

    private static final Logger log = LogManager.getLogger(SoundPad.class);
    private Player currentPlayer;
    private Thread musicThread;
    private List<String> currentPlaylist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean isMuted = false;
    private boolean isBossFight = false;

    private List<String> playlist1 = new ArrayList<>();
    private List<String> playlist2 = new ArrayList<>();

    public void setPlaylist1(List<String> tracks, boolean shuffle) {
        this.playlist1 = new ArrayList<>(tracks);
        if (shuffle) Collections.shuffle(this.playlist1);
    }

    public void setPlaylist2(List<String> tracks, boolean shuffle) {
        this.playlist2 = new ArrayList<>(tracks);
        if (shuffle) Collections.shuffle(this.playlist2);
    }

    public void start() {
        musicThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (isMuted) {
                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                    continue;
                }

                String trackToPlay = isBossFight
                        ? playlist2.get(currentTrackIndex % playlist2.size())
                        : playlist1.get(currentTrackIndex % playlist1.size());

                try {
                    playSingleTrack(trackToPlay);
                } catch (Exception e) {
                    log.error("Sorry, track hasn't been played");
                }
                currentTrackIndex++;
            }
        });
        musicThread.setDaemon(true);
        musicThread.start();
    }

    private void playSingleTrack(String path) throws Exception {
        stopCurrentTrack();

        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            return;
        }

        BufferedInputStream bis = new BufferedInputStream(is);
        currentPlayer = new Player(bis);
        currentPlayer.play();
    }

    public void nextTrack() {
        currentTrackIndex++;
        stopCurrentTrack();
    }

    public void previousTrack() {
        currentTrackIndex = Math.max(0, currentTrackIndex - 1);
        stopCurrentTrack();
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopCurrentTrack();
        } else {
            currentTrackIndex = Math.max(0, currentTrackIndex - 1);
        }
    }

    public void switchToBossMusic() {
        isBossFight = true;
        currentTrackIndex = 0;
        stopCurrentTrack();
    }

    public void stopCurrentTrack() {
        if (currentPlayer != null) {
            try {
                currentPlayer.close();
            } catch (Exception ignored) {}
            currentPlayer = null;
        }
    }

    public void stopAll() {
        isMuted = true;
        stopCurrentTrack();
        if (musicThread != null) {
            musicThread.interrupt();
        }
    }
}
