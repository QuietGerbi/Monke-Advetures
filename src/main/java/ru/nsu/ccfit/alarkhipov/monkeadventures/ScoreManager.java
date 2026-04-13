package ru.nsu.ccfit.alarkhipov.monkeadventures;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ScoreManager {
    private static final String BEST_TIME_KEY = "best_time_seconds";
    private static final Logger log = LogManager.getLogger(ScoreManager.class);
    private final Preferences prefs = Preferences.userNodeForPackage(ScoreManager.class);

    public void saveIfHigher(long currentSeconds) {
        long bestSeconds = prefs.getLong(BEST_TIME_KEY, 0);
        if (currentSeconds > bestSeconds) {
            prefs.putLong(BEST_TIME_KEY, currentSeconds);
        }
        try {
            prefs.flush();
        } catch (BackingStoreException _) {
            log.info("Something went wrong with saving stats of the game");
        }
    }

    public String getBestTimeFormatted() {
        long totalSeconds = prefs.getLong(BEST_TIME_KEY, 0);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}