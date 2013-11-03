package org.nolat.explosions.screens;

import org.nolat.explosions.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PoolExperiment implements Screen {

    private SpriteBatch batch;
    private Array<Water> drops;
    private SwimmingPool pool;
    private Texture dropTexture;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (Water water : drops) {
            water.draw(batch);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        drops = new Array<>();
        pool = new SwimmingPool();
        dropTexture = new Texture("images/drop.png");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Water drop = pool.obtain();
                drop.x = screenX - dropTexture.getWidth() / 2;
                drop.y = Gdx.graphics.getHeight() - screenY - dropTexture.getHeight() / 2;
                drops.add(drop);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                case Keys.ESCAPE:
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu());
                    break;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }
        });
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        dropTexture.dispose();
    }

    private class SwimmingPool extends Pool<Water> {
        @Override
        protected Water newObject() {
            return new Water(dropTexture, 0, 0, 64 * 2, 101 * 2);
        }
    }

    private class Water implements Poolable {

        private final Texture texture;
        public float width, height;
        public float x, y, life = 2f;

        public Water(Texture texture, float x, float y, float width, float height) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void draw(SpriteBatch batch) {
            batch.draw(texture, x, y, width, height);
            life -= Gdx.graphics.getDeltaTime();
            if (life <= 0) {
                drops.removeValue(this, true);
                pool.free(this);
            }
        }

        @Override
        public void reset() {
            x = 0;
            y = 0;
            width = 64;
            height = 101;
            life = 2;
        }
    }
}
