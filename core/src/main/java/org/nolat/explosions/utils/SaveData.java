package org.nolat.explosions.utils;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.stackmob.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.exception.StackMobException;

public class SaveData {

    private static final int SAVE_VERSION = 1;
    private static final Preferences prefs = Gdx.app.getPreferences(Config.NAME + (Config.debug ? ".debug" : SAVE_VERSION));
    //preference strings
    private static final String LEVELS_UNLOCKED = "unlocked";
    private static final String LEVEL_DATA = "level_data_"; //append level # when using

    private static final String PLAYER_ID = "player_id";

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
    public static void saveLevelsUnlocked(final int level) {
        if (getLevelsUnlocked() < level) {
            prefs.putInteger(LEVELS_UNLOCKED, level);
            prefs.flush();
            final Player player = Player.getExistingPlayer();
            player.fetch(new StackMobCallback() {
                @Override
                public void success(String responseBody) {
                    player.setLevelsComplete(level);
                    player.save();
                }

                @Override
                public void failure(StackMobException e) {
                }
            });
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
     * Saves the score only if it was higher than the previous and updates global total high score
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
            SaveData.saveTotalScore(SaveData.getTotalScore());
        }
    }

    /**
     * Saves the player id
     * 
     * @param playerId
     *            the player id to save
     */
    public static void savePlayerId(String playerId) {
        prefs.putString(PLAYER_ID, playerId);
        prefs.flush();
    }

    /**
     * Gets the player id if it exists
     * 
     * @return the player id if it exists, otherwise returns null
     */
    public static String getPlayerId() {
        return prefs.getString(PLAYER_ID, null);
    }

    /**
     * Removes the player id from the save file after saving the id in a backup key called "PLAYER_ID" + "_backup"
     */
    public static void deletePlayerId() {
        prefs.putString(PLAYER_ID + "_backup", getPlayerId());
        prefs.remove(PLAYER_ID);
        prefs.flush();
    }

    /**
     * Calculates the total score
     * 
     * @return the total score
     */
    public static int getTotalScore() {
        int score = 0;
        for (int i = 0; i < LevelInfo.getNumberOfLevels(); i++) {
            score += getLevelScore(i);
        }
        return score;
    }

    /**
     * Saves the total score if the score on the server is lower
     * 
     * @param score
     *            the new score
     */
    public static void saveTotalScore(final int score) {
        final Player player = Player.getExistingPlayer();
        player.fetch(new StackMobCallback() {
            @Override
            public void success(String responseBody) {
                if (player.getScore() < score) {
                    player.setScore(score);
                    player.save();
                }
            }

            @Override
            public void failure(StackMobException e) {
            }
        });
    }

    private SaveData() {
    }
}
