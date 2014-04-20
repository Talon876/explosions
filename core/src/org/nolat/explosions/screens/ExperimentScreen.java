package org.nolat.explosions.screens;

import org.nolat.explosions.Config;
import org.nolat.explosions.entities.Explosion;
import org.nolat.explosions.entities.Explosion.ExplosionState;
import org.nolat.explosions.entities.HUD;
import org.nolat.explosions.utils.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class ExperimentScreen implements Screen {

	private static final int INITIAL_AMOUNT = 75;

	private Stage stage;
	private Group explosions;

	private int numDestroyed = 0;
	private int clearedAmount = 0;
	private float elapsedTime;

	private TextureRegion explosionTexture;
	private Sound popFx, puffFx;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		elapsedTime += delta;
		spawnManagement();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	    stage.getViewport().update((int) Config.WIDTH, (int) Config.HEIGHT);
	    stage.getCamera().update();
	}

	@Override
	public void show() {
		stage = new Stage(new StretchViewport(Config.WIDTH, Config.HEIGHT));
		final Texture explosionTex = new Texture("images/disc256.png");
		explosionTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		explosionTexture = new TextureRegion(explosionTex);

		popFx = Gdx.audio.newSound(Gdx.files.internal("sfx/pop.ogg"));
		puffFx = Gdx.audio.newSound(Gdx.files.internal("sfx/puff.ogg"));

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (isExplosionsHappening()) {
					elapsedTime += 2f;
					System.out.println("+2 second time penalty");
				}
				Explosion seed = new Explosion(getBounds(), explosionTexture, puffFx, popFx);
				Vector3 point3 = new Vector3(screenX, screenY, 0);
				stage.getCamera().unproject(point3);
				seed.setPosition(point3.x, point3.y);
				seed.setSpeedModifier(2f);
				seed.explode();
				explosions.addActor(seed);
				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.F10:
					HUD.showFps = !HUD.showFps;
					break;
				case Keys.BACK:
				case Keys.ESCAPE:
					((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
					break;
				}
				return false;
			}
		}, stage));

		setupBackground();

		// set initial explosions
		explosions = new Group();
		spawnExplosions(INITIAL_AMOUNT, explosionTexture, popFx, puffFx);
		stage.addActor(explosions);

		stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));
	}
	
	private void spawnManagement() {
		int newAmt = Math.max(10, INITIAL_AMOUNT - (clearedAmount) * 5);
		int lastAmt = Math.max(10, INITIAL_AMOUNT - (Math.max(clearedAmount - 1, 0)) * 5);
		int triggerAmt = (int) (lastAmt * 0.14f);
		System.out.println("Destroy " + (newAmt-triggerAmt) + " of " + newAmt);
		if (explosions.getChildren().size <= triggerAmt && !isExplosionsHappening()) {
			spawnExplosions(newAmt, explosionTexture, popFx, puffFx);
			clearedAmount++;
			System.out.println("destroyed " + numDestroyed + ", cleared " + clearedAmount + ", taken "
					+ Math.round(elapsedTime) + " seconds");
		}
	}

	private void spawnExplosions(final int amount, final TextureRegion explosionTexture, final Sound popFx,
			final Sound puffFx) {
		final Rectangle bounds = getBounds();
		for (int i = 0; i < amount; i++) {
			Explosion exp = new Explosion(bounds, explosionTexture, puffFx, popFx);
			float randomX = MathUtils.random(bounds.x + exp.getWidth(), bounds.x + bounds.width - exp.getWidth());
			float randomY = MathUtils.random(bounds.y + exp.getHeight(), bounds.y + bounds.height - exp.getHeight());
			exp.setPosition(randomX, randomY);
			exp.setSpeedModifier(2f);
			exp.setDeathAction(new Runnable() {
				@Override
				public void run() {
					numDestroyed++;
				}
			});
			explosions.addActor(exp);
		}
	}

	private boolean isExplosionsHappening() {
		boolean exploding = false;
		Actor[] actors = explosions.getChildren().begin();
		for (int i = 0, n = explosions.getChildren().size; i < n; i++) {
			if (actors[i] instanceof Explosion) {
				Explosion exp = (Explosion) actors[i];
				if (exp.getState() == ExplosionState.EXPLODING) {
					exploding = true;
				}
			}
		}
		explosions.getChildren().end();
		return exploding;
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
		stage.dispose();
		explosionTexture.getTexture().dispose();
		popFx.dispose();
		puffFx.dispose();
	}

	private void setupBackground() {
		Texture backgroundTexture = new Texture("backgrounds/title.png");
		Image background = new Image(backgroundTexture);
		background.setPosition(0, 0);
		background.setFillParent(true);
		stage.addActor(background);
	}

	public Rectangle getBounds() {
		// magic numbers represent border in background image
		return new Rectangle(16f, 8f, Config.WIDTH - 32f, Config.HEIGHT - 16f);
	}
}
