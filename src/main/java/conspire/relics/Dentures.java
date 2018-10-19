package conspire.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class Dentures extends AbstractConspireRelic {
    public static final String ID = "conspire:Dentures";

    public Dentures() {
        super(ID, AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.CLINK);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction act) {
        if (AbstractDungeon.cardRandomRng.randomBoolean()) {
            AbstractDungeon.player.getRelic(ID).flash();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(card, 1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Dentures();
    }
}