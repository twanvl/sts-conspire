package conspire.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class Boomerang extends AbstractConspireRelic {
    public static final String ID = "conspire:Boomerang";
    private boolean used = false;

    public Boomerang() {
        super(ID, AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
    }

    public static void onUseCardAction(UseCardAction act, AbstractCard card, AbstractCreature target) {
        AbstractRelic self = AbstractDungeon.player.getRelic(ID);
        if (self == null) return;
        Boomerang boomerang = (Boomerang)self;
        if (!boomerang.used && card.type == CardType.ATTACK && !card.exhaust && !card.exhaustOnUseOnce) {
            boomerang.flash();
            act.reboundCard = true;
            boomerang.used = true;
        }
    }

    @Override
    public void atTurnStart() {
        this.used = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Boomerang();
    }
}