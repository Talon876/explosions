package org.nolat.explosions.entities;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player implements InputProcessor, ContactFilter, ContactListener {

    private final Body body;
    private final Fixture fixture;
    public final float WIDTH, HEIGHT;
    private final Vector2 velocity = new Vector2();
    private final float movementForce = 500, jumpPower = 45;

    public Player(World world, float x, float y, float width) {
        WIDTH = width;
        HEIGHT = width * 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true; //prevent spinning

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH / 2, HEIGHT / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0f; //we handle this
        fixtureDef.friction = .8f;
        fixtureDef.density = 3f;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update() {
        body.applyForceToCenter(velocity, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
        case Keys.A:
            velocity.x = -movementForce;
            break;
        case Keys.D:
            velocity.x = movementForce;
            break;
        case Keys.SPACE:
            body.applyLinearImpulse(0, jumpPower, body.getWorldCenter().x, body.getWorldCenter().y, true);
            break;
        default:
            return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.A || keycode == Keys.D) {
            velocity.x = 0;
        } else {
            return false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public float getRestitution() {
        return fixture.getRestitution();
    }

    public void setRestitution(float restitution) {
        fixture.setRestitution(restitution);
    }

    public Body getBody() {
        return body;
    }

    public Fixture getFixture() {
        return fixture;
    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        if (fixtureA.equals(fixture) || fixtureB.equals(fixture)) { //us?
            return body.getLinearVelocity().y < 0; //return true if going up
        }
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    //collision calculation stuff (SOLVE)

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (contact.getFixtureA().equals(fixture) || contact.getFixtureB().equals(fixture)) {
            if (contact.getWorldManifold().getPoints()[0].y <= body.getPosition().y - HEIGHT / 2) {
                body.applyLinearImpulse(0, jumpPower, body.getWorldCenter().x, body.getWorldCenter().y, true);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

}
