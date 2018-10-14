package conspire.cards.colorless;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.CustomCard;
import conspire.Conspire;
import conspire.actions.ObtainGoldAction;

public class SpireCoStock extends CustomCard {
    public static final String ID = "conspire:SpireCoStock";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int MIN_GOLD = -5;
    private static final int MAX_GOLD = 25;
    private static final int UPGRADE_GOLD = 4;

    public SpireCoStock() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = (MIN_GOLD + MAX_GOLD) / 2;
        this.exhaust = true;
        this.tags.add(CardTags.HEALING);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ObtainGoldAction(this.magicNumber));
    }

    @Override
    public void triggerWhenDrawn() {
        if (this.upgraded) {
            this.magicNumber = this.baseMagicNumber = AbstractDungeon.cardRng.random(MIN_GOLD, MAX_GOLD + 2*UPGRADE_GOLD);
        } else {
            this.magicNumber = this.baseMagicNumber = AbstractDungeon.cardRng.random(MIN_GOLD, MAX_GOLD);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SpireCoStock();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_GOLD);
        }
    }
}

