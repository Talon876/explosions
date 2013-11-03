package org.nolat.explosions.screens;

import org.nolat.explosions.InputAdapter;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LightExperiment implements Screen {

    OrthographicCamera camera;

    float width, height;

    FPSLogger logger;

    World world;
    Box2DDebugRenderer renderer;

    Body circleBody;

    RayHandler handler;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(1 / 60f, 8, 3);
        renderer.render(world, camera.combined);
        handler.updateAndRender();
        logger.log();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        width = Gdx.graphics.getWidth() / 5;
        height = Gdx.graphics.getHeight() / 5;

        camera = new OrthographicCamera(width, height);
        camera.position.set(width * 0.5f, height * 0.5f, 0);
        camera.update();

        world = new World(new Vector2(0, -9.81f), false);
        renderer = new Box2DDebugRenderer();
        logger = new FPSLogger();

        //ball
        BodyDef circleDef = new BodyDef();
        circleDef.type = BodyType.DynamicBody;
        circleDef.position.set(width / 2f, height / 2f);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(3f);

        FixtureDef circleFixture = new FixtureDef();
        circleFixture.shape = circleShape;
        circleFixture.density = 0.4f;
        circleFixture.friction = 0.2f;
        circleFixture.restitution = 0.8f;

        circleBody = world.createBody(circleDef);
        circleBody.createFixture(circleFixture);
        circleShape.dispose();

        //ground
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyType.StaticBody;
        groundDef.position.set(0, 3f);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox((camera.viewportWidth * 2), 3);

        Body groundBody = world.createBody(groundDef);
        groundBody.createFixture(groundBox, 0);

        //lights
        handler = new RayHandler(world);
        handler.setCombinedMatrix(camera.combined);

        new PointLight(handler, 5000, Color.CYAN, 200f, (width / 2) - 50, (height / 2) + 15);
        new ConeLight(handler, 5000, Color.PINK, 400f, (width / 2) + 50, (height / 2) + 15, 270, 35);

        //input
        Gdx.input.setInputProcessor(new InputAdapter() {

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
        world.dispose();
    }

}
