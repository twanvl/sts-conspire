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
import conspire.actions.ReduceHolyAction;

public class InfernalBerry extends CustomCard {
    public static final String ID = "conspire:InfernalBerry";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;

    public InfernalBerry() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.ENEMY);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasRelic("Medical Kit")) {
            this.useMedicalKit(p);
        }
        AbstractDungeon.actionManager.addToBottom(new ReduceHolyAction(m, 1));
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
        return new InfernalBerry();
    }

    @Override
    public void upgrade() {
    }
}
