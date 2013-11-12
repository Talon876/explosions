package org.nolat.explosions.stackmob;

import org.nolat.explosions.Config;
import org.nolat.explosions.utils.SaveData;

import com.badlogic.gdx.Gdx;
import com.stackmob.sdk.model.StackMobModel;

public class Player extends StackMobModel {

    private String name;
    private int timesPlayed;
    private boolean hasKonami;
    private int levelsComplete;

    public Player(String name, int timesPlayed, boolean hasKonami) {
        super(Player.class);
        this.name = name;
        this.timesPlayed = timesPlayed;
        this.hasKonami = hasKonami;
    }

    public Player() {
        this("Anonymous", 1, false);
    }

    /**
     * Updates times played by one and saves the data to the server. Make sure this is called on a refreshed Player object.
     */
    public void incrementTimesPlayed() {
        timesPlayed++;
        save();
        if (Config.debug) {
            Gdx.app.log("StackMob", "Setting timesPlayed to " + timesPlayed);
        }
    }

    public void enableKonami() {
        hasKonami = true;
        save();
    }

    /**
     * Gets the existing player who's id is saved in the preferences. If one does not exist, one is created and sent to the server.<br>
     * 
     * @return a Player object with the correct id. It will need to be refreshed from the server before performing operations on its data.
     */
    public static Player getExistingPlayer() {
        final String playerId = SaveData.getPlayerId();
        final Player player = new Player();
        if (playerId != null) { //we have a player id, fetch full object from server
            player.setID(playerId);
        }
        return player;
    }

    public String getName() {
        return name;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public boolean isHasKonami() {
        return hasKonami;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevelsComplete() {
        return levelsComplete;
    }

    public void setLevelsComplete(int levelsComplete) {
        this.levelsComplete = levelsComplete;
    }

}
