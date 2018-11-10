package conspire.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;

public class ReflectBlockPower extends AbstractConspirePower {
    public static final String POWER_ID = "conspire:ReflectBlock";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float fraction;

    public ReflectBlockPower(AbstractMonster owner, float fraction) {
        super(POWER_ID, NAME, owner);
        this.fraction = fraction;
        this.amount = 0;
        this.updateDescription();
    }

    // Note: onPlayerGainedBlock doesn't do what the name suggests, and it also triggers on monsters gaining block.
    // So we use a patch instead.
    public void onCreatureGainedBlock(AbstractCreature target, float blockAmt) {
        int block = Math.round(blockAmt * this.fraction);
        if (target.isPlayer && block > 0) {
            this.amount += block;
            this.flash();
            AbstractDungeon.effectList.add(new PowerBuffEffect(this.owner.hb.cX - this.owner.animX, this.owner.hb.cY + this.owner.hb.height / 2.0f, DESCRIPTIONS[4] + block + DESCRIPTIONS[5]));
            this.updateDescription();
        }
    }

    // Note: don't use duringTurn(), see OrnateMirror

    public void doReflectDuringTurn() {
        if (this.amount > 0) {
            this.flash();
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.owner.hb.cX, this.owner.hb.cY, AbstractGameAction.AttackEffect.SHIELD));
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner, this.owner, this.amount));
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
