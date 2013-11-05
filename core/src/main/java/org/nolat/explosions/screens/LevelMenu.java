package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.entities.LevelSelectron;
import org.nolat.explosions.utils.InputAdapter;
import org.nolat.explosions.utils.ShaderUtils;

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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LevelMenu implements Screen {

    private Stage stage;
    private Table table;
    private Skin skin;
    private Sound rolloverSfx;

    private BitmapFont levelSelectFont;
    private BitmapFont buttonFont;
    private BitmapFont titleFont;

    private int selected = 0;

    /**
     * 
     * @param selected
     *            index selected by default
     */
    public LevelMenu(int selected) {
        this.selected = selected;
    }

    public LevelMenu() {
        this(0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        table.invalidateHierarchy();
    }

    @Override
    public void show() {
        ShaderUtils.init();
        stage = new Stage();
        //handle input processor
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.F9 && Config.debug) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new ExperimentScreen());
                }
                return false;
            }
        }));
        rolloverSfx = Gdx.audio.newSound(Gdx.files.internal("sfx/rollover.ogg"));

        Texture backgroundTexture = new Texture("backgrounds/title.png");
        Image background = new Image(backgroundTexture);
        background.setPosition(0, 0);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setFillParent(true);
        stage.addActor(background);

        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/simpleatlas.atlas"));

        table = new Table(skin);
        table.setFillParent(true);

        levelSelectFont = Config.generateFont("fonts/square.ttf", 36, Color.WHITE);
        buttonFont = Config.generateFont("fonts/square.ttf", 42, Color.WHITE);
        titleFont = Config.generateFont("fonts/square.ttf", 90, Color.BLACK);

        LabelStyle labelStyle = skin.get("default", LabelStyle.class);
        labelStyle.font = titleFont;
        labelStyle.fontColor = Color.BLACK;
        skin.add("default", labelStyle, LabelStyle.class);

        Texture levelButtonTexture = new Texture("images/disc256.png");
        LevelSelectron levelSelectron = new LevelSelectron(levelSelectFont, levelButtonTexture, skin);
        levelSelectron.setPosition(100, 600);

        TextButtonStyle buttonStyle = skin.get("default", TextButtonStyle.class);
        buttonStyle.font = buttonFont;
        final TextButton play = new TextButton("Play", buttonStyle);
        play.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (play.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(LevelInfo.getLevelInfo(0))); //TODO fix
                    }
                })));
            }
        });
        play.pad(3f, 10f, 3f, 10f);

        final TextButton back = new TextButton("Back", buttonStyle);
        back.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (back.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                    }
                })));
            }
        });
        back.pad(3f, 10f, 3f, 10f);

        //putting stuff together
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.add("Select Level").colspan(3).expandX().spaceBottom(25).row();

        table.add(back).size(210f, 76f).uniformX().bottom().left().padLeft(28).padBottom(20);
        table.add(levelSelectron.getSelectron()).expand().top();
        table.add(play).size(210f, 76f).uniformX().bottom().right().padRight(28).padBottom(20);

        stage.addActor(table);

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
        skin.dispose();
        rolloverSfx.dispose();
        levelSelectFont.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
