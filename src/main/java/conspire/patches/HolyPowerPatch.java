package conspire.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

import conspire.powers.HolyPower;
import javassist.CtBehavior;

public class HolyPowerPatch {
    @SpirePatch(clz=AbstractCreature.class, method="renderRedHealthBar")
    public static class RenderRedHealthBar {
        @SpireInsertPatch(locator = Locator.class, localvars={"poisonAmt"})
        public static void Insert(AbstractCreature self, SpriteBatch sb, float x, float y, @ByRef int[] poisonAmt) {
            if (self.hasPower(HolyPower.POWER_ID)) {
                poisonAmt[0] = 0;
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(com.megacrit.cardcrawl.core.AbstractCreature.class, "hasPower");
            int[] locs = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            int[] newlocs = {locs[1]};
            return newlocs;
        }
    }
}
