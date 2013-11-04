package org.nolat.explosions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Config {
    public static final String NAME = "Explosions";
    public static final String VERSION = "0.1";
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static boolean debug = false;

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

    /**
     * Taken from java.awt.Color<br>
     * 
     * Converts the components of a color, as specified by the HSB model, to an equivalent set of values for the default RGB model.
     * <p>
     * The <code>saturation</code> and <code>brightness</code> components should be floating-point values between zero and one (numbers in
     * the range 0.0-1.0). The <code>hue</code> component can be any floating-point number. The floor of this number is subtracted from it
     * to create a fraction between 0 and 1. This fractional number is then multiplied by 360 to produce the hue angle in the HSB color
     * model.
     * <p>
     * The integer that is returned by <code>HSBtoRGB</code> encodes the value of a color in bits 0-23 of an integer value that is the same
     * format used by the method {@link #getRGB() <code>getRGB</code>}. This integer can be supplied as an argument to the
     * <code>Color</code> constructor that takes a single integer argument.
     * 
     * @param hue
     *            the hue component of the color
     * @param saturation
     *            the saturation of the color
     * @param brightness
     *            the brightness of the color
     * @return the RGB value of the color with the indicated hue, saturation, and brightness.
     * @see java.awt.Color#getRGB()
     * @see java.awt.Color#Color(int)
     * @see java.awt.image.ColorModel#getRGBdefault()
     * @since JDK1.0
     */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0x00000000 | (r << 24) | (g << 16) | (b << 8);
    }
}
