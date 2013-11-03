package org.nolat.explosions.entities;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

public class Car implements InputProcessor {

    private final Body chassis;
    private final Body leftWheel;
    private final Body rightWheel;
    private final WheelJoint leftAxis;
    private final WheelJoint rightAxis;
    private final float motorSpeed = 75f;

    public Car(World world, FixtureDef chassisFixtureDef, FixtureDef wheelFixtureDef, float x, float y, float width,
            float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        //chassis
        PolygonShape chassisShape = new PolygonShape();
        //@formatter:off
        chassisShape.set(new float[] {
                -width / 2f, -height / 2, //bottom left
                width / 2f, -height / 2, //bottom right
                width / 2f * .4f, height / 2, //top right
                -width /2f * .8f, height / 2 * .8f //top left
        }); //counterclockwise order
        //@formatter:on

        chassisFixtureDef.shape = chassisShape;

        chassis = world.createBody(bodyDef);
        chassis.createFixture(chassisFixtureDef);

        //left wheel
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(height / 3.5f);

        wheelFixtureDef.shape = wheelShape;

        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFixtureDef);

        //right wheel
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(wheelFixtureDef);

        //left axis
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(-width / 2f * .75f + wheelShape.getRadius(), -height / 2 * 1.25f);
        axisDef.frequencyHz = chassisFixtureDef.density;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = chassisFixtureDef.density * 10;
        leftAxis = (WheelJoint) world.createJoint(axisDef);

        //right axis
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.x *= -1; //opposite side

        rightAxis = (WheelJoint) world.createJoint(axisDef);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
        case Keys.D:
            leftAxis.enableMotor(true);
            leftAxis.setMotorSpeed(-motorSpeed); //speed of the wheel is negative in clockwise direction
            break;
        case Keys.A:
            leftAxis.enableMotor(true);
            leftAxis.setMotorSpeed(motorSpeed);
            break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
        case Keys.D:
        case Keys.A:
            leftAxis.enableMotor(false);
            break;
        }
        return true;
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

    public Body getChassis() {
        return chassis;
    }

}
