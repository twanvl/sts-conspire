package conspire.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import conspire.helpers.AscensionHelper;
import conspire.helpers.MovePicker;
import conspire.powers.CapriciousPower;

public class FuzzyDie extends AbstractMonster {
    public static final String ID = "conspire:FuzzyDie";
    public static final String ENCOUNTER_NAME = "conspire:FuzzyDice";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = 0.0f;
    private static final float HB_Y = 10.0f;
    private static final float HB_W = 210.0f;
    private static final float HB_H = 220.0f;
    // stats
    private static final int HP_MIN = 60;
    private static final int HP_MAX = 65;
    private static final int HP_MIN_A = HP_MIN + 5;
    private static final int HP_MAX_A = HP_MAX + 5;
    private static final int ATTACK_DMG_MIN = 7;
    private static final int ATTACK_DMG_MAX = 11;
    private static final int ATTACK_DMG_MIN_A = 9;
    private static final int ATTACK_DMG_MAX_A = 12;
    private static final int BIG_ATTACK_DMG = 20;
    private static final int BIG_ATTACK_DMG_A = 22;
    private static final int MULTI_ATTACK_DMG = 1;
    private static final int MULTI_ATTACK_TIMES = 6;
    private static final int MULTI_ATTACK_TIMES_A = 7;
    private static final int ATTACK_BLOCK_DMG = 8;
    private static final int ATTACK_BLOCK_DMG_A = ATTACK_BLOCK_DMG + 2;
    private static final int ATTACK_BLOCK_AMT = 7;
    private static final int ATTACK_BLOCK_AMT_A = ATTACK_BLOCK_AMT + 2;
    private static final int BLOCK_AMT = 13;
    private static final int BLOCK_AMT_A = BLOCK_AMT + 3;
    private static final int BUFF_STRENGTH = 3;
    private static final int BUFF_STRENGTH_A = 4;
    private static final int ATTACK_DEBUFF_DMG = 8;
    private static final int ATTACK_DEBUFF_DMG_A = ATTACK_BLOCK_DMG + 2;
    private int attackDmgMin;
    private int attackDmgMax;
    private int bigAttackDmg;
    private int multiAttackDmg;
    private int multiAttackTimes;
    private int blockAmt;
    private int attackBlockDmg;
    private int attackBlockAmt;
    private int buffStrength;
    private int attackDebuffAmt = 1;
    private int attackDebuffDmg = 1;
    private int debuffAmt = 2;
    private int woundAmt = 2;
    private boolean canApplyVulnerable;
    // moves
    private static final byte ATTACK_1      = 1;
    private static final byte ATTACK_2      = 2;
    private static final byte ATTACK_3      = 3;
    private static final byte BIG_ATTACK    = 4;
    private static final byte MULTI_ATTACK  = 5;
    private static final byte ATTACK_DEFEND = 6;
    private static final byte DEFEND        = 7;
    private static final byte BUFF          = 8;
    private static final byte DEBUFF        = 9;
    private static final byte ATTACK_DEBUFF = 10;
    private static final byte BIG_DEBUFF    = 11;
    private static final byte WOUND         = 12;

