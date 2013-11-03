package org.nolat.explosions.screens;

import org.nolat.explosions.InputAdapter;
import org.nolat.explosions.entities.LevelGenerator;
import org.nolat.explosions.entities.Player;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class DoodleJump implements Screen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    private LevelGenerator levelGenerator;
    private Player player;

    private Vector3 bottomLeft, bottomRight;

    private final Array<Body> tmpBodies = new Array<>();

    public DoodleJump(String level) {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player.getBody().getPosition().x < bottomLeft.x) {
            player.getBody().setTransform(bottomRight.x, player.getBody().getPosition().y, player.getBody().getAngle());
        } else if (player.getBody().getPosition().x > bottomRight.x) {
            player.getBody().setTransform(bottomLeft.x, player.getBody().getPosition().y, player.getBody().getAngle());
        }
        player.update();
        world.step(1 / 60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        camera.position.y = player.getBody().getPosition().y > camera.position.y ? player.getBody().getPosition().y
                : camera.position.y;

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.getBodies(tmpBodies);
        for (Body body : tmpBodies) {
            if (body.getUserData() != null && body.getUserData() instanceof Sprite) {
                Sprite sprite = (Sprite) body.getUserData();
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2,
                        body.getPosition().y - sprite.getHeight() / 2);
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                sprite.draw(batch);
            }
        }
        batch.end();

        debugRenderer.render(world, camera.combined);
        levelGenerator.generate(camera.position.y + camera.viewportHeight / 2);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / 25;
        camera.viewportHeight = height / 25;
        camera.update();
    }

    @Override
    public void show() {
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            //            Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);
        }

        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);

        // Body and Fixture defintions to be re-used
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        //Player
        player = new Player(world, 0, 2, 1f);
        world.setContactFilter(player);
        world.setContactListener(player);

        Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                camera.zoom += amount / 5f;
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                case Keys.ESCAPE:
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu());
                    break;
                case Keys.NUM_0:
                    camera.zoom = 1f;
                    break;
                }
                return false;
            }
        }, player));

        //GROUND
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        ChainShape groundShape = new ChainShape();
        bottomLeft = new Vector3(0, Gdx.graphics.getHeight(), 0);
        bottomRight = new Vector3(Gdx.graphics.getWidth(), bottomLeft.y, 0);
        camera.unproject(bottomLeft);
        camera.unproject(bottomRight);
        groundShape.createChain(new float[] { bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y });

        fixtureDef.shape = groundShape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0;

        Body ground = world.createBody(bodyDef);
        ground.createFixture(fixtureDef);

        groundShape.dispose();

        levelGenerator = new LevelGenerator(ground, bottomLeft.x, bottomRight.x, player.WIDTH, player.HEIGHT * 3,
                player.WIDTH * 1.5f, player.WIDTH * 3.5f, player.WIDTH / 3, 20 * MathUtils.degRad);
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
        batch.dispose();
    }
}
