package moremonsters.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CapriciousPower extends AbstractPower {
    public static final String POWER_ID = "Capricious";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CapriciousPower(AbstractMonster owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.updateDescription();
        this.img = ImageMaster.loadImage("images/powers/32/"+POWER_ID+".png");
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
