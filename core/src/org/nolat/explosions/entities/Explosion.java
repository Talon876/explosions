package org.nolat.explosions.entities;

import org.nolat.explosions.utils.ColorUtils;
import org.nolat.explosions.utils.ParticleEffectPool;
import org.nolat.explosions.utils.ParticleEffectPool.PooledEffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Explosion extends Actor {
    private static final float MIN_SPEED = 1.65f, MAX_SPEED = 2.25f, // random speeds
            GROW_SIZE = 128f, // max size to explode to
            GROW_TIME = 1.15f, // time spent growing to max size
            SHRINK_TIME = 0.85f, // time spent shrinking
            WAIT_TIME = 1.2f; // time in seconds to wait at max size before  dying
    private static final int ANGLE_FLUCTUATION = 15; // degrees

    private static final ParticleEffectPool confettiPool;
    private static final ParticleEffectPool explodePool;
    static {
        ParticleEffect confettiTrailPool = new ParticleEffect();
        confettiTrailPool.load(Gdx.files.internal("particles/confetti.p"), Gdx.files.internal("images/"));
        confettiPool = new ParticleEffectPool(confettiTrailPool, 125, 200);

        ParticleEffect explodeEffectPool = new ParticleEffect();
        explodeEffectPool.load(Gdx.files.internal("particles/explode.p"), Gdx.files.internal("images/"));
        explodePool = new ParticleEffectPool(explodeEffectPool, 75, 100);
    }

    private final Rectangle bounds;
    private final TextureRegion texture;
    private final Sound growFx;
    private final Sound dieFx;

    private final PooledEffect confettiTrail;
    private final PooledEffect explodeEffect;
    private float[] particleColor;

    public final Vector2 velocity;
    private final float speed;
    private float speedModifier;

    private ExplosionState state;

    private Runnable deathAction;

    public Explosion(Rectangle bounds, TextureRegion texture, Sound growFx, Sound dieFx) {
        this.bounds = bounds;
        this.texture = texture;
        this.growFx = growFx;
        this.dieFx = dieFx;
        deathAction = new DefaultDeathAction();

        confettiTrail = confettiPool.obtain();
        confettiTrail.setPosition(getX(), getY());
        confettiTrail.start();

        explodeEffect = explodePool.obtain();

        velocity = new Vector2(1, 1).setAngle(getRandomAngle(ANGLE_FLUCTUATION));
        speed = MathUtils.random(MIN_SPEED, MAX_SPEED);
        speedModifier = 1f;

        setSize(32, 32);

        setColor(new Color(ColorUtils.HSBtoRGB(MathUtils.random(), 1f, 1f)));
        state = ExplosionState.MOVING;
    }

    /**
     * Calculates a random angle that isn't too close to 0, 90, 270, or 360
     * 
     * @param fluctuation
     *            amount of degrees away from vertical/horizontal. Must be 0 < fluctuation < 45
     * @return random degrees
     */
    private int getRandomAngle(int fluctuation) {
        if (fluctuation >= 45 || fluctuation < 0) {
            throw new IllegalArgumentException("Fluctuation value must satisfy: 0 < fluctuation < 45");
        }
        int quadrant = MathUtils.random(0, 3);
        return MathUtils.random((quadrant * 90) + fluctuation, ((quadrant + 1) * 90) - fluctuation);
    }

    public Explosion(Rectangle bounds, TextureRegion texture) {
        this(bounds, texture, null, null);
    }

    public void explode() {
        state = ExplosionState.EXPLODING;
        if (growFx != null) {
            growFx.play();
        }
        if (explodeEffect != null) {
            explodeEffect.reset();
        }
        addAction(Actions.sequence(
                Actions.parallel(Actions.sizeTo(1.5f / speedModifier * GROW_SIZE, 
                								1.5f / speedModifier * GROW_SIZE,
                								1f / speedModifier * GROW_TIME, Interpolation.exp5In),
                        Actions.alpha(0.75f, 1f / speedModifier * GROW_TIME, Interpolation.exp5In)),
                        Actions.delay(1f / speedModifier * WAIT_TIME),
                        Actions.sizeTo(0, 0, 1f / speedModifier * SHRINK_TIME, Interpolation.exp5Out), Actions.run(deathAction),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                if (dieFx != null) {
                                    dieFx.play(0.55f);
                                }
                                if (explodeEffect != null) {
                                    explodeEffect.free();
                                }
                                if (confettiTrail != null) {
                                    confettiTrail.free();
                                }
                                Explosion.this.remove();
                            }
                        })));
    }

    private void handleWallCollisions() {
        // failsafe, if just reversing the velocity failed, just teleport out
        // doing this first gives it a frame to set the position again
        // this can only occur on the title screen
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

        // check for bouncing
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

    private void handleExplosionCollisions() {
        Actor[] actors = getParent().getChildren().begin();
        for (int i = 0, n = getParent().getChildren().size; i < n; i++) {
            if (actors[i] instanceof Explosion) {
                Explosion exp = (Explosion) actors[i];
                if (exp.getState() == ExplosionState.EXPLODING) {
                    if (isColliding(exp)) {
                        explode();
                    }
                }
            }
        }
        getParent().getChildren().end();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        switch (state) {
        case MOVING:
            setPosition(getX() + velocity.x * speed * speedModifier, getY() + velocity.y * speed * speedModifier);
            handleWallCollisions();
            handleExplosionCollisions();
            break;
        case EXPLODING:
            break;
        }
        updateParticles();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (parentAlpha >= .9f) {
            if (confettiTrail != null) {
                confettiTrail.draw(batch, Gdx.graphics.getDeltaTime());
            }
        }
        if (explodeEffect != null) {
            explodeEffect.draw(batch, Gdx.graphics.getDeltaTime());
        }
        batch.setColor(new Color(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha));
        batch.draw(texture, getX() - getWidth() / 2, getY() - getHeight() / 2, getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(Color.WHITE);
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
    
    public void setSpeedModifier(float speedModifier) {
    	this.speedModifier = speedModifier;
    }

    private void updateParticles() {
        if (confettiTrail != null) {
            confettiTrail.setPosition(getX(), getY());
            confettiTrail.findEmitter("confetti").getTint().setColors(particleColor);
        }
        if (explodeEffect != null) {
            explodeEffect.setPosition(getX(), getY());
            explodeEffect.findEmitter("explode").getTint().setColors(particleColor);
        }
    }

    public enum ExplosionState {
        MOVING, EXPLODING
    }

    public float getRadius() {
        return getWidth() / 2; // width and height should always be the same so either could work here
    }

    public ExplosionState getState() {
        return state;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
    
    public float getSpeedModifier() {
    	return speedModifier;
    }

    public void setDeathAction(Runnable deathAction) {
        this.deathAction = deathAction;
    }

    /**
     * Checks if this is colliding with another explosion
     * 
     * @param other
     *            the other explosion
     * @return true if colliding and one of the explosions is exploding
     */
    public boolean isColliding(Explosion other) {
        boolean colliding = false;
        if (equals(other)) { // if self, not colliding
            return false;
        }
        if (state == ExplosionState.EXPLODING && other.getState() == ExplosionState.EXPLODING) { // both exploding = not  colliding
            return false;
        }

        Vector2 posA = new Vector2(getX(), getY());
        Vector2 posB = new Vector2(other.getX(), other.getY());
        float distance = posA.dst(posB);
        if (distance < (getRadius() + other.getRadius())) {
            colliding = true;
        }

        // actually only collide if ONE is exploding
        colliding = colliding && (state == ExplosionState.EXPLODING || other.getState() == ExplosionState.EXPLODING);

        return colliding;
    }

    private class DefaultDeathAction implements Runnable {
        @Override
        public void run() {
            // default action is to do nothing
        }
    }
}
