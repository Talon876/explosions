package org.nolat.explosions.entities;

import org.nolat.explosions.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Explosion extends Actor {
    private static final float MIN_SPEED = 1.7f, MAX_SPEED = 2.0f;

    private final Rectangle bounds;
    private final TextureRegion texture;
    private final Sound removeFx;

    private final ParticleEffect confettiTrail;
    private float[] particleColor;

    public final Vector2 velocity;
    private final float speed;

    public Explosion(Rectangle bounds, Texture texture, Sound removeFx) {
        this.bounds = bounds;
        this.texture = new TextureRegion(texture);
        this.removeFx = removeFx;

        confettiTrail = new ParticleEffect();
        confettiTrail.load(Gdx.files.internal("particles/confetti.p"), Gdx.files.internal("images/"));
        confettiTrail.setPosition(getX(), getY());
        confettiTrail.start();

        velocity = new Vector2(1, 1).setAngle(MathUtils.random(360));
        speed = MathUtils.random(MIN_SPEED, MAX_SPEED);

        setSize(32, 32);

        setColor(new Color(Config.HSBtoRGB(MathUtils.random(), 1f, 1f)));
    }

    public Explosion(Rectangle bounds, Texture texture) {
        this(bounds, texture, null);
    }

    private void handleCollisions() {
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

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        particleColor = new float[] { getColor().r, getColor().g, getColor().b, getColor().a };
    }

    private void updateParticles() {
        confettiTrail.setPosition(getX(), getY());
        confettiTrail.findEmitter("confetti").getTint().setColors(particleColor);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(getX() + velocity.x * speed, getY() + velocity.y * speed);
        handleCollisions();
        updateParticles();
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
