package org.nolat.explosions.entities;

import org.nolat.explosions.LevelInfo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HUD extends Actor {

    private final BitmapFont font;
    private final LevelInfo levelInfo;
    private final float x, y, shadowSize;
    private String string;

    public HUD(BitmapFont font, LevelInfo levelInfo, float x, float y, float shadowSize) {
        this.font = font;
        this.levelInfo = levelInfo;
        this.x = x;
        this.y = y;
        this.shadowSize = shadowSize;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        string = String.format("Destroy %d of %d", levelInfo.numNeededToPass, levelInfo.numTotal);
        font.setColor(Color.BLACK);
        font.draw(batch, string, x + shadowSize, y - shadowSize);
        font.setColor(Color.GREEN);
        font.draw(batch, string, x, y);
    }
}
