package org.nolat.explosions.entities;

import java.util.HashMap;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.utils.PagedScrollPane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LevelSelectron extends Group {

    private final BitmapFont font;
    private final Texture buttonTexture;
    private final Skin skin;
    private final ClickListener buttonListener;

    private final Table container;

    private int levelsUnlocked = 0;

    private int selectedLevel = 0;

    private Button selectedButton = null;
    private HashMap<Integer, Button> levelButtonMap = null;

    public LevelSelectron(BitmapFont font, Texture buttonTexture, Skin skin, ClickListener buttonListener) {
        this.font = font;
        this.buttonTexture = buttonTexture;
        this.skin = skin;
        this.buttonListener = buttonListener;

        //used for mapping ints to Button actors
        levelButtonMap = new HashMap<>();

        //setup main table container
        container = new Table();
        levelsUnlocked = LevelInfo.getNumberOfLevels(); //TODO correctly track/set this

        //create paged scroll pane
        PagedScrollPane pagedScrollArea = new PagedScrollPane(skin);
        pagedScrollArea.getStyle().hScrollKnob.setMinHeight(3f);
        pagedScrollArea.setScrollBarPositions(false, true);
        pagedScrollArea.setFlingTime(0.25f);

        //create level tables with 35 buttons per page arranged in 5 rows and 7 cols
        Table levelPage = null;
        for (int i = 0; i < LevelInfo.getNumberOfLevels(); i++) {
            if (i % 35 == 0) {
                levelPage = new Table().pad(0f);
                levelPage.defaults().pad(15f, 25f, 15f, 25f);
                pagedScrollArea.addPage(levelPage);
            }
            if (i % (LevelInfo.getNumberOfLevels() / 5) == 0) {
                levelPage.row();
            }
            levelPage.add(getLevelButton((i)));
        }

        //add page scroll pane to main table and take up all space
        container.add(pagedScrollArea).expand().fill();
    }

    private Button getLevelButton(int level) {
        //Create a basic button style that doesn't have any images
        ButtonStyle buttonStyle = new ButtonStyle();
        Button button = new Button(buttonStyle);
        levelButtonMap.put(level, button);

        // Create the label to show the level number
        LabelStyle labelStyleActive = skin.get("levelButtons", LabelStyle.class);
        labelStyleActive.font = font;

        Label label = new Label(Integer.toString(level + 1), labelStyleActive);
        label.setAlignment(Align.center);

        // Stack the image label on top of eachother
        Image buttonImage = new Image(buttonTexture);
        button.stack(buttonImage, label).size(64f, 64f);

        //set button properties for an active button
        if (level <= levelsUnlocked) {
            buttonStyle.pressedOffsetX = 1;
            buttonStyle.pressedOffsetY = -1;

            buttonImage.setColor(Config.getRandomHSBColor());
            //assign a name to the actor based on the level it represents
            button.setName(Integer.toString(level));
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedLevel(Integer.parseInt(event.getListenerActor().getName()));
                }
            });
            button.addListener(buttonListener);
        } else {
            buttonImage.setColor(new Color(0.75f, 0.75f, 0.75f, 1));
        }
        return button;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public Table getSelectron() {
        return container;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
        //reset last button
        if (selectedButton != null) {
            selectedButton.getColor().a = 1f;
            selectedButton.clearActions();
        }
        //set pulsing effect to represent current selection
        selectedButton = levelButtonMap.get(selectedLevel);
        selectedButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(0.2f, 1f), Actions.alpha(1f, 1f))));
        //TODO scroll to correct page
    }

}
