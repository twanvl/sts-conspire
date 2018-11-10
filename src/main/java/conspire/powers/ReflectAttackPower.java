package conspire.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;

public class ReflectAttackPower extends AbstractConspirePower {
    public static final String POWER_ID = "conspire:ReflectAttack";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float fraction;
    // some part of this.amount is an estimate based on Poison
    // during the player turn, include that estimate in this.amount
    private boolean isDuringTurn;
    private int amountEstimated;
    private int amountEstimatedTaken;

    public ReflectAttackPower(AbstractMonster owner, float fraction) {
        super(POWER_ID, NAME, owner);
        this.fraction = fraction;
        this.amount = 0;
        this.amountEstimated = 0;
        this.amountEstimatedTaken = 0;
        this.isDuringTurn = false;
        this.updateDescription();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        int damage = Math.round(damageAmount * this.fraction);
        if (damage > 0) {
            this.amount += damage;
            this.flash();
            AbstractDungeon.effectList.add(new PowerBuffEffect(this.owner.hb.cX - this.owner.animX, this.owner.hb.cY + this.owner.hb.height / 2.0f, DESCRIPTIONS[7] + damage + DESCRIPTIONS[8]));
            this.updateDescription();
        }
        return super.onAttacked(info, damageAmount);
    }

    public void applyPowers() {
        if (!this.isDuringTurn) {
            this.updateDamageEstimate();
            this.updateDescription();
        }
    }

    @Override
    public void atStartOfTurn() {
        this.isDuringTurn = true;
        this.amount -= this.amountEstimated;
        this.amountEstimated = 0;
        this.amountEstimatedTaken = 0;
    }

    private void updateDamageEstimate() {
        this.amount -= this.amountEstimated;
        this.amountEstimated = 0;
        this.amountEstimatedTaken = 0;
        if (!this.isDuringTurn) {
            for (AbstractPower p : this.owner.powers) {
                if (p.ID.equals(PoisonPower.NAME)) {
                    this.amountEstimated += Math.round(p.amount * this.fraction);
                    this.amountEstimatedTaken += p.amount;
                } else if (p.ID.equals("Necrotic Poison")) { // from Replay the Spire
                    this.amountEstimated += Math.round(2 * p.amount * this.fraction);
                    this.amountEstimatedTaken += 2 * p.amount;
                }
                // TODO: MadScienceMod.BombPower ?
            }
            // TODO: Also include the player's Orbs, TheBombPower?
            this.amount += this.amountEstimated;
        }
    }

    // Note: don't use duringTurn(), see OrnateMirror

    public void doReflectDuringTurn() {
        if (this.amount > 0) {
            this.flash();
            int actualAmount = this.amount - this.amountEstimated;
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, new DamageInfo(this.owner, actualAmount, DamageInfo.DamageType.THORNS), AttackEffect.BLUNT_LIGHT));
            this.amount = 0;
            this.amountEstimated = 0;
            this.isDuringTurn = false;
            this.updateDamageEstimate();
            this.updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + Math.round(fraction * 100) + DESCRIPTIONS[1];
        if (this.amount > 0) {
            if (this.amountEstimated > 0) {
                this.description += DESCRIPTIONS[4] + amount + DESCRIPTIONS[5] + amountEstimatedTaken + DESCRIPTIONS[6];
            } else {
                this.description += DESCRIPTIONS[2] + amount + DESCRIPTIONS[3];
            }
        }
    }
}
