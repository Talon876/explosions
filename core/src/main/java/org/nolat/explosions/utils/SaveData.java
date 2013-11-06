package org.nolat.explosions.utils;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public class SaveData {

    private static final Preferences prefs = Gdx.app.getPreferences(Config.NAME);
    //preference strings
    private static final String LEVELS_UNLOCKED = "unlocked";
    private static final String LEVEL_DATA = "level_data_"; //append level # when using

    /**
     * 
     * @return the number of levels unlocked, clamped between 0 and the max value
     */
    public static int getLevelsUnlocked() {
        return MathUtils.clamp(prefs.getInteger(LEVELS_UNLOCKED, 0), 0, LevelInfo.getNumberOfLevels() - 1);
    }

    /**
     * Saves level unlocked only if the current level is less than the value passed in
     * 
     * @param level
     *            the level to save
     */
    public static void saveLevelsUnlocked(int level) {
        if (getLevelsUnlocked() < level) {
            prefs.putInteger(LEVELS_UNLOCKED, level);
            prefs.flush();
        }
    }

    /**
     * 
     * @param level
     *            level to get score for
     * @return the score for the level
     */
    public static int getLevelScore(int level) {
        return prefs.getInteger(LEVEL_DATA + level, 0);
    }

    /**
     * Saves the score only if it was higher than the previous
     * 
     * @param level
     *            level to save score for
     * @param score
     *            the score
     */
    public static void saveLevelScore(int level, int score) {
        if (getLevelScore(level) < score) {
            prefs.putInteger(LEVEL_DATA + level, score);
            prefs.flush();
        }
    }

    private SaveData() {
    }

}
