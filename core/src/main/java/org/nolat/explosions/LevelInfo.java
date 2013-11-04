package org.nolat.explosions;

import java.util.HashMap;

public class LevelInfo {
    private static HashMap<Integer, LevelInfo> levelData = new HashMap<>();

    static {
        //e^(-(x/10)^2) * 50 + 1, e^(-(x/15)) * 50 + 2
        levelData.put(0, new LevelInfo(2, 10));
        levelData.put(1, new LevelInfo(2, 12));
        levelData.put(2, new LevelInfo(3, 14));
        levelData.put(3, new LevelInfo(4, 16));
        levelData.put(4, new LevelInfo(5, 18));
        levelData.put(5, new LevelInfo(6, 20));
        levelData.put(6, new LevelInfo(8, 23));
        levelData.put(7, new LevelInfo(10, 26));
        levelData.put(8, new LevelInfo(13, 28));
        levelData.put(9, new LevelInfo(16, 31));
        levelData.put(10, new LevelInfo(19, 34));
        levelData.put(11, new LevelInfo(23, 37));
        levelData.put(12, new LevelInfo(27, 40));
        levelData.put(13, new LevelInfo(32, 42));
        levelData.put(14, new LevelInfo(36, 45));
        levelData.put(15, new LevelInfo(40, 47));
        levelData.put(16, new LevelInfo(44, 49));
        levelData.put(17, new LevelInfo(47, 50));
        levelData.put(18, new LevelInfo(49, 51));
    }

    public static LevelInfo getLevelInfo(int level) {
        return levelData.get(level);
    }

    public static int getNumberOfLevels() {
        return levelData.size();
    }

    public final int level;
    public final int numNeededToPass;
    public final int numTotal;

    private LevelInfo(int numNeededToPass, int numTotal) {
        level = levelData.size();
        this.numNeededToPass = numNeededToPass;
        this.numTotal = numTotal;
    }

}
