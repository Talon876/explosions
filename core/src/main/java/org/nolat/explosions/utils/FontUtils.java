package org.nolat.explosions.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontUtils {
	
    public static BitmapFont generateFont(String internalPath, int size, Color color) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(internalPath));
        BitmapFont font = null;

        font = gen.generateFont(size);
        font.setColor(color);
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        gen.dispose();
        return font;
    }

}
