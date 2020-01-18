package conspire.cards.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.CustomCard;
import conspire.Conspire;
import conspire.relics.SpecialSausage;

public class SausageOptionStrength extends CustomCard {
    public static final String ID = "conspire:SausageOptionStrength";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = -2;
    private SpecialSausage parent;

    public SausageOptionStrength(SpecialSausage parent, int amount) {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = amount;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onChoseThisOption() {
        parent.setBuff(SpecialSausage.Buff.STRENGTH);
    }

    @Override
    public AbstractCard makeCopy() {
        return new SausageOptionStrength(this.parent, this.baseMagicNumber);
    }

    @Override
    public void upgrade() {
    }
}
