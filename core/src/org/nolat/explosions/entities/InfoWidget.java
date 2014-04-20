package org.nolat.explosions.entities;

import org.nolat.explosions.Config;
import org.nolat.explosions.entities.Explosion.ExplosionState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class InfoWidget extends Actor {

    public static boolean IS_VISIBLE = false;
    private final ShapeRenderer sr;

    private final Vector2 avgVelocity;
    private final Vector2 avgPosition;
    private final Array<Vector2> locations;
    private final Array<Vector2> velocities;

    public InfoWidget() {
        sr = new ShapeRenderer();
        avgVelocity = new Vector2(1, 0);
        avgPosition = new Vector2(0, 0);
        locations = new Array<>();
        velocities = new Array<>();
        setPosition(Config.WIDTH / 2, Config.HEIGHT / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (IS_VISIBLE) {
            Actor[] actors = getStage().getRoot().getChildren().begin();

            int total = 0;
            avgVelocity.set(0, 0);
            avgPosition.set(0, 0);
            locations.clear();
            velocities.clear();

            for (int i = 0, n = getStage().getRoot().getChildren().size; i < n; i++) {
                if (actors[i] instanceof Explosion) {
                    Explosion exp = (Explosion) actors[i];
                    if (exp.getState() == ExplosionState.MOVING) {
                        avgVelocity.add(exp.getVelocity());
                        avgPosition.add(exp.getX(), exp.getY());
                        locations.add(new Vector2(exp.getX(), exp.getY()));
                        velocities.add(exp.getVelocity());
                        total++;
                    }
                }
            }
            avgVelocity.nor();
            avgPosition.set(avgPosition.x / total, avgPosition.y / total);
            getStage().getRoot().getChildren().end();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (IS_VISIBLE) {
            batch.end();
            sr.begin(ShapeType.Line);
            sr.rect(getX(), getY(), 2f, 2f);
            sr.circle(avgPosition.x, avgPosition.y, 5f);
            sr.setColor(new Color(0f, 1f, 0f, 0.05f));

            sr.setColor(Color.YELLOW);
            Vector3 point3 = new Vector3(getX(), getY(), 0);
            getStage().getCamera().unproject(point3);
            sr.line(point3.x, point3.y, point3.x + avgVelocity.x * 25f, point3.y + avgVelocity.y * 25f);

            for (int i = 0; i < locations.size; i++) {
                sr.line(locations.get(i).x, locations.get(i).y, locations.get(i).x + velocities.get(i).x * 300f,
                        locations.get(i).y + velocities.get(i).y * 300f);
            }

            sr.end();
            batch.begin();
        }
    }
}
