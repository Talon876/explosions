package org.nolat.explosions;

import org.nolat.explosions.screens.MainMenu;
import org.nolat.explosions.screens.Splash;
import org.nolat.explosions.stackmob.APIVersion;
import org.nolat.explosions.stackmob.Launch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.stackmob.sdk.api.StackMob;

public class Explosions extends Game {

    private Music backgroundMusic;

    @Override
    public void create() {
        StackMob.setStackMob(new StackMob(APIVersion.DEVELOPMENT.ordinal(), "859efe9c-801c-4d85-8e5f-30d25a4393ec"));
        Launch.trackAppLaunch();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/cherryBlossom.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.2f);
        if (!Config.debug) {
            backgroundMusic.play();
            setScreen(new Splash());
        } else { //if debugging go directly to main menu
            Gdx.app.log("StackMob", "StackMob v" + StackMob.getVersion());
            setScreen(new MainMenu());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        backgroundMusic.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

}
