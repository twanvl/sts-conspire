package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import conspire.powers.PoisonWeaponsPower;
import javassist.CtBehavior;

@SpirePatch(clz = AbstractMonster.class, method = "damage", paramtypez = {DamageInfo.class})
public class PoisonWeaponsPowerPatch {
    @SpireInsertPatch(locator = Locator.class, localvars={"p","damageAmount"})
    public static void Insert(AbstractMonster self, DamageInfo info, AbstractPower p, @ByRef int[] damageAmount) {
        if (p instanceof PoisonWeaponsPower) {
            damageAmount[0] = ((PoisonWeaponsPower)p).onAttackToPoison(info, damageAmount[0], self);
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.powers.AbstractPower", "onAttack");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
