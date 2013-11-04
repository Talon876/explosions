package org.nolat.explosions.entities;

import org.nolat.explosions.LevelInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HUD extends Actor {

    public static boolean showFps = false;

    private final BitmapFont font;
    private final BitmapFont barFont;
    private final LevelInfo levelInfo;
    private final Rectangle bounds;
    private final float shadowSize;
    private String string;
    private int numDestroyed;

    public HUD(BitmapFont font, BitmapFont barFont, LevelInfo levelInfo, Rectangle bounds, float shadowSize) {
        this.font = font;
        this.barFont = barFont;
        this.levelInfo = levelInfo;
        this.bounds = bounds;
        this.shadowSize = shadowSize;

    }

    public void update(int numDestroyed) {
        this.numDestroyed = numDestroyed;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        string = String.format("L%d - Destroy %d of %d", levelInfo.level + 1, levelInfo.numNeededToPass,
                levelInfo.numTotal);
        drawShadowString(batch, font, string, bounds.x + 4, bounds.y + bounds.height - 6, Color.GREEN);
        string = getDestroyedBar();
        TextBounds txtSize = barFont.getBounds(string);
        drawShadowString(batch, barFont, string, bounds.x + bounds.width - 4 - txtSize.width, bounds.y + bounds.height
                - 6, Color.GREEN);
        if (showFps) {
            drawShadowString(batch, font, Gdx.graphics.getFramesPerSecond() + " FPS", bounds.x + 4, bounds.y + 22,
                    Color.YELLOW);
        }
    }

    private String getDestroyedBar() {
        String bar = "";
        for (int i = 0; i < levelInfo.numNeededToPass - numDestroyed; i++) {
            bar += "|";
        }
        return bar;
    }

    private void drawShadowString(SpriteBatch batch, BitmapFont font, String string, float x, float y, Color color) {
        font.setColor(Color.BLACK);
        font.draw(batch, string, x + shadowSize, y - shadowSize);
        font.setColor(color);
        font.draw(batch, string, x, y);
    }
}
