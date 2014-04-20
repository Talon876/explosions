package org.nolat.explosions.screens;

import org.nolat.explosions.utils.FontUtils;
import org.nolat.explosions.utils.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BonusGame implements Screen {

    private int score = 0;
    private float rechargeTime = 0.5f;
    private float timer = 0f;
    private boolean onCooldown = false;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private Body box;
    private Body ball;
    private Body ball2;

    private final float speed = 500f;
    private final Vector2 movement = new Vector2();

    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    private BitmapFont scoreFont = null;
    private SpriteBatch batch;

    public BonusGame() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, camera.combined);
        world.step(1 / 60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        camera.position.set(new Vector3(box.getPosition(), 0));
        camera.update();

        box.applyForceToCenter(movement, true);
        updateMovement();
        updateScore();
        updateTimer();
        batch.begin();
        scoreFont.setColor(Color.WHITE);
        scoreFont.draw(batch, "Score: " + score, 32, Gdx.graphics.getHeight() - 32);
        if (!onCooldown) {
            scoreFont.setColor(Color.GREEN);
        }
        scoreFont.draw(batch, getPowerBar(), 32, 32);
        batch.end();

    }

    private String getPowerBar() {
        String powerBar = "Ability: ";
        if (onCooldown) {
            int bars = (int) ((timer / rechargeTime) * 20f);
            for (int i = 0; i < bars; i++) {
                powerBar += "|";
            }
            return powerBar;
        }
        return powerBar + "||||||||||||||||||||";
    }

    private void updateTimer() {
        if (onCooldown) {
            timer += Gdx.graphics.getDeltaTime();
            if (timer >= rechargeTime) {
                onCooldown = false;
                timer = 0f;
            }
        }
    }

    private void updateMovement() {

        if (Gdx.input.isKeyPressed(Keys.W)) {
            movement.y = speed;
        } else if (Gdx.input.isKeyPressed(Keys.S)) {
            movement.y = -speed;
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            movement.x = -speed;
        } else if (Gdx.input.isKeyPressed(Keys.D)) {
            movement.x = speed;
        }

        if (!Gdx.input.isKeyPressed(Keys.W) && !Gdx.input.isKeyPressed(Keys.S)) {
            movement.y = 0;
        }
        if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
            movement.x = 0;
        }

        if (Gdx.input.justTouched() && !onCooldown) {
            int x = Gdx.input.getX();
            int y = Gdx.graphics.getHeight() - Gdx.input.getY();
            Vector3 clickPoint = new Vector3(x, y, 0);
            camera.unproject(clickPoint);
            Vector2 forcePoint = new Vector2(clickPoint.x, clickPoint.y);
            Vector2 forceDirection = forcePoint.sub(box.getPosition()).nor().scl(500f);
            box.applyLinearImpulse(new Vector2(forceDirection.x, -forceDirection.y), box.getPosition(), true);
            onCooldown = true;
            rechargeTime = 0.75f;
            //x pos = right
            //x neg = left
            //y pos = up
            //y neg = down
        }
    }

    private void updateScore() {
        int currScore = (int) ((ball.getLinearVelocity().len2() + ball2.getLinearVelocity().len2()) / 2);
        if (currScore > score) {
            score = currScore;
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / 15;
        camera.viewportHeight = height / 15;
        camera.update();
    }

    @Override
    public void show() {
        scoreFont = FontUtils.generateFont("fonts/square.ttf", 26, Color.WHITE);
        batch = new SpriteBatch();

        world = new World(new Vector2(0, -9.81f), true);
        World.setVelocityThreshold(5f);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                if (!onCooldown) {
                    box.applyTorque(8500f * amount, true);
                    rechargeTime = 3f;
                    onCooldown = true;
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                case Keys.ESCAPE:
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                    break;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }
        });

        // Body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(-1, 8);

        //bal shape
        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(0.5f);

        //fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.25f;
        fixtureDef.restitution = 0.95f;
        fixtureDef.shape = ballShape;

        ball = world.createBody(bodyDef);
        ball.createFixture(fixtureDef);
        bodyDef.position.set(3, 18);
        ball2 = world.createBody(bodyDef);
        ball2.createFixture(fixtureDef);
        ballShape.dispose();

        //GROUND
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        ChainShape groundShape = new ChainShape();

        //@formatter:off
        groundShape.createChain(new Vector2[] {
                new Vector2(-50, 50),
                new Vector2(-45, 40),
                new Vector2(-50, 5),
                new Vector2(-10, 0),
                new Vector2(-.25f, 2),
                new Vector2(0, 0),
                new Vector2(10, 0),
                new Vector2(15, 4),
                new Vector2(50, 4),
                new Vector2(70, 25),
                new Vector2(50, 50),
                new Vector2(0, 40),
                new Vector2(-50, 50) });
        //@formatter:on

        fixtureDef.shape = groundShape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0;

        world.createBody(bodyDef).createFixture(fixtureDef);
        groundShape.dispose();

        //BOX
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(2.25f, 10);

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(0.5f, 1.5f);

        fixtureDef.shape = boxShape;
        fixtureDef.friction = 0.75f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.density = 5f;

        box = world.createBody(bodyDef);
        box.createFixture(fixtureDef);
        boxShape.dispose();
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
        world.dispose();
        debugRenderer.dispose();
        scoreFont.dispose();
        batch.dispose();
    }
}
