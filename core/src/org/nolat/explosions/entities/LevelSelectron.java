package org.nolat.explosions.entities;

import java.util.HashMap;

import org.nolat.explosions.LevelInfo;
import org.nolat.explosions.utils.ColorUtils;
import org.nolat.explosions.utils.PagedScrollPane;
import org.nolat.explosions.utils.SaveData;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private static final int BUTTONS_PER_ROW = 7, BUTTONS_PER_PAGE = 35, MAX_STARS = 3;

    private final BitmapFont font;
    private final Texture buttonTexture;
    private final Texture hollowTexture;
    private final Skin skin;
    private final ClickListener buttonListener;
    private final int levelsUnlocked;

    private final Table container;

    private int selectedLevel = 0;

    private Button selectedButton = null;
    private HashMap<Integer, Button> levelButtonMap = null;

    private final PagedScrollPane pagedScrollArea;

    public LevelSelectron(BitmapFont font, Texture buttonTexture, Texture hollowTexture, Skin skin, int levelsUnlocked,
            ClickListener buttonListener) {
        this.font = font;
        this.buttonTexture = buttonTexture;
        this.hollowTexture = hollowTexture;
        this.skin = skin;
        this.levelsUnlocked = levelsUnlocked;
        this.buttonListener = buttonListener;

        //used for mapping ints to Button actors
        levelButtonMap = new HashMap<>();

        //setup main table container
        container = new Table();

        //create paged scroll pane
        pagedScrollArea = new PagedScrollPane(skin);
        pagedScrollArea.getStyle().hScrollKnob.setMinHeight(3f);
        pagedScrollArea.setColor(Color.CYAN);
        pagedScrollArea.setScrollBarPositions(false, true);
        pagedScrollArea.setFlingTime(0.25f);

        //create level tables with 35 buttons per page arranged in 5 rows and 7 cols
        // there must be at least 7 buttons on a page for page scrolling to work correctly
        Table levelPage = null;
        for (int i = 0; i < getButtonAmount(); i++) {
            if (i % BUTTONS_PER_PAGE == 0) { //pages should have 35 buttons (5 rows)
                levelPage = new Table().pad(0f);
                levelPage.defaults().pad(15f, 25f, 0f, 25f);
                pagedScrollArea.addPage(levelPage);
            }
            if (i % BUTTONS_PER_ROW == 0) {
                levelPage.row();
            }
            levelPage.add(getLevelButton((i)));
        }
        //add page scroll pane to main table and take up all space
        container.add(pagedScrollArea).expand().fill();
    }

    /**
     * Increases amount of buttons until there is mod 7 of them so page scrolling works correctly
     * 
     * @return
     */
    private int getButtonAmount() {
        int numLevels = LevelInfo.getNumberOfLevels();
        while (numLevels % BUTTONS_PER_ROW != 0) {
            numLevels++;
        }
        return numLevels;
    }

    private int getPageFromLevel(int level) {
        return level / BUTTONS_PER_PAGE; //int division to find which page the level is on
    }

    public void setPage(int page) {
        pagedScrollArea.setPage(page);
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

            buttonImage.setColor(ColorUtils.getRandomHSBColor());
            //assign a name to the actor based on the level it represents
            button.setName(Integer.toString(level));
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedLevel(Integer.parseInt(event.getListenerActor().getName()));
                }
            });
            button.addListener(buttonListener);

            //calculate "star" information
            final LevelInfo levelData = LevelInfo.getLevelInfo(level);
            float maxDiff = levelData.numTotal - levelData.numNeededToPass;
            float earnedDiff = SaveData.getLevelScore(level) - levelData.numNeededToPass;
            float perCent = earnedDiff / maxDiff;
            int stars = 0;
            if (perCent <= 0.20f) {
                stars = 1;
            } else if (perCent <= 0.60f) {
                stars = 2;
            } else {
                stars = 3;
            }
            Table starTable = new Table();
            starTable.defaults().pad(2f);
            if (stars >= 1) {
                for (int star = 0; star < MAX_STARS; star++) {
                    Image image = new Image((stars > star) ? buttonTexture : hollowTexture);
                    image.setColor(buttonImage.getColor());
                    starTable.add(image).width(16f).height(16f);
                }
            }
            button.row();
            button.add(starTable).height(25f);

        } else if (level <= LevelInfo.getNumberOfLevels() - 1) {
            buttonImage.setColor(new Color(0.75f, 0.75f, 0.75f, 1)); //existing levels, but are locked
        } else { //levels that don't exist that are used for keeping the layout, make them invisible
            button.getColor().a = 0f;
        }
        return button;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public Table getSelectron() {
        return container;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        setPage(getPageFromLevel(selectedLevel));
        this.selectedLevel = selectedLevel;
        //reset last button
        if (selectedButton != null) {
            selectedButton.getColor().a = 1f;
            selectedButton.clearActions();
        }
        //set pulsing effect to represent current selection
        selectedButton = levelButtonMap.get(selectedLevel);
        selectedButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(0.2f, 1f), Actions.alpha(1f, 1f))));
    }

    public int getLevelsUnlocked() {
        return levelsUnlocked;
    }
}
