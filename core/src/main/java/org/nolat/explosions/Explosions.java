package org.nolat.explosions;

import org.nolat.explosions.screens.LevelMenu;
import org.nolat.explosions.screens.Splash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Explosions extends Game {

    private Music backgroundMusic;

    @Override
    public void create() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/cherryBlossom.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.2f);
        if (!Config.debug) {
            backgroundMusic.play();
            setScreen(new Splash());
        } else { //if debugging go directly to level menu
            setScreen(new LevelMenu());
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
