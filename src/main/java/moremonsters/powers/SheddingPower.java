package moremonsters.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import moremonsters.monsters.HeadLouse;

public class SheddingPower extends AbstractPower {
    public static final String POWER_ID = "Shedding";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SheddingPower(AbstractMonster owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.updateDescription();
        this.img = ImageMaster.loadImage("images/powers/32/"+POWER_ID+".png");
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        HeadLouse headLouse = (HeadLouse)this.owner;
        if (this.amount > 0 && damageAmount < this.owner.currentHealth && damageAmount > 0 && info.owner != null && info.type == DamageInfo.DamageType.NORMAL && headLouse.numAliveMinions() < HeadLouse.MAX_LICE) {
            this.amount = 0;
            this.flash();
            AbstractDungeon.actionManager.addToBottom(headLouse.spawnLouseInFreeSlotAction(false));
        }
        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        this.amount = 1;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
