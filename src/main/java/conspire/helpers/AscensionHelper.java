package conspire.helpers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;

public class AscensionHelper {
    public static boolean deadlier(EnemyType type) {
        switch (type) {
            case NORMAL: return AbstractDungeon.ascensionLevel >= 2;
            case ELITE:  return AbstractDungeon.ascensionLevel >= 3;
            case BOSS:   return AbstractDungeon.ascensionLevel >= 4;
            default:     return false;
        }
    }

    public static boolean tougher(EnemyType type) {
        switch (type) {
            case NORMAL: return AbstractDungeon.ascensionLevel >= 7;
            case ELITE:  return AbstractDungeon.ascensionLevel >= 8;
            case BOSS:   return AbstractDungeon.ascensionLevel >= 9;
            default:     return false;
        }
    }

    public static boolean harder(EnemyType type) {
        switch (type) {
            case NORMAL: return AbstractDungeon.ascensionLevel >= 17;
            case ELITE:  return AbstractDungeon.ascensionLevel >= 18;
            case BOSS:   return AbstractDungeon.ascensionLevel >= 19;
            default:     return false;
        }
    }
}