package conspire.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import conspire.cards.status.InfernalBerry;
import conspire.helpers.AscensionHelper;
import conspire.helpers.MovePicker;
import conspire.powers.HolyPower;

public class HollyBat extends AbstractMonster {
    public static final String ID = "conspire:HollyBat";
    public static final String ENCOUNTER_NAME = ID;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = 0.0f;
    private static final float HB_Y = -15.0f;
    private static final float HB_W = 320.0f;
    private static final float HB_H = 240.0f;
    // stats
    private static final int HP_MIN = 100;
    private static final int HP_MAX = 105;
    private static final int HP_MIN_A = HP_MIN + 5;
    private static final int HP_MAX_A = HP_MAX + 5;
    private static final int HOLY_AMT = 3;
    private static final int HOLY_AMT_A = 4;
    private static final int ATTACK_DMG = 13;
    private static final int ATTACK_DMG_A = 16;
    private static final int BITE_DMG = 7;
    private static final int BITE_DMG_A = 8;
    private static final int ANGRY_DMG = 18;
    private static final int ANGRY_DMG_A = 21;
    private int attackDmg, biteDmg, angryDmg;
    private int holyAmt;
    private int strengthAmt = 2;
    private int weakAmt = 2;
    private int woundAmt = 1;
    // moves
    private static final byte PELT     = 1;
    private static final byte SWOOP    = 2;
    private static final byte BITE     = 3;
    private static final byte INFECT   = 4;
    private static final byte ANGRY    = 5;
    private static final byte GLOW     = 6;
    private boolean donePelt = false;
    private boolean doneAngry = false;
    private boolean isHoly = true;

    public HollyBat(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, null, x, y + 150.0f);
        this.loadAnimation("conspire/images/monsters/HollyBat/skeleton.atlas", "conspire/images/monsters/HollyBat/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "IdleHoly", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.holyAmt   = AscensionHelper.harder(this.type) ? HOLY_AMT_A : HOLY_AMT;
        this.attackDmg = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.biteDmg = AscensionHelper.deadlier(this.type) ? BITE_DMG_A : BITE_DMG;
        this.angryDmg = AscensionHelper.deadlier(this.type) ? ANGRY_DMG_A : ANGRY_DMG;
        this.woundAmt = AscensionHelper.deadlier(this.type) ? 2 : 1;
        this.damage.add(new DamageInfo(this, attackDmg));
        this.damage.add(new DamageInfo(this, biteDmg));
        this.damage.add(new DamageInfo(this, angryDmg));
        // called holy until no longer holy
        this.name = DIALOG[0];
        this.isHoly = true;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new HolyPower(this, this.holyAmt), this.holyAmt));
    }

    public void onDecreaseHoly(int amount) {
        if (amount == 0) {
            this.name = NAME;
            AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "UNHOLY"));
        }
    }

    @Override
    public void changeState(String stateName) {
        switch (stateName) {
            case "SWOOP": {
                this.state.setAnimation(0, isHoly ? "SwoopHoly" : "Swoop", false);
                this.state.addAnimation(0, isHoly ? "IdleHoly" : "Idle", true, 0.0f);
                break;
            }
            case "UNHOLY": {
                isHoly = false;
                this.state.setAnimation(0, "IdleFromHoly", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
            }
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case PELT: {
                donePelt = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "PELT"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2f));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new InfernalBerry(), this.holyAmt));
                break;
            }
            case SWOOP: {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "SWOOP"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case BITE: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), woundAmt));
                break;
            }
            case INFECT: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.weakAmt, true), this.weakAmt));
                break;
            }
            case ANGRY: {
                doneAngry = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "SWOOP"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case GLOW: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strengthAmt), this.strengthAmt));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.donePelt) {
            this.setMove(MOVES[0], PELT, AbstractMonster.Intent.DEBUFF);
            return;
        }
        if (this.hasPower(HolyPower.POWER_ID)) {
            if (!this.lastMove(GLOW) && !this.lastMove(PELT)) moves.add(MOVES[5], GLOW, Intent.BUFF, 1.0f);
        } else {
            if (!this.doneAngry) {
                this.setMove(MOVES[4], ANGRY, AbstractMonster.Intent.ATTACK, this.damage.get(2).base);
                return;
            }
        }
        if (!this.lastMove(SWOOP)) moves.add(MOVES[1], SWOOP, Intent.ATTACK, this.damage.get(0).base, 1.0f);
        if (!this.lastMove(BITE)) moves.add(MOVES[2], BITE, Intent.ATTACK_DEBUFF, this.damage.get(1).base, 1.0f);
        if (!this.lastMove(INFECT)) moves.add(MOVES[3], INFECT, Intent.DEBUFF, 1.0f);
        moves.pickRandomMove(this);
    }
}