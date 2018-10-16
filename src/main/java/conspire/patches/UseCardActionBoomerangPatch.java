package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

import conspire.relics.Boomerang;

@SpirePatch(clz=com.megacrit.cardcrawl.actions.utility.UseCardAction.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, AbstractCreature.class})
public class UseCardActionBoomerangPatch {
    public static void Postfix(UseCardAction self, AbstractCard card, AbstractCreature target) {
        Boomerang.onUseCardAction(self, card, target);
    }
}
