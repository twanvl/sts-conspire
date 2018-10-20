package conspire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.PowerDebuffEffect;

import conspire.monsters.HollyBat;
import conspire.powers.HolyPower;

public class ReduceHolyAction extends AbstractGameAction {
    private float startingDuration;

    public ReduceHolyAction(AbstractCreature target, int amount) {
        this.startingDuration = Settings.FAST_MODE ? 0.1f : Settings.ACTION_DUR_FAST;
        this.setValues(target, source, amount);
        this.duration = this.startingDuration;
        this.actionType = AbstractGameAction.ActionType.POWER;
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.duration = 0.0f;
            this.startingDuration = 0.0f;
            this.isDone = true;
        }
    }

    @Override
    public void update() {
        if (this.shouldCancelAction()) {
            this.isDone = true;
            return;
        }
        if (this.duration == this.startingDuration) {
            if (!this.target.hasPower(HolyPower.POWER_ID)) {
                this.isDone = true;
                return;
            }
            if (this.target instanceof AbstractMonster && this.target.isDeadOrEscaped()) {
                this.duration = 0.0f;
                this.isDone = true;
                return;
            }
            AbstractPower power = this.target.getPower(HolyPower.POWER_ID);
            power.stackPower(-1);
            power.flash();
            if (power.amount > 0) {
                AbstractDungeon.effectList.add(new PowerDebuffEffect(this.target.hb.cX - this.target.animX, this.target.hb.cY + this.target.hb.height / 2.0f, "-" + Integer.toString(this.amount) + " " + power.name));
                power.updateDescription();
            } else {
                AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.target, this.target, HolyPower.POWER_ID));
            }
            if (this.target instanceof HollyBat) {
                ((HollyBat)this.target).onDecreaseHoly(power.amount);
            }
            AbstractDungeon.onModifyPower();
        }
        this.tickDuration();
    }
}

