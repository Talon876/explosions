package org.nolat.explosions;

import java.util.HashMap;

public class LevelInfo {
    private static HashMap<Integer, LevelInfo> levelData = new HashMap<>();

    static {
        levelData.put(0, new LevelInfo(2, 10));
        levelData.put(1, new LevelInfo(3, 15));
        levelData.put(2, new LevelInfo(5, 20));
        levelData.put(3, new LevelInfo(7, 25));
        levelData.put(4, new LevelInfo(12, 30));
        levelData.put(5, new LevelInfo(18, 35));
        levelData.put(6, new LevelInfo(23, 40));
        levelData.put(7, new LevelInfo(27, 45));
        levelData.put(8, new LevelInfo(35, 50));
        levelData.put(9, new LevelInfo(43, 55));
        levelData.put(10, new LevelInfo(50, 60));
        levelData.put(11, new LevelInfo(60, 65));
        //        levelData.put(0, new LevelInfo(1, 5));
        //        levelData.put(1, new LevelInfo(2, 10));
        //        levelData.put(2, new LevelInfo(4, 15));
        //        levelData.put(3, new LevelInfo(6, 20));
        //        levelData.put(4, new LevelInfo(10, 25));
        //        levelData.put(5, new LevelInfo(15, 30));
        //        levelData.put(6, new LevelInfo(18, 35));
        //        levelData.put(7, new LevelInfo(22, 40));
        //        levelData.put(8, new LevelInfo(30, 45));
        //        levelData.put(9, new LevelInfo(37, 50));
        //        levelData.put(10, new LevelInfo(48, 55));
        //        levelData.put(11, new LevelInfo(55, 60));
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
