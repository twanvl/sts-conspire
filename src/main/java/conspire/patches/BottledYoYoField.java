package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(clz=AbstractCard.class, method=SpirePatch.CLASS)
public class BottledYoYoField {
    public static SpireField<Boolean> inBottledYoYo = new SpireField<>(() -> false);

    @SpirePatch(clz=AbstractCard.class, method="makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            inBottledYoYo.set(result, inBottledYoYo.get(self));
            return result;
        }
    }

    @SpirePatch(clz=Soul.class, method="discard", paramtypez={AbstractCard.class, boolean.class})
    public static class DiscardBottledCardPatch {
        public static void Postfix(Soul soul, AbstractCard card, boolean visualOnly) {
            if (card != null && inBottledYoYo.get(card) && !visualOnly) {
                soul.isReadyForReuse = true; // don't show animation into deck
                AbstractDungeon.player.discardPile.moveToDeck(card, true);
            }
        }
    }
}
