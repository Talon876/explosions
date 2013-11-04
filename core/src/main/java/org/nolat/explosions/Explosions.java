package org.nolat.explosions;

import org.nolat.explosions.screens.LevelMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Explosions extends Game {

    private Music backgroundMusic;

    @Override
    public void create() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/zapBeat.ogg"));
        backgroundMusic.setLooping(true);
        setScreen(new LevelMenu(0));
        backgroundMusic.setVolume(0.2f);
        //        backgroundMusic.play();
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
