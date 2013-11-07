package org.nolat.explosions.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontUtils {

    /**
     * Generates a {@link BitmapFont} from a TTF font file.
     * 
     * @param internalPath
     *            the internal path to the font location
     * @param size
     *            the size of the font
     * @param color
     *            the color of the font
     * @return the generated font
     */
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
