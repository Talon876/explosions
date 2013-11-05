package org.nolat.explosions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.utils.Array;

public class LevelInfo {
    private static HashMap<Integer, LevelInfo> levelData = new HashMap<>();

    static {
        Array<LevelInfo> tmp = new Array<LevelInfo>();
        LevelInfo info = new LevelInfo(3, 4);
        for (int i = -4; info.numNeededToPass > 2; i--) {
            info = LevelInfo.generateLevelInfo(i);
            tmp.add(info);
        }
        tmp.reverse();
        for (int i = 0; i < tmp.size; i++) {
            tmp.get(i).setLevel(i);
            levelData.put(i, tmp.get(i));
            if (Config.debug) {
                System.out.println(tmp.get(i));
            }
        }
    }

    public static List<LevelInfo> getAllLevels() {
        List<LevelInfo> list = new ArrayList<>();
        for (int i = 0; i < levelData.size(); i++) {
            list.add(levelData.get(i));
        }
        return list;
    }

    public static LevelInfo generateLevelInfo(int x) {
        int needed = (int) Math.round((Math.pow(Math.E, -Math.pow(x / 20.0, 2.0)) * 65f + 1f));
        int total = (int) Math.round((Math.pow(Math.E, -Math.pow(x / 27.0, 2.0)) * 65f + 2f));
        return new LevelInfo(needed, total);
    }

    public static LevelInfo getLevelInfo(int level) {
        return levelData.get(level);
    }

    public static int getNumberOfLevels() {
        return levelData.size();
    }

    public int level;
    public final int numNeededToPass;
    public final int numTotal;

    private LevelInfo(int numNeededToPass, int numTotal) {
        if (numNeededToPass > numTotal) {
            throw new IllegalArgumentException("Amount needed to pass must be less than or equal to the total.");
        }
        level = levelData.size();
        this.numNeededToPass = numNeededToPass;
        this.numTotal = numTotal;
    }

    private void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "[" + level + ": " + numNeededToPass + " / " + numTotal + "]";
    }
}
