package org.nolat.explosions.utils;

import org.nolat.explosions.utils.ParticleEffectPool.PooledEffect;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<PooledEffect> {
    private final ParticleEffect effect;

    public ParticleEffectPool(ParticleEffect effect, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.effect = effect;
    }

    @Override
    protected PooledEffect newObject() {
        return new PooledEffect(effect);
    }

    @Override
    public PooledEffect obtain() {
        PooledEffect effect = super.obtain();

        return effect;
    }

    public class PooledEffect extends ParticleEffect {
        PooledEffect(ParticleEffect effect) {
            super(effect);
        }

        @Override
        public void reset() {
            super.reset();
        }

        public void free() {
            ParticleEffectPool.this.free(this);
        }
    }
}