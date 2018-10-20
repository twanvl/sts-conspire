package conspire.cards.status;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EvolvePower;
import com.megacrit.cardcrawl.powers.NoDrawPower;

import basemod.abstracts.CustomCard;
import conspire.Conspire;
import conspire.actions.ObtainGoldAction;

public class Treasure extends CustomCard {
    public static final String ID = "conspire:Treasure";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int GOLD = 2;
    private static final int UPGRADE_GOLD = 1;

    public Treasure() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = GOLD;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasRelic("Medical Kit")) {
            this.useMedicalKit(p);
        } else {
            AbstractMonster source = AbstractDungeon.getCurrRoom().monsters.getRandomMonster();
            AbstractDungeon.actionManager.addToBottom(new ObtainGoldAction(this.magicNumber, source, false));
        }
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (AbstractDungeon.player.hasRelic("Medical Kit")) {
            return true;
        }
        if (this.cardPlayable(m) && this.hasEnoughEnergy()) {
            return true;
        }
        return false;
    }

    @Override
    public void triggerWhenDrawn() {
        if (AbstractDungeon.player.hasPower(EvolvePower.POWER_ID) && !AbstractDungeon.player.hasPower(NoDrawPower.POWER_ID)) {
            AbstractDungeon.player.getPower(EvolvePower.POWER_ID).flash();
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, AbstractDungeon.player.getPower(EvolvePower.POWER_ID).amount));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Treasure();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_GOLD);
        }
    }
}
