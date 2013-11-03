package org.nolat.explosions.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class LevelGenerator {

    private Body environment;
    private float leftEdge, rightEdge, minGap, maxGap, minWidth, maxWidth, height;
    private final float angle;
    private float platformY;

    public LevelGenerator(Body environment, float leftEdge, float rightEdge, float minGap, float maxGap,
            float minWidth, float maxWidth, float height, float angle) {
        this.environment = environment;
        this.leftEdge = leftEdge;
        this.rightEdge = rightEdge;
        this.minGap = minGap;
        this.maxGap = maxGap;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.height = height;
        this.angle = angle;
    }

    public void generate(float topEdge) {
        if (platformY + MathUtils.random(minGap, maxGap) > topEdge) {
            return;
        } else {
            platformY = topEdge;
            float width = MathUtils.random(minWidth, maxWidth);
            float x = MathUtils.random(leftEdge, rightEdge - width);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2, height / 2, new Vector2(x + width / 2, platformY + height / 2),
                    MathUtils.random(-angle / 2, angle / 2));

            environment.createFixture(shape, 0); //density doesn't matter for static
            shape.dispose();
        }
    }

    public Body getEnvironment() {
        return environment;
    }

    public void setEnvironment(Body environment) {
        this.environment = environment;
    }

    public float getLeftEdge() {
        return leftEdge;
    }

    public void setLeftEdge(float leftEdge) {
        this.leftEdge = leftEdge;
    }

    public float getRightEdge() {
        return rightEdge;
    }

    public void setRightEdge(float rightEdge) {
        this.rightEdge = rightEdge;
    }

    public float getMinGap() {
        return minGap;
    }

    public void setMinGap(float minGap) {
        this.minGap = minGap;
    }

    public float getMaxGap() {
        return maxGap;
    }

    public void setMaxGap(float maxGap) {
        this.maxGap = maxGap;
    }

    public float getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}
