package conspire.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class PoisonWeaponsPower extends AbstractConspirePower {
    public static final String POWER_ID = "conspire:PoisonWeapons";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PoisonWeaponsPower(AbstractCreature owner) {
        super(POWER_ID, NAME, owner);
        this.amount = 1;
        this.updateDescription();
        this.isTurnBased = true;
        this.priority = 99;
    }

    // Note: called by patch
    public int onAttackToPoison(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, this.owner, new PoisonPower(target, this.owner, damageAmount), damageAmount, true));
            return 0;
        }
        return damageAmount;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (this.amount == 0) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + (this.amount == 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[2]);
    }
}

