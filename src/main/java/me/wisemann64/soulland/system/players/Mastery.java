package me.wisemann64.soulland.system.players;

import java.util.HashMap;
import java.util.Map;

public class Mastery {

    private final SLPlayer owner;
    private int xp = 0;
    private int level = 0;

    public Mastery(SLPlayer owner) {
        this.owner = owner;
    }

    public void addXp(int count) {
        xp += count;
        checkLevel();
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    private void checkLevel() {
        int level = 0;
        while (cumulativeXp(level+1) <= xp && level < 40) level++;
        this.level = level;
    }

    void initialize(int xp, int level) {
        this.xp = xp;
        this.level = level;
        checkLevel();
    }

    public float getProgress() {
        if (level == 40) return 1.0f;
        float t = (float) (xp - cumulativeXp(level))/LEVEL_UP.get(level);
        return Math.max(0,Math.min(1,t));
    }

    private static final Map<Integer, Integer> LEVEL_UP = levelUpRequirements();

    private static Map<Integer,Integer> levelUpRequirements() {
        HashMap<Integer,Integer> toRet = new HashMap<>();
        toRet.put(0,250);
        toRet.put(1,373);
        toRet.put(2,554);
        toRet.put(3,811);
        toRet.put(4,1162);
        toRet.put(5,1625);
        toRet.put(6,2218);
        toRet.put(7,2959);
        toRet.put(8,3866);
        toRet.put(9,4957);
        toRet.put(10,6250);
        toRet.put(11,7763);
        toRet.put(12,9514);
        toRet.put(13,11521);
        toRet.put(14,13802);
        toRet.put(15,16375);
        toRet.put(16,19258);
        toRet.put(17,22469);
        toRet.put(18,26026);
        toRet.put(19,29947);
        toRet.put(20,34250);
        toRet.put(21,38953);
        toRet.put(22,44074);
        toRet.put(23,49631);
        toRet.put(24,55642);
        toRet.put(25,62125);
        toRet.put(26,69098);
        toRet.put(27,76579);
        toRet.put(28,84586);
        toRet.put(29,93137);
        toRet.put(30,102250);
        toRet.put(31,111943);
        toRet.put(32,122234);
        toRet.put(33,133141);
        toRet.put(34,144682);
        toRet.put(35,156875);
        toRet.put(36,169738);
        toRet.put(37,183289);
        toRet.put(38,197546);
        toRet.put(39,212527);
        return toRet;
    }

    public static int cumulativeXp(int level) {
        level = Math.max(0,Math.min(40,level));
        int xp = 0;
        for (int i = 0; i < level; i++) xp += LEVEL_UP.get(i);
        return xp;
    }
}
