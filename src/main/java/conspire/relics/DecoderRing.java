package conspire.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DecoderRing extends AbstractConspireRelic {
    public static final String ID = "conspire:DecoderRing";
    private static int DAMAGE = 30;
    private static CardType[] CODE = {CardType.ATTACK,CardType.ATTACK,CardType.ATTACK,CardType.SKILL,CardType.SKILL,CardType.ATTACK,CardType.ATTACK,CardType.SKILL};
    // AAASSAAS

    public DecoderRing() {
        super(ID, AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.CLINK);
        this.counter = 0;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.counter >= 0 && this.counter < CODE.length && card.type == CODE[this.counter]) {
            ++this.counter;
            if (this.counter == CODE.length) {
                this.counter = 0;
                this.flash();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(DAMAGE, true), DamageInfo.DamageType.THORNS, AttackEffect.SLASH_HORIZONTAL));
            }
			this.description = this.getUpdatedDescription();
        } else if (this.counter >= 2 && this.counter < CODE.length && CODE[this.counter-2] == CODE[0] && CODE[this.counter-1] == CODE[1] && card.type == CODE[2]) {
            this.counter = 3;
			this.description = this.getUpdatedDescription();
        } else if (this.counter >= 1 && this.counter < CODE.length && CODE[this.counter-1] == CODE[0] && card.type == CODE[1]) {
            this.counter = 2;
			this.description = this.getUpdatedDescription();
        } else if (card.type == CODE[0]) {
            this.counter = 1;
			this.description = this.getUpdatedDescription();
        } else {
            this.counter = 0;
			this.description = this.getUpdatedDescription();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DAMAGE + DESCRIPTIONS[1] + (this.counter >= 0 && this.counter < CODE.length ? DESCRIPTIONS[2+this.counter] : "");
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DecoderRing();
    }
}