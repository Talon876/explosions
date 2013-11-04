package org.nolat.explosions.entities;

import org.nolat.explosions.LevelInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HUD extends Actor {

    public static boolean showFps = false;

    private final BitmapFont font;
    private final LevelInfo levelInfo;
    private final Rectangle bounds;
    private final float shadowSize;
    private String string;

    public HUD(BitmapFont font, LevelInfo levelInfo, Rectangle bounds, float shadowSize) {
        this.font = font;
        this.levelInfo = levelInfo;
        this.bounds = bounds;
        this.shadowSize = shadowSize;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        string = String.format("Destroy %d of %d", levelInfo.numNeededToPass, levelInfo.numTotal);
        drawShadowString(batch, string, bounds.x + 4, bounds.y + bounds.height - 6);
        if (showFps) {
            drawShadowString(batch, Gdx.graphics.getFramesPerSecond() + " FPS", bounds.x + 4, bounds.y + 22);
        }
    }

    private void drawShadowString(SpriteBatch batch, String string, float x, float y) {
        font.setColor(Color.BLACK);
        font.draw(batch, string, x + shadowSize, y - shadowSize);
        font.setColor(Color.GREEN);
        font.draw(batch, string, x, y);
    }
}
