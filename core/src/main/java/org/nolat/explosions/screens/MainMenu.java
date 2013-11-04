package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.InputAdapter;
import org.nolat.explosions.KonamiInputDetector;
import org.nolat.explosions.entities.Explosion;
import org.nolat.explosions.tween.ActorAccessor;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu implements Screen {

    private Stage stage;
    private Skin skin;
    private Table table;
    private BitmapFont buttonFont, titleFont, textFont;
    private TweenManager tweenManager;

    private Sound rolloverSfx, badingSfx;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        tweenManager.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        table.invalidateHierarchy();
    }

    @Override
    public void show() {
        stage = new Stage();
        rolloverSfx = Gdx.audio.newSound(Gdx.files.internal("sfx/rollover.ogg"));
        badingSfx = Gdx.audio.newSound(Gdx.files.internal("sfx/bading.ogg"));

        Texture backgroundTexture = new Texture("backgrounds/title.png");
        Image background = new Image(backgroundTexture);
        background.setPosition(0, 0);
        background.setPosition(0, 0);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setFillParent(true);
        stage.addActor(background);

        //create group for explosions and add now so they're under the UI
        final Group explosionsGroup = new Group();
        explosionsGroup.getColor().a = 0f; //invisible, will tween in
        stage.addActor(explosionsGroup);

        //setup skin and table
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/simpleatlas.atlas"));

        table = new Table(skin);
        table.setBounds(0, 100, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.setFillParent(true);

        //creating fonts
        buttonFont = Config.generateFont("fonts/square.ttf", 42, Color.WHITE);
        titleFont = Config.generateFont("fonts/square.ttf", 90, Color.BLACK);
        textFont = Config.generateFont("fonts/minecraftia.ttf", 16, Color.BLUE);

        //programmatically add font to style
        TextButtonStyle buttonStyle = skin.get("default", TextButtonStyle.class);
        buttonStyle.font = buttonFont;

        //play button
        final TextButton buttonPlay = new TextButton("Play", buttonStyle);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (buttonPlay.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu());
                    }
                })));
            }
        });
        buttonPlay.pad(3f, 10f, 3f, 10f);

        //exit button
        final TextButton buttonExit = new TextButton("Exit", buttonStyle);
        buttonExit.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (buttonExit.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        buttonExit.pad(3f, 10f, 3f, 10f);

        //programmatically add font to labelstyle
        LabelStyle headingStyle = skin.get("default", LabelStyle.class);
        headingStyle.font = titleFont;

        //create heading
        Label heading = new Label(Config.NAME, skin);

        //create bonus content
        WindowStyle windowStyle = skin.get("default", WindowStyle.class);
        windowStyle.titleFont = buttonFont;
        final Window bonus = new Window("Bonus Game!", windowStyle);
        Label infoText = new Label("\n\n--Controls--\n\nWASD - Movement\n\nClick - Boost\n\nScroll - Spin",
                new LabelStyle(textFont, Color.BLACK));
        buttonStyle.font = textFont;
        final TextButton buttonBonus = new TextButton("Let's go!", buttonStyle);
        buttonBonus.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (buttonBonus.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new BonusGame());
            }
        });
        buttonBonus.pad(3f, 10f, 3f, 10f);
        bonus.pad(windowStyle.titleFont.getBounds(bonus.getTitle()).height, 10f, 10f, 10f);
        bonus.add().padBottom(20f).row();
        bonus.add(infoText).center().padBottom(10f).row();
        bonus.add(buttonBonus).center();
        bonus.setMovable(false);
        bonus.setSize(stage.getWidth() / 1.5f, stage.getHeight() / 1.5f);
        bonus.setPosition(stage.getWidth() / 2 - bonus.getWidth() / 2, stage.getHeight() / 2 - bonus.getHeight() / 2);
        bonus.setVisible(false);

        //handle input processor
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {

            @Override
            public boolean scrolled(int amount) {
                Actor[] actors = explosionsGroup.getChildren().begin();
                for (int i = 0, n = explosionsGroup.getChildren().size; i < n; i++) {
                    Explosion exp = (Explosion) actors[i];
                    //scroll = randomize
                    exp.velocity.setAngle(MathUtils.random(360));
                }
                explosionsGroup.getChildren().end();
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 forcePoint = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
                Actor[] actors = explosionsGroup.getChildren().begin();
                for (int i = 0, n = explosionsGroup.getChildren().size; i < n; i++) {
                    Explosion exp = (Explosion) actors[i];
                    Vector2 expPos = new Vector2(exp.getX(), exp.getY());
                    //left = repel, right/other = attract
                    exp.velocity.set(expPos.sub(forcePoint).nor().scl(button == 0 ? 1f : -1f));
                }
                explosionsGroup.getChildren().end();
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.F9) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new ExperimentScreen());
                }

                if (keycode == Keys.ESCAPE && bonus.isVisible()) {
                    bonus.setVisible(false);
                }
                return false;
            }
        }, new KonamiInputDetector(new Runnable() {
            @Override
            public void run() {
                badingSfx.play();
                bonus.setVisible(true);
            }
        })));

        //putting stuff together
        table.add(heading).spaceBottom(150f);
        table.row(); //add new row
        table.add(buttonPlay).spaceBottom(25f);
        table.row();
        table.add(buttonExit);
        table.debug();
        stage.addActor(table);

        stage.addActor(bonus);

        //creating animations
        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class, new ActorAccessor());

        // @formatter:off

        Timeline.createSequence().beginSequence()
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(0, 0, 1))
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(0, 1, 0))
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(1, 0, 0))
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(1, 1, 0))
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(0, 1, 1))
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(1, 0, 1))
        .push(Tween.to(heading, ActorAccessor.RGB, 0.75f).target(1, 1, 1))
        .end()
        .repeat(Tween.INFINITY, 0)
        .start(tweenManager);

        Timeline.createSequence()
        .beginSequence()
        .push(Tween.set(explosionsGroup, ActorAccessor.ALPHA).target(0f))
        .push(Tween.set(heading, ActorAccessor.ALPHA).target(0f))
        .push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0f))
        .push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0f))
        .push(Tween.to(background, ActorAccessor.ALPHA, 1.0f).target(1f))
        .push(Tween.to(heading, ActorAccessor.ALPHA, 0.2f).target(1f))
        .push(Tween.to(buttonPlay, ActorAccessor.ALPHA, 0.2f).target(1f))
        .push(Tween.to(buttonExit, ActorAccessor.ALPHA, 0.2f).target(1f))
        .push(Tween.to(explosionsGroup, ActorAccessor.ALPHA, 0.5f).target(1f))
        .end()
        .start(tweenManager);
        // @formatter:on

        //table fade in
        Tween.from(table, ActorAccessor.ALPHA, 1.5f).target(0).start(tweenManager);
        Tween.from(table, ActorAccessor.Y, 1.5f).target(Gdx.graphics.getHeight() / 2).start(tweenManager);
        tweenManager.update(Gdx.graphics.getDeltaTime());

        //add explosions flying around right above background layer
        Rectangle bounds = getBounds();
        final Texture explosionTexture = new Texture("images/disc256.png");
        for (int i = 0; i < 20; i++) {
            Explosion exp = new Explosion(getBounds(), explosionTexture);
            float randomX = MathUtils.random(bounds.x + exp.getWidth(), bounds.x + bounds.width - exp.getWidth());
            float randomY = MathUtils.random(bounds.y + exp.getHeight(), bounds.y + bounds.height - exp.getHeight());
            exp.setPosition(randomX, randomY);
            explosionsGroup.addActor(exp);
        }

    }

    private Rectangle getBounds() {
        //magic numbers represent border in background image
        return new Rectangle(16, 8, Gdx.graphics.getWidth() - 32, Gdx.graphics.getHeight() - 16);
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
        skin.dispose();
        buttonFont.dispose();
        titleFont.dispose();
        rolloverSfx.dispose();
        badingSfx.dispose();
    }

}
