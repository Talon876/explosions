package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.tween.SpriteAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Splash implements Screen {

	private SpriteBatch batch;
	private Sprite splash;
	private TweenManager tweenManager;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		splash.draw(batch);
		batch.end();

		tweenManager.update(delta);

		if (Gdx.input.justTouched()) {
			((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		tweenManager = new TweenManager();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		float widthScale = Gdx.graphics.getWidth() / Config.WIDTH;
		float heightScale = Gdx.graphics.getHeight() / Config.HEIGHT;

		Texture splashTexture = new Texture("icons/nolatorg.png");
		splashTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		splash = new Sprite(splashTexture);
		splash.setSize(widthScale * splash.getWidth(), heightScale * splash.getHeight());
		splash.setOrigin(splash.getWidth() / 2, splash.getWidth() / 2);
		splash.setPosition(Gdx.graphics.getWidth() / 2 - splash.getWidth() / 2,
				Gdx.graphics.getHeight() / 2 - splash.getHeight() / 2);

		Tween.set(splash, SpriteAccessor.ALPHA).target(0f).start(tweenManager);
		Tween.to(splash, SpriteAccessor.ALPHA, 1f).target(1f).repeatYoyo(1, 1f).setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
			}
		}).start(tweenManager);
		tweenManager.update(Float.MIN_VALUE);
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
		splash.getTexture().dispose();
	}
}
