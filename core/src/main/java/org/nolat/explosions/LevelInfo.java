package org.nolat.explosions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.utils.Array;

public class LevelInfo {
    private static float PER_CENT_IGNORE = 0.935f;
    private static HashMap<Integer, LevelInfo> levelData = new HashMap<>();

    static {
        Array<LevelInfo> tmp = new Array<LevelInfo>();
        LevelInfo info = new LevelInfo(3, 4);
        for (int i = 0; info.numTotal <= 70; i++) {
            info = LevelInfo.generateLevelInfo(i);
            tmp.add(info);
        }
        //        tmp.reverse();
        //always add 0th level
        tmp.get(0).setLevel(0);
        levelData.put(0, tmp.get(0));

        int lvl = 1;
        for (int i = 1; i < tmp.size; i++) {
            float perCent = (float) tmp.get(i).numNeededToPass / (float) tmp.get(i).numTotal;
            if (perCent < PER_CENT_IGNORE) {

                if (!tmp.get(i - 1).equals(tmp.get(i))) {
                    tmp.get(i).setLevel(lvl);
                    levelData.put(lvl, tmp.get(i));
                    lvl++;
                } else {
                    System.out.println("Discarded " + tmp.get(i) + " for being a duplicate");
                }

            } else {
                if (Config.debug) {
                    System.out.println("Discarded " + tmp.get(i) + " for having " + perCent);
                }
            }
        }

        if (Config.debug) {
            System.out.println("\n--Levels--");
            for (LevelInfo li : getAllLevels()) {
                System.out.println(li);
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
        // I've gone insane: http://www.wolframalpha.com/input/?i=63-63*e%5E%28-%28%28x%2B5%29%2F30%29%5E2%29+%2B+4*%280.25*cos%280.65*x%29%29+%2B+0+and+x%3D70+and+x+and+63-63*e%5E%28-%28%28x%2B5%29%2F30%29%5E2%29+%2B+5*%280.45*sin%280.65*x%29%29+%2B+7+from+0+to+65
        // I'm definitely overcomplicating things
        double e_equation = 63.0 * Math.pow(Math.E, -((1.0 / 900.0) * Math.pow(x + 5.0, 2.0)));
        int needed = (int) Math.round(63.0 - e_equation + (1f * Math.cos(0.65f * x)));
        int total = (int) Math.round(70.0 - e_equation + (2.25f * Math.sin(0.65f * x)));
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
            throw new IllegalArgumentException("Amount needed to pass must be less than or equal to the total: "
                    + numNeededToPass + " is not less than " + numTotal);
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

    @Override
    public boolean equals(Object obj) {
        LevelInfo other = (LevelInfo) obj;
        return numNeededToPass == other.numNeededToPass && numTotal == other.numTotal;
    }
}
