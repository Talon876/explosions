package org.nolat.explosions.screens;

import org.nolat.explosions.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;

public class JointExperiment implements Screen {

    private final String level;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Body box;
    private final float speed = 500f;
    private final Vector2 movement = new Vector2();

    //    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    private Sprite boxSprite;

    private final Array<Body> tmpBodies = new Array<>();

    public JointExperiment(String level) {
        this.level = level;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        box.applyForceToCenter(movement, true);
        camera.position.set(box.getPosition().x, box.getPosition().y, 0);
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
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / 25;
        camera.viewportHeight = height / 25;
        camera.update();
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        camera = new OrthographicCamera();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                camera.zoom += amount / 5f;
                return true;
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

                if (keycode == Keys.W) {
                    movement.y = speed;
                }
                if (keycode == Keys.S) {
                    movement.y = -speed;
                }

                if (keycode == Keys.D) {
                    movement.x = speed;
                }
                if (keycode == Keys.A) {
                    movement.x = -speed;
                }

                return true;
            }

            @Override
            public boolean keyUp(int keycode) {

                if (keycode == Keys.W) {
                    movement.y = 0;
                }
                if (keycode == Keys.S) {
                    movement.y = 0;
                }

                if (keycode == Keys.D) {
                    movement.x = 0;
                }
                if (keycode == Keys.A) {
                    movement.x = 0;
                }
                return true;
            }
        });

        // Body and Fixture defintions to be re-used
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        //BOX
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(2.25f, 10);

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(0.5f, 1f);

        fixtureDef.shape = boxShape;
        fixtureDef.friction = 0.75f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.density = 5f;

        box = world.createBody(bodyDef);
        box.createFixture(fixtureDef);

        boxSprite = new Sprite(new Texture("images/luigiFront.png"));
        boxSprite.setSize(0.5f * 2, 1 * 2); //meters
        boxSprite.setOrigin(boxSprite.getWidth() / 2, boxSprite.getHeight() / 2);
        box.setUserData(boxSprite);
        boxShape.dispose();

        //BALL
        CircleShape ballShape = new CircleShape();
        ballShape.setPosition(new Vector2(0, 1.5f));
        ballShape.setRadius(0.5f);

        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.25f;
        fixtureDef.restitution = 0.95f;
        fixtureDef.shape = ballShape;

        box.createFixture(fixtureDef);
        ballShape.dispose();

        //GROUND
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[] { new Vector2(-50, 50), new Vector2(-50, 5), new Vector2(-10, 0),
                new Vector2(-.25f, 2), new Vector2(0, 0), new Vector2(10, 0), new Vector2(15, 4), new Vector2(50, 4),
                new Vector2(50, 50), new Vector2(-50, 50) });

        fixtureDef.shape = groundShape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0;

        Body ground = world.createBody(bodyDef);
        ground.createFixture(fixtureDef);
        groundShape.dispose();

        //Static Other Box
        bodyDef.position.set(2, 7);

        PolygonShape otherBoxShape = new PolygonShape();
        otherBoxShape.setAsBox(0.25f, 0.25f);

        fixtureDef.shape = otherBoxShape;

        Body otherBox = world.createBody(bodyDef);
        otherBox.createFixture(fixtureDef);
        otherBoxShape.dispose();

        // DistanceJoint between other box and box
        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.bodyA = otherBox;
        distanceJointDef.bodyB = box;
        distanceJointDef.length = 5f;
        world.createJoint(distanceJointDef);

        //RopeJoint between ground and box
        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = ground;
        ropeJointDef.bodyB = box;
        ropeJointDef.collideConnected = true;
        ropeJointDef.maxLength = 7f;
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        //        world.createJoint(ropeJointDef);

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
        boxSprite.getTexture().dispose();
        batch.dispose();
    }
}
