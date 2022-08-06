package me.wisemann64.soulland.players;

import java.util.HashMap;
import java.util.Map;

public class Mastery {

    private final SLPlayer owner;
    private int xp;
    private int level;

    public Mastery(SLPlayer owner) {
        this.owner = owner;
        /*
        xp
        level
         */
        xp = 0;
        level = 0;
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

    }

    private static final Map<Integer, Integer> LEVEL_UP = levelUpRequirements();
    private static final Map<Integer, Integer> CUMULATIVE = cumulativeLevelUp();

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

    private static Map<Integer, Integer> cumulativeLevelUp() {
        HashMap<Integer,Integer> toRet = new HashMap<>();
        toRet.put(1,250);
        toRet.put(2,623);
        toRet.put(3,1177);
        toRet.put(4,1988);
        toRet.put(5,3150);
        toRet.put(6,4775);
        toRet.put(7,6993);
        toRet.put(8,9952);
        toRet.put(9,13818);
        toRet.put(10,18775);
        toRet.put(11,25025);
        toRet.put(12,32788);
        toRet.put(13,42302);
        toRet.put(14,53823);
        toRet.put(15,67625);
        toRet.put(16,84000);
        toRet.put(17,103258);
        toRet.put(18,125727);
        toRet.put(19,151753);
        toRet.put(20,181700);
        toRet.put(21,215950);
        toRet.put(22,254903);
        toRet.put(23,298977);
        toRet.put(24,348608);
        toRet.put(25,404250);
        toRet.put(26,466375);
        toRet.put(27,535473);
        toRet.put(28,612052);
        toRet.put(29,696638);
        toRet.put(30,789775);
        toRet.put(31,892025);
        toRet.put(32,1003968);
        toRet.put(33,1126202);
        toRet.put(34,1259343);
        toRet.put(35,1404025);
        toRet.put(36,1560900);
        toRet.put(37,1730638);
        toRet.put(38,1913927);
        toRet.put(39,2111473);
        toRet.put(40,2324000);
        return toRet;
    }
}
