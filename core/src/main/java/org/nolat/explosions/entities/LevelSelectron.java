package org.nolat.explosions.entities;

import org.nolat.explosions.Config;
import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.utils.PagedScrollPane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

    private final Table container;

    private int levelsUnlocked = 1;

    public LevelSelectron(BitmapFont font, Texture buttonTexture, Skin skin) {
        this.font = font;
        this.buttonTexture = buttonTexture;
        this.skin = skin;

        //setup main table container
        container = new Table();
        levelsUnlocked = MathUtils.random(1, LevelInfo.getNumberOfLevels()); //TODO correctly track/set this

        //create paged scroll pane
        PagedScrollPane pagedScrollArea = new PagedScrollPane(skin);
        pagedScrollArea.getStyle().hScrollKnob.setMinHeight(3f);
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
            levelPage.add(getLevelButton((i + 1)));
        }

        //add page scroll pane to main table and take up all space
        container.add(pagedScrollArea).expand().fill();
    }

    private Button getLevelButton(int level) {
        //Create a basic button style that doesn't have any images
        ButtonStyle buttonStyle = new ButtonStyle();
        Button button = new Button(buttonStyle);

        // Create the label to show the level number
        LabelStyle labelStyleActive = skin.get("levelButtons", LabelStyle.class);
        labelStyleActive.font = font;

        Label label = new Label(Integer.toString(level), labelStyleActive);
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
            button.setName("Level " + Integer.toString(level));
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //TODO actually go to the level
                    System.out.println("Click: " + event.getListenerActor().getName());
                }
            });
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
}
