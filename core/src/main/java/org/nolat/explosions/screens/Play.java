package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.entities.CompletionText;
import org.nolat.explosions.entities.Explosion;
import org.nolat.explosions.entities.Explosion.ExplosionState;
import org.nolat.explosions.entities.HUD;
import org.nolat.explosions.utils.InputAdapter;
import org.nolat.explosions.utils.SaveData;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Play implements Screen {

    private Stage stage;
    private final LevelInfo levelInfo;
    private HUD hud;
    private BitmapFont hudFont;
    private BitmapFont barFont;
    private BitmapFont completionFont;

    private int numDestroyed = 0;
    private boolean seedPlaced = false;

    private CompletionText completionText;

    public Play(LevelInfo info) {
        levelInfo = info;
        if (Config.debug) {
            Gdx.app.log("Play", "Level " + (info.level + 1) + ": Explode " + info.numNeededToPass + " out of "
                    + info.numTotal);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        hud.update(numDestroyed);
        stage.act(delta);
        stage.draw();
        checkGameCondition();
    }

    private void checkGameCondition() {
        if (seedPlaced) { //only check once the initial seed has been placed
            if (!isExplosionsHappening()) { //wait until no more explosions
                completionText.setWinning(!hasLost());
                if (completionText.getActions().size == 0) { //don't re-add actions
                    //@formatter:off
                    completionText.addAction(
                            Actions.sequence(
                                    Actions.parallel(
                                            Actions.alpha(1f, 1f),
                                            Actions.scaleTo(2f, 2f, 1f),
                                            Actions.rotateBy(720, 1f)
                                            ),
                                            Actions.delay(0.5f),
                                            Actions.parallel(
                                                    Actions.scaleTo(0f, 0f, 1.5f),
                                                    Actions.moveBy(0, Gdx.graphics.getHeight() / 2, 1.25f),
                                                    Actions.fadeOut(1.25f),
                                                    Actions.run(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (hasLost()) {
                                                                stage.addAction(Actions.sequence(Actions.fadeOut(1.25f), Actions.run(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(levelInfo));
                                                                    }
                                                                })));
                                                            } else {
                                                                advanceToNextLevel();
                                                            }
                                                        }

                                                    }))
                                    )
                            );
                    //@formatter:on
                }
            }
        }
    }

    private boolean isExplosionsHappening() {
        boolean exploding = false;
        Actor[] actors = stage.getRoot().getChildren().begin();
        for (int i = 0, n = stage.getRoot().getChildren().size; i < n; i++) {
            if (actors[i] instanceof Explosion) {
                Explosion exp = (Explosion) actors[i];
                if (exp.getState() == ExplosionState.EXPLODING) {
                    exploding = true;
                }
            }
        }
        stage.getRoot().getChildren().end();
        return exploding;
    }

    private boolean hasLost() {
        boolean lost = seedPlaced && numDestroyed < levelInfo.numNeededToPass;
        return lost;
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        stage = new Stage();
        final Texture explosionTexture = new Texture("images/disc256.png");
        explosionTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        final Sound popFx = Gdx.audio.newSound(Gdx.files.internal("sfx/pop.ogg"));
        final Sound puffFx = Gdx.audio.newSound(Gdx.files.internal("sfx/puff.ogg"));

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (!seedPlaced) {
                    Explosion seed = new Explosion(getBounds(), explosionTexture, puffFx, popFx);
                    seed.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
                    seed.explode();
                    stage.addActor(seed);
                    seedPlaced = true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                case Keys.F10:
                    HUD.showFps = !HUD.showFps;
                    break;
                case Keys.F12:
                    advanceToNextLevel();
                    break;
                case Keys.BACK:
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

        //set initial explosions
        Rectangle bounds = getBounds();
        for (int i = 0; i < levelInfo.numTotal; i++) {
            Explosion exp = new Explosion(getBounds(), explosionTexture, puffFx, popFx);
            float randomX = MathUtils.random(bounds.x + exp.getWidth(), bounds.x + bounds.width - exp.getWidth());
            float randomY = MathUtils.random(bounds.y + exp.getHeight(), bounds.y + bounds.height - exp.getHeight());
            exp.setPosition(randomX, randomY);
            exp.setDeathAction(new Runnable() {
                @Override
                public void run() {
                    numDestroyed++;
                }
            });
            stage.addActor(exp);
        }

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));

        //HUD
        hudFont = Config.generateFont("fonts/minecraftia.ttf", 16, Color.BLACK);
        barFont = Config.generateFont("fonts/quadrats.ttf", 16, Color.BLACK);
        completionFont = Config.generateFont("fonts/quadrats.ttf", 42, Color.BLACK);
        hud = new HUD(hudFont, barFont, levelInfo, bounds, 2f);
        stage.addActor(hud);

        //Completion text
        final Texture winTexture = new Texture("images/win.png"), failTexture = new Texture("images/fail.png");
        completionText = new CompletionText(winTexture, failTexture);
        stage.addActor(completionText);

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
        barFont.dispose();
        completionFont.dispose();
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

    private void advanceToNextLevel() {
        final LevelInfo nextLevel = LevelInfo.getLevelInfo(levelInfo.level + 1);
        if (nextLevel != null) {
            stage.addAction(Actions.sequence(Actions.fadeOut(1.25f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    //save progress if this is the highest level unlocked
                    SaveData.saveLevelsUnlocked(nextLevel.level);
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(nextLevel));
                }
            })));
        } else {
            //TODO go to a win screen
            stage.addAction(Actions.sequence(Actions.fadeOut(1.25f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                }
            })));
        }
    }
}
