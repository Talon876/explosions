package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.utils.ColorUtils;
import org.nolat.explosions.utils.InputAdapter;
import org.nolat.explosions.utils.PagedScrollPane;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ExperimentScreen implements Screen {

    private Skin skin;

    private Stage stage;

    private Table container;

    private BitmapFont font;

    private Texture buttonTexture;
    private Texture check;
    private Texture checkHollow;

    private int levelsUnlocked = 1;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
    }

    /**
     * Creates a button to represent the level
     * 
     * @param level
     * @return The button to use for the level
     */
    public Button getLevelButton(int level) {
        ButtonStyle buttonStyle = new ButtonStyle();
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;
        Button button = new Button(buttonStyle);

        // Create the label to show the level number
        LabelStyle labelStyle = skin.get("default", LabelStyle.class);
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLACK;
        Label label = new Label(Integer.toString(level), labelStyle);
        label.setAlignment(Align.center);

        // Stack the image and the label at the top of our button
        Image buttonImage = new Image(check);
        buttonImage.setColor(ColorUtils.getRandomHSBColor());
        //        buttonImage.getColor().a = MathUtils.randomBoolean() ? 1f : 0.25f;
        //        if (buttonImage.getColor().a >= 1f) {
        //            button.stack(buttonImage, label).size(64f, 64f);
        //        } else {
        //            button.stack(buttonImage).size(64f, 64f);
        //        }

        if (level > levelsUnlocked) {
            buttonImage.getColor().a = 0.25f;
            button.stack(buttonImage).size(64f, 64f);
        } else {
            button.stack(buttonImage, label).size(64f, 64f);
        }

        // Randomize the number of stars earned for demonstration purposes
        /*int max = 3;
        int stars = MathUtils.random(-1, max);
        Table starTable = new Table();
        starTable.defaults().pad(3f);
        if (stars >= 1) {
            for (int star = 0; star < max; star++) {
                if (stars > star) {
                    starTable.add(new Image(check)).width(16f).height(16f);
                } else {
                    starTable.add(new Image(checkHollow)).width(16f).height(16f);
                }
            }
        }

        button.row();
        button.add(starTable).height(24f);
         */
        button.setName("Level " + Integer.toString(level));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Click: " + event.getListenerActor().getName());
            }
        });
        return button;
    }

    @Override
    public void show() {
        stage = new Stage();

        buttonTexture = new Texture("images/grass.png");
        check = new Texture("images/fill.png");
        checkHollow = new Texture("images/disc256-hollow.png");
        buttonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        check.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        checkHollow.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/simpleatlas.atlas"));
        container = new Table();
        font = Config.generateFont("fonts/square.ttf", 36, Color.WHITE);
        stage.addActor(container);
        container.setFillParent(true);
        //        container.debug();

        levelsUnlocked = MathUtils.random(1, LevelInfo.getNumberOfLevels());

        PagedScrollPane scroll = new PagedScrollPane();
        scroll.setFlingTime(0.1f);
        scroll.setPageSpacing(0f);
        Table levels = new Table().pad(100f, 50f, 100f, 50f);
        //            levels.debug();
        levels.defaults().pad(15f, 25f, 15f, 25f);
        for (int i = 0; i < LevelInfo.getNumberOfLevels(); i++) {
            if (i % (LevelInfo.getNumberOfLevels() / 5) == 0) {
                levels.row();
            }
            levels.add(getLevelButton((i + 1)));
        }
        scroll.addPage(levels);

        container.add(scroll).expand().fill();

        //handle input processor
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.ESCAPE) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                } else if (keycode == Keys.R) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new ExperimentScreen());
                }
                return false;
            }
        }));
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
    }

}
