package org.nolat.explosions.entities;

import org.nolat.explosions.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Explosion extends Actor {

    private final TextureRegion texture;
    private final Rectangle bounds;

    public final Vector2 velocity;
    private final float speed;
    private final int spinSpeedDegrees;

    private final ParticleEffect confettiTrail;
    private final float[] particleColor;

    public Explosion(Rectangle bounds, Texture texture) {
        this.bounds = bounds;
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        this.texture = new TextureRegion(texture);

        confettiTrail = new ParticleEffect();
        confettiTrail.load(Gdx.files.internal("particles/confetti.p"), Gdx.files.internal("images/"));
        confettiTrail.setPosition(getX(), getY());
        confettiTrail.start();

        setSize(32, 32);
        setOrigin(getWidth() / 2, getHeight() / 2);
        spinSpeedDegrees = 0; // 2 * (MathUtils.randomBoolean() ? 1 : -1);
        speed = 1.8f;
        velocity = new Vector2(1, 1).setAngle(MathUtils.random(360));
        setColor(new Color(Config.HSBtoRGB(MathUtils.random(), 1f, 1f)));
        particleColor = new float[] { getColor().r, getColor().g, getColor().b, getColor().a };
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        rotate(spinSpeedDegrees);
        setPosition(getX() + velocity.x * speed, getY() + velocity.y * speed);
        wallCollision();
        particles();
    }

    private void wallCollision() {
        //failsafe, if just reversing the velocity failed, just teleport out
        //doing this first gives it a frame to set the position again
        //this can only occur on the title screen
        if (getX() - getWidth() / 2 < bounds.x - 4) {
            setPosition(getX() + 32, getY());
        } else if (getX() + getWidth() / 2 > bounds.x + bounds.width + 4) {
            setPosition(getX() - 32, getY());
        }
        if (getY() - getHeight() / 2 < bounds.y - 4) {
            setPosition(getX(), getY() + 32);
        } else if (getY() + getHeight() / 2 > bounds.y + bounds.height + 4) {
            setPosition(getX(), getY() - 32);
        }

        //check for bouncing
        if (getX() - getWidth() / 2 < bounds.x) {
            velocity.x *= -1;
        } else if (getX() + getWidth() / 2 > bounds.x + bounds.width) {
            velocity.x *= -1;
        }
        if (getY() - getHeight() / 2 < bounds.y) {
            velocity.y *= -1;
        } else if (getY() + getWidth() / 2 > bounds.y + bounds.height) {
            velocity.y *= -1;
        }

    }

    private void particles() {
        confettiTrail.setPosition(getX(), getY());
        confettiTrail.findEmitter("confetti").getTint().setColors(particleColor);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (parentAlpha >= .9f) {
            confettiTrail.draw(batch, Gdx.graphics.getDeltaTime());
        }
        getColor().a = parentAlpha;
        batch.setColor(getColor());
        batch.draw(texture, getX() - getWidth() / 2, getY() - getHeight() / 2, getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(Color.WHITE);
    }
}
