package conspire.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CapriciousPower extends AbstractConspirePower {
    public static final String POWER_ID = "conspire:Capricious";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CapriciousPower(AbstractMonster owner) {
        super(POWER_ID, NAME, owner);
        this.updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        //AbstractDungeon.actionManager.addToBottom(new RollMoveAction((AbstractMonster)this.owner));
        // Note: RollMoveAction doesn't update intent
        AbstractMonster m = (AbstractMonster)this.owner;
        m.rollMove();
        m.createIntent();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
