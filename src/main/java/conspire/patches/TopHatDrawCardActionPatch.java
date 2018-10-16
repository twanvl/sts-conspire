package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.defect.ScrapeAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;

import conspire.relics.TopHat;
import javassist.CtBehavior;

@SpirePatch(clz = DrawCardAction.class, method = "update")
@SpirePatch(clz = ScrapeAction.class, method = "update")
public class TopHatDrawCardActionPatch {
    @SpireInsertPatch(locator = Locator.class)
    public static void Insert(AbstractGameAction self) {
        if (AbstractDungeon.player.hasRelic(TopHat.ID)) {
            AbstractDungeon.player.getRelic(TopHat.ID).flash();
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DrawCardNextTurnPower(AbstractDungeon.player, self.amount), self.amount));
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.characters.AbstractPlayer", "createHandIsFullDialog");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
