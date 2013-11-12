package org.nolat.explosions.stackmob;

import org.nolat.explosions.Config;
import org.nolat.explosions.utils.SaveData;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.exception.StackMobException;
import com.stackmob.sdk.model.StackMobModel;

public class Launch extends StackMobModel {

    private final String appType;
    private final boolean isDebug;
    private final String javaVersion;
    private final String osName;
    private final String osVersion;
    private final String osArch;
    private final String explosionsVersion;
    private Player player;

    public Launch(String appType, boolean isDebug, String javaVersion, String osName, String osVersion, String osArch,
            String explosionsVersion, Player player) {
        super(Launch.class);
        this.appType = appType;
        this.isDebug = isDebug;
        this.javaVersion = javaVersion;
        this.osName = osName;
        this.osVersion = osVersion;
        this.osArch = osArch;
        this.explosionsVersion = explosionsVersion;
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static Launch createDefaultLaunch(Player player) {
        return new Launch(Gdx.app.getType().toString(), Config.debug, System.getProperty("java.version"),
                System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"),
                Config.VERSION, player);
    }

    public String getAppType() {
        return appType;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getOsName() {
        return osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public String getExplosionsVersion() {
        return explosionsVersion;
    }

    public Player getPlayer() {
        return player;
    }

    public static void trackAppLaunch() {
        final String playerId = SaveData.getPlayerId(); //should only be null the first time this is ran (when no pref file exists)
        final Player player = new Player();
        player.setID(playerId);

        player.fetch(new StackMobCallback() {
            @Override
            public void success(String responseBody) { //player exists on server
                player.incrementTimesPlayed();
                player.save();
                final Launch launch = Launch.createDefaultLaunch(player);
                launch.save();
            }

            @Override
            public void failure(StackMobException e) { //player doesn't exist on server
                final Player newPlayer = new Player();
                if (Gdx.app.getType() == ApplicationType.Desktop) {
                    newPlayer.setName(System.getProperty("user.name"));
                }
                newPlayer.save(new StackMobCallback() { //save player to server
                    @Override
                    public void success(String responseBody) {
                        if (Config.debug) {
                            Gdx.app.log("StackMob", "Created new player with id: " + newPlayer.getID());
                        }
                        final Launch launch = Launch.createDefaultLaunch(newPlayer);
                        launch.save();
                        SaveData.savePlayerId(newPlayer.getID()); //now we have an id
                    }

                    @Override
                    public void failure(StackMobException e) {
                        if (Config.debug) {
                            Gdx.app.log("StackMob", "Failed to create a new player. No internet connection?");
                        }
                    }
                });
            }
        });
    }
}
