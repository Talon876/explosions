package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.InputAdapter;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.entities.Explosion;
import org.nolat.explosions.entities.HUD;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Play implements Screen {

    private Stage stage;
    private final LevelInfo levelInfo;
    private HUD hud;
    private BitmapFont hudFont;
    private FPSLogger fps;

    public Play(LevelInfo info) {
        levelInfo = info;
        System.out.println("Level " + (info.level + 1) + ": Explode " + info.numNeededToPass + " out of "
                + info.numTotal);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        fps.log();

    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        fps = new FPSLogger();
        stage = new Stage();
        final Texture explosionTexture = new Texture("images/disc256.png");

        Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Explosion test = new Explosion(getBounds(), explosionTexture);
                test.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
                test.velocity.set(Vector2.Zero);
                stage.addActor(test);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Explosion test = new Explosion(getBounds(), explosionTexture);
                test.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
                test.velocity.set(Vector2.Zero);
                stage.addActor(test);
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                case Keys.ESCAPE:
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu(levelInfo.level));
                        }
                    })));
                    break;
                }
                return false;
            }
        }, stage));

        setupBackground();
        Rectangle bounds = getBounds();
        for (int i = 0; i < levelInfo.numTotal; i++) {
            Explosion exp = new Explosion(getBounds(), explosionTexture);
            float randomX = MathUtils.random(bounds.x + exp.getWidth(), bounds.x + bounds.width - exp.getWidth());
            float randomY = MathUtils.random(bounds.y + exp.getHeight(), bounds.y + bounds.height - exp.getHeight());
            exp.setPosition(randomX, randomY);
            stage.addActor(exp);
        }

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));

        //HUD
        hudFont = Config.generateFont("fonts/minecraftia.ttf", 16, Color.BLACK);
        hud = new HUD(hudFont, levelInfo, bounds.x + 4, bounds.y + bounds.height - 6, 2f);
        stage.addActor(hud);

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
        hudFont.dispose();
    }

    private void setupBackground() {
        Texture backgroundTexture = new Texture("backgrounds/title.png");
        Image background = new Image(backgroundTexture);
        background.setPosition(0, 0);
        background.setFillParent(true);
        stage.addActor(background);
    }

    public Rectangle getBounds() {
        //magic numbers represent border in background image
        return new Rectangle(16, 8, Gdx.graphics.getWidth() - 32, Gdx.graphics.getHeight() - 16);
    }

}
