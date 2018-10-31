package conspire.cards.curse;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.BlueCandle;

import basemod.abstracts.CustomCard;
import conspire.Conspire;

public class NecroticWound extends CustomCard {
    public static final String ID = "conspire:NecroticWound";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = -2;
    private static final int WOUNDS = 1;

    public NecroticWound() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.CURSE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = WOUNDS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!this.dontTriggerOnUseCard && p.hasRelic(BlueCandle.ID)) {
            this.useBlueCandle(p);
        }
    }

    @Override
    public void triggerWhenDrawn() {
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Wound(), this.magicNumber, true, false));
    }

    @Override
    public AbstractCard makeCopy() {
        return new NecroticWound();
    }

    @Override
    public void upgrade() {
    }
}