    public FuzzyDie(float x, float y, boolean canApplyVulnerable) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, null, x, y);
        this.loadAnimation("conspire/images/monsters/FuzzyDie/skeleton.atlas", "conspire/images/monsters/FuzzyDie/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.attackDmgMin     = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_MIN_A : ATTACK_DMG_MIN;
        this.attackDmgMax     = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_MAX_A : ATTACK_DMG_MAX;
        this.bigAttackDmg     = AscensionHelper.deadlier(this.type) ? BIG_ATTACK_DMG_A : BIG_ATTACK_DMG;
        this.multiAttackDmg   = MULTI_ATTACK_DMG;
        this.multiAttackTimes = AscensionHelper.deadlier(this.type) ? MULTI_ATTACK_TIMES_A : MULTI_ATTACK_TIMES;
        this.attackBlockDmg   = AscensionHelper.harder(this.type) ? ATTACK_BLOCK_DMG_A : ATTACK_BLOCK_DMG;
        this.attackDebuffDmg  = AscensionHelper.harder(this.type) ? ATTACK_DEBUFF_DMG_A : ATTACK_DEBUFF_DMG;
        this.blockAmt         = AscensionHelper.harder(this.type) ? BLOCK_AMT_A : BLOCK_AMT;
        this.attackBlockAmt   = AscensionHelper.harder(this.type) ? ATTACK_BLOCK_AMT_A : ATTACK_BLOCK_AMT;
        this.buffStrength     = AscensionHelper.harder(this.type) ? BUFF_STRENGTH_A : BUFF_STRENGTH;
        this.canApplyVulnerable = canApplyVulnerable;
        this.damage.add(new DamageInfo(this, attackDmgMin));
        this.damage.add(new DamageInfo(this, (attackDmgMin+attackDmgMax)/2));
        this.damage.add(new DamageInfo(this, attackDmgMax));
        this.damage.add(new DamageInfo(this, bigAttackDmg));
        this.damage.add(new DamageInfo(this, multiAttackDmg));
        this.damage.add(new DamageInfo(this, attackBlockDmg));
        this.damage.add(new DamageInfo(this, attackDebuffDmg));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CapriciousPower(this)));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case ATTACK_1: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case ATTACK_2: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case ATTACK_3: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case BIG_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AttackEffect.BLUNT_HEAVY));
                break;
            }
            case MULTI_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                for (int i = 0; i < this.multiAttackTimes; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(4), AttackEffect.BLUNT_LIGHT));
                }
                break;
            }
            case ATTACK_DEFEND: {
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, attackBlockAmt));
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(5), AttackEffect.BLUNT_HEAVY));
                break;
            }
            case DEFEND: {
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, blockAmt));
                break;
            }
            case BUFF: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.buffStrength), this.buffStrength));
                break;
            }
            case ATTACK_DEBUFF: {
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(6), AttackEffect.BLUNT_HEAVY));
                if (this.canApplyVulnerable) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.attackDebuffAmt, true), attackDebuffAmt));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.attackDebuffAmt, true), attackDebuffAmt));
                }
                break;
            }
            case DEBUFF: {
                if (this.canApplyVulnerable) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.debuffAmt, true), debuffAmt));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.debuffAmt, true), debuffAmt));
                }
                break;
            }
            case BIG_DEBUFF: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, this.debuffAmt, true), debuffAmt));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.debuffAmt, true), debuffAmt));
                break;
            }
            case WOUND: {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), this.woundAmt));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.lastMove(ATTACK_1))      moves.add(ATTACK_1,      Intent.ATTACK,        this.damage.get(0).base, 0.4f);
        if (!this.lastMove(ATTACK_2))      moves.add(ATTACK_2,      Intent.ATTACK,        this.damage.get(1).base, 0.4f);
        if (!this.lastMove(ATTACK_3))      moves.add(ATTACK_3,      Intent.ATTACK,        this.damage.get(2).base, 0.4f);
        if (!this.lastMove(BIG_ATTACK))    moves.add(BIG_ATTACK,    Intent.ATTACK,        this.damage.get(3).base, 1.0f);
        if (!this.lastMove(MULTI_ATTACK))  moves.add(MULTI_ATTACK,  Intent.ATTACK,        this.damage.get(4).base, multiAttackTimes, true, 1.0f);
        if (!this.lastMove(ATTACK_DEFEND)) moves.add(ATTACK_DEFEND, Intent.ATTACK_DEFEND, this.damage.get(5).base, 1.0f);
        if (!this.lastMove(DEFEND))        moves.add(DEFEND,        Intent.DEFEND,                                 1.0f);
        if (!this.lastMove(BUFF))          moves.add(BUFF,          Intent.BUFF,                                   1.0f);
        if (!this.lastMove(ATTACK_DEBUFF)) moves.add(ATTACK_DEBUFF, Intent.ATTACK_DEBUFF, this.damage.get(6).base, 1.0f);
        if (!this.lastMove(DEBUFF) && canApplyVulnerable)
                                           moves.add(DEBUFF,        Intent.DEBUFF,                                 1.0f);
        if (!this.lastMove(BIG_DEBUFF))    moves.add(BIG_DEBUFF,    Intent.STRONG_DEBUFF,                          1.0f);
        if (!this.lastMove(WOUND))         moves.add(MOVES[0], WOUND, Intent.DEBUFF,                               1.0f);
        moves.pickRandomMove(this);
    }
}