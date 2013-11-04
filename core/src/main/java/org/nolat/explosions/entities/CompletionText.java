package org.nolat.explosions.entities;

import org.nolat.explosions.utils.ShaderUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CompletionText extends Actor {

    private final TextureRegion winTexture, failTexture;
    private boolean winning = true; //tiger's blood
    private float time = MathUtils.random(10f);

    public CompletionText(Texture winTexture, Texture failTexture) {
        winTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        failTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        this.winTexture = new TextureRegion(winTexture);
        this.failTexture = new TextureRegion(failTexture);
        setPosition(Gdx.graphics.getWidth() / 2 - winTexture.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - winTexture.getHeight() / 2);
        setOrigin(winTexture.getWidth() / 2, winTexture.getHeight() / 2);
        setSize(winTexture.getWidth(), winTexture.getHeight());
        setScale(0f);
        getColor().a = 1f;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        ShaderUtils.startWobbly(batch, time += Gdx.graphics.getDeltaTime(), 1 / 2f);
        batch.setColor(getColor());
        batch.draw(isWinning() ? winTexture : failTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
        ShaderUtils.end(batch);
    }

    public boolean isWinning() {
        return winning;
    }

    public void setWinning(boolean winning) {
        this.winning = winning;
    }
}
