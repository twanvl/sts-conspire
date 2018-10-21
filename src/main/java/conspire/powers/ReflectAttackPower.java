package conspire.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;

public class ReflectAttackPower extends AbstractConspirePower {
    public static final String POWER_ID = "conspire:ReflectAttack";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float fraction;

    public ReflectAttackPower(AbstractMonster owner, float fraction) {
        super(POWER_ID, NAME, owner);
        this.fraction = fraction;
        this.amount = 0;
        this.updateDescription();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        int damage = Math.round(damageAmount * this.fraction);
        if (damage > 0) {
            this.amount += damage;
            this.flash();
            AbstractDungeon.effectList.add(new PowerBuffEffect(this.owner.hb.cX - this.owner.animX, this.owner.hb.cY + this.owner.hb.height / 2.0f, DESCRIPTIONS[4] + damage + DESCRIPTIONS[5]));
            this.updateDescription();
        }
        return super.onAttacked(info, damageAmount);
    }

    @Override
    public void atStartOfTurn() {
        if (this.amount > 0) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AttackEffect.BLUNT_LIGHT));
            this.amount = 0;
            this.updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + Math.round(fraction * 100) + DESCRIPTIONS[1];
        if (this.amount > 0) {
            this.description += DESCRIPTIONS[2] + amount + DESCRIPTIONS[3];
        }
    }
}
