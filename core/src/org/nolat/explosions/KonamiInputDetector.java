package org.nolat.explosions;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class KonamiInputDetector implements InputProcessor {
    private final Queue<Integer> keycodes = new LinkedList<>();
    private static final int[] KONAMI_CODE = new int[] { Keys.UP, Keys.UP, Keys.DOWN, Keys.DOWN, Keys.LEFT, Keys.RIGHT,
        Keys.LEFT, Keys.RIGHT, Keys.B, Keys.A, Keys.ENTER };
    private final Runnable run;

    /**
     * Detects entry of konami code
     * 
     * @param run
     *            runs this runnable if the code is detected
     */
    public KonamiInputDetector(Runnable run) {
        this.run = run;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycodes.size() >= 11) {
            keycodes.remove();
        }
        keycodes.add(keycode);
        if (checkForKonami()) {
            run.run();
        }
        return false;
    }

    private boolean checkForKonami() {
        Integer[] currCode = new Integer[11];
        keycodes.toArray(currCode);
        if (keycodes.size() == 11) {
            boolean isKonami = true;
            for (int i = 0; i < currCode.length; i++) {
                isKonami = isKonami && (currCode[i] == KONAMI_CODE[i]);
            }
            return isKonami;
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

}
