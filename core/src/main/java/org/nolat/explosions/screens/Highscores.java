package org.nolat.explosions.screens;

import java.util.List;

import org.nolat.explosions.Config;
import org.nolat.explosions.stackmob.Player;
import org.nolat.explosions.utils.FontUtils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQuery.Ordering;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class Highscores implements Screen {
    private final int NUMBER = 10;

    private Stage stage;
    private Table table;
    private Skin skin;
    private Sound rolloverSfx;

    private BitmapFont settingsFont;
    private BitmapFont buttonFont;
    private BitmapFont titleFont;

    private Table highscoreTable;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(Config.WIDTH, Config.HEIGHT, false);
        table.invalidateHierarchy();
    }

    @Override
    public void show() {
        stage = new Stage(Config.WIDTH, Config.HEIGHT, false);
        // handle input processor
        Gdx.input.setInputProcessor(stage);
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

        settingsFont = FontUtils.generateFont("fonts/square.ttf", 36, Color.WHITE);
        buttonFont = FontUtils.generateFont("fonts/square.ttf", 42, Color.WHITE);
        titleFont = FontUtils.generateFont("fonts/square.ttf", 90, Color.BLACK);

        LabelStyle labelStyle = skin.get("default", LabelStyle.class);
        labelStyle.font = titleFont;
        labelStyle.fontColor = Color.BLACK;
        skin.add("default", labelStyle, LabelStyle.class);

        LabelStyle settingsLabelStyle = skin.get("settings", LabelStyle.class);
        settingsLabelStyle.font = settingsFont;
        settingsLabelStyle.fontColor = Color.BLACK;
        skin.add("settings", settingsLabelStyle, LabelStyle.class);

        TextButtonStyle buttonStyle = skin.get("default", TextButtonStyle.class);
        buttonStyle.font = buttonFont;
        final TextButton refresh = new TextButton("Refresh", buttonStyle);
        refresh.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (refresh.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (stage.getRoot().getActions().size == 0) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            refreshScores();
                        }
                    }), Actions.fadeIn(0.5f)));
                }
            }
        });
        refresh.pad(3f, 10f, 3f, 10f);

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

        highscoreTable = new Table(skin);
        refreshScores();

        // putting stuff together
        table.add("Highscores").colspan(3).expandX().spaceBottom(25).row();
        table.add(back).size(210f, 76f).uniformX().bottom().left().padLeft(28).padBottom(20);
        table.add(highscoreTable).expand().fillX().top();
        table.add(refresh).size(210f, 76f).uniformX().bottom().right().padRight(28).padBottom(20);
        stage.addActor(table);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));
    }

    private void refreshTable(List<Player> players) {
        highscoreTable.clear();
        highscoreTable.add("#", "settings").center().expandX();
        highscoreTable.add("Name", "settings").center().expandX();
        highscoreTable.add("Levels Complete", "settings").center().expandX();
        highscoreTable.add("Score", "settings").center().expandX();
        highscoreTable.row();
        if (players != null) {
            int rank = 1;
            for (Player p : players) {
                highscoreTable.add(Integer.toString(rank++), "settings").center();
                highscoreTable.add(p.getName(), "settings").center();
                highscoreTable.add(Integer.toString(p.getLevelsComplete()), "settings").center();
                highscoreTable.add(Integer.toString(p.getScore()), "settings").center();
                highscoreTable.row();
            }
        }
        highscoreTable.invalidateHierarchy();
    }

    private void refreshScores() {
        Player.query(
                Player.class,
                new StackMobQuery().fieldIsGreaterThan("levelsComplete", 5)
                .fieldIsOrderedBy("levelsComplete", Ordering.DESCENDING)
                .fieldIsOrderedBy("score", Ordering.DESCENDING).isInRange(0, NUMBER - 1),
                new StackMobQueryCallback<Player>() {
                    @Override
                    public void success(List<Player> result) {
                        refreshTable(result);
                    }

                    @Override
                    public void failure(StackMobException e) {
                        e.printStackTrace();
                        refreshTable(null);
                    }
                });
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
        settingsFont.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
