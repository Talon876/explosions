package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.utils.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class Win implements Screen {

    private Stage stage;
    private Image winImage;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update((int) Config.WIDTH, (int) Config.HEIGHT);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Config.WIDTH, Config.HEIGHT));

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.ENTER || keycode == Keys.ESCAPE) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                        }
                    })));
                }
                return false;
            }
        }));

        Texture backgroundTexture = new Texture("backgrounds/title.png");
        Image background = new Image(backgroundTexture);
        background.setPosition(0, 0);
        background.setFillParent(true);
        stage.addActor(background);

        winImage = new Image(new Texture("images/winmessage.png"));
        winImage.setPosition(Config.WIDTH / 2 - winImage.getWidth() / 2, Config.HEIGHT / 2 - winImage.getHeight() / 2);
        stage.addActor(winImage);
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
