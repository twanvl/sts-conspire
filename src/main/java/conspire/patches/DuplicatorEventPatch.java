package conspire.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Duplicator;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import basemod.ReflectionHacks;

public class DuplicatorEventPatch {
    public static final String ID = "conspire:DuplicatorExtra";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    @SpirePatch(clz = Duplicator.class, method = SpirePatch.CONSTRUCTOR)
    public static class Constructor {
        public static void Postfix(Duplicator self) {
            // insert new choice at position 1
            self.imageEventText.updateDialogOption(1, OPTIONS[0]);
            self.imageEventText.setDialogOption(Duplicator.OPTIONS[1]);
        }
    }

    @SpirePatch(clz = Duplicator.class, method = "buttonEffect")
    public static class ButtonEffect {
        public static SpireReturn Prefix(Duplicator self, @ByRef int[] buttonPressed) {
            if ((int)ReflectionHacks.getPrivate(self, Duplicator.class, "screenNum") == 0) {
                if (buttonPressed[0] == 1) {
                    self.imageEventText.updateBodyText(DESCRIPTIONS[0]);
                    self.imageEventText.updateDialogOption(0, Duplicator.OPTIONS[1]);
                    self.imageEventText.clearRemainingOptions();
                    duplicateAll();
                    ReflectionHacks.setPrivate(self, Duplicator.class, "screenNum", 2);
                    return SpireReturn.Return(null);
                } else if (buttonPressed[0] == 2) {
                    buttonPressed[0] = 1; // original choice 1
                }
            }
            return SpireReturn.Continue();
        }

        private static void duplicateAll() {
            ArrayList<AbstractCard> newCards = new ArrayList<>();
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                AbstractCard c2 = c.makeStatEquivalentCopy();
                c2.inBottleFlame = false;
                c2.inBottleLightning = false;
                c2.inBottleTornado = false;
                newCards.add(c2);
            }
            for (AbstractCard c : newCards) {
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float)Settings.WIDTH / 2.0f, (float)Settings.HEIGHT / 2.0f));
            }
        }
    }

}