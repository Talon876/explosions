package org.nolat.explosions.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.exception.StackMobException;

public class Settings implements Screen {

    private Stage stage;
    private Table table;
    private Skin skin;
    private Sound rolloverSfx;

    private BitmapFont settingsFont;
    private BitmapFont buttonFont;
    private BitmapFont titleFont;

    private Player player;
    private TextField nameField;
    private TextButton save;

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
        //handle input processor
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

        //fonts
        settingsFont = FontUtils.generateFont("fonts/square.ttf", 36, Color.WHITE);
        buttonFont = FontUtils.generateFont("fonts/square.ttf", 42, Color.WHITE);
        titleFont = FontUtils.generateFont("fonts/square.ttf", 90, Color.BLACK);

        //styles
        LabelStyle labelStyle = skin.get("default", LabelStyle.class);
        labelStyle.font = titleFont;
        labelStyle.fontColor = Color.BLACK;
        skin.add("default", labelStyle, LabelStyle.class);

        LabelStyle settingsLabelStyle = skin.get("settings", LabelStyle.class);
        settingsLabelStyle.font = settingsFont;
        settingsLabelStyle.fontColor = Color.BLACK;
        skin.add("settings", settingsLabelStyle, LabelStyle.class);

        TextFieldStyle textFieldStyle = skin.get("default", TextFieldStyle.class);
        textFieldStyle.font = settingsFont;
        skin.add("default", textFieldStyle, TextFieldStyle.class);

        //settings items
        nameField = new TextField("Please Wait...", skin);
        nameField.setMaxLength(25);
        nameField.setDisabled(true);

        //settings tables
        Table settingsTable = new Table(skin);
        settingsTable.add("Highscore Name", "settings").pad(10f).left();
        settingsTable.add(nameField).pad(10f).right().expand().fillX();

        TextButtonStyle buttonStyle = skin.get("default", TextButtonStyle.class);
        buttonStyle.font = buttonFont;
        save = new TextButton("Save", buttonStyle);
        save.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (save.getColor().a >= 1f) {
                    rolloverSfx.play();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (!nameField.isDisabled()) { //prevent saving the placeholder text as a name
                            player.setName(nameField.getText());
                            player.save();
                        }
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                    }
                })));
            }
        });
        save.pad(3f, 10f, 3f, 10f);

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
        table.add("Settings", "default").colspan(3).expandX().spaceBottom(25).row();

        table.add(back).size(210f, 76f).uniformX().bottom().left().padLeft(28).padBottom(20);
        table.add(settingsTable).expand().fillX().top();
        table.add(save).size(210f, 76f).uniformX().bottom().right().padRight(28).padBottom(20);

        stage.addActor(table);

        stage.addAction(Actions.alpha(0)); //set alpha to 0
        stage.addAction(Actions.fadeIn(0.5f));

        player = Player.getExistingPlayer();
        player.fetch(new StackMobCallback() {

            @Override
            public void success(String responseBody) { //wait for success to fade in
                if (Config.debug) {
                    Gdx.app.log("StackMob", "Refreshed player data for " + player.getName());
                }
                nameField.setText(player.getName());
                nameField.setDisabled(false);
            }

            @Override
            public void failure(StackMobException e) { //otherwise go back to main
                if (Config.debug) {
                    Gdx.app.debug("StackMob",
                            "Error retrieving player information for " + player.getID() + ": " + e.getMessage());
                }
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
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
