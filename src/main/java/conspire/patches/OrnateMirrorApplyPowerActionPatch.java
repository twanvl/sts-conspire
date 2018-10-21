package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import basemod.ReflectionHacks;
import conspire.monsters.OrnateMirror;
import javassist.CtBehavior;

@SpirePatch(clz = ApplyPowerAction.class, method = "update")
public class OrnateMirrorApplyPowerActionPatch {
    @SpireInsertPatch(locator = Locator.class)
    public static void Insert(ApplyPowerAction self) {
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m instanceof OrnateMirror) {
                AbstractPower powerToApply = (AbstractPower) ReflectionHacks.getPrivate(self, ApplyPowerAction.class, "powerToApply");
                ((OrnateMirror)m).onApplyPower(powerToApply, self.target, self.source);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
