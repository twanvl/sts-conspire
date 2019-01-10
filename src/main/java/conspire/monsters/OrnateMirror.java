package conspire.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

import conspire.helpers.AscensionHelper;
import conspire.helpers.MovePicker;
import conspire.powers.ReflectAttackPower;
import conspire.powers.ReflectBlockPower;

public class OrnateMirror extends AbstractMonster {
    public static final String ID = "conspire:OrnateMirror";
    public static final String ENCOUNTER_NAME = ID;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = 0.0f;
    private static final float HB_Y = 0.0f;
    private static final float HB_W = 320.0f;
    private static final float HB_H = 360.0f;
    // stats
    private static final int HP = 450;
    private static final int HP_A = 465;
    private static final int ATTACK_DMG = 18;
    private static final int ATTACK_DMG_A = 21;
    private static final int ATTACK_2_DMG = 10;
    private static final int ATTACK_2_DMG_A = 12;
    private static final int ATTACK_2_TIMES = 2;
    private static final int BLOCK_AMT = 3;
    private static final int BLOCK_AMT_A = 4;
    private static final float REFLECT_AMT = 0.4f;
    private static final float REFLECT_AMT_A = 0.5f;
    private static final int COPY_THRESHOLD = 2;
    private static final int MAX_STR = 4;
    private int attackDmg, attackDmg2;
    private int blockAmt;
    private float reflectAmt = 2;
    // moves
    private static final byte REFLECT  = 1;
    private static final byte ATTACK_1 = 2;
    private static final byte ATTACK_2 = 3;
    private static final byte COPY_POWER = 4;
    // moves done
    private boolean doneReflect;
    // buffs/debuffs to copy
    private int copy_str = 0, copy_dex = 0, copy_weak = 0, copy_vuln = 0, copy_negstr = 0, copy_gainstr = 0;
    // buffs/debuffs to apply
    private int apply_str = 0, apply_dex = 0, apply_weak = 0, apply_vuln = 0, apply_negstr = 0, apply_gainstr = 0;

    public OrnateMirror() {
        this(-210.f,0.f);
    }

    public OrnateMirror(float x, float y) {
        super(NAME, ID, HP, HB_X, HB_Y, HB_W, HB_H, null, x, y);
        this.loadAnimation("conspire/images/monsters/OrnateMirror/skeleton.atlas", "conspire/images/monsters/OrnateMirror/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.type = EnemyType.BOSS;
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_A, HP_A);
        } else {
            this.setHp(HP, HP);
        }
        // damage amounts
        this.attackDmg  = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.attackDmg2  = AscensionHelper.deadlier(this.type) ? ATTACK_2_DMG_A : ATTACK_2_DMG;
        this.blockAmt   = AscensionHelper.tougher(this.type) ? BLOCK_AMT_A : BLOCK_AMT;
        this.reflectAmt = AscensionHelper.harder(this.type) ? REFLECT_AMT_A : REFLECT_AMT;
        this.damage.add(new DamageInfo(this, attackDmg));
        this.damage.add(new DamageInfo(this, attackDmg2));
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BEYOND");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ReflectAttackPower(this, reflectAmt)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ReflectBlockPower(this, reflectAmt)));
    }

    @Override
    public void takeTurn() {
        reflectPowersDuringTurn();
        switch (this.nextMove) {
            case REFLECT: {
                doneReflect = true;
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new MirrorImage(120.0f, 0.0f), true));
                break;
            }
            case ATTACK_1: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.FIRE));
                break;
            }
            case ATTACK_2: {
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < ATTACK_2_TIMES; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.FIRE));
                }
                break;
            }
            case COPY_POWER: {
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.SKY, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.5f));
                if (apply_str > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, apply_str), apply_str));
                }
                if (apply_dex > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new DexterityPower(this, apply_str), apply_str));
                }
                if (apply_weak > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, apply_weak, true), apply_weak));
                }
                if (apply_vuln > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, apply_vuln, true), apply_vuln));
                }
                if (apply_negstr > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new StrengthPower(AbstractDungeon.player, -apply_negstr), -apply_negstr));
                }
                if (apply_gainstr > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new GainStrengthPower(AbstractDungeon.player, apply_gainstr), apply_gainstr));
                }
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.blockAmt));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void reflectPowersDuringTurn() {
        // Call power.duringTurn() for ReflectAttackPower and ReflectBlockPower
        // Otherwise they happen after the attack, and are affected by thorns.
        for (AbstractPower p : this.powers) {
            if (p instanceof ReflectAttackPower) {
                // Note that p.duringTurn() resets the amount, so we don't have to worry about it being called twice.
                ((ReflectAttackPower)p).doReflectDuringTurn();
            } else if (p instanceof ReflectBlockPower) {
                ((ReflectBlockPower)p).doReflectDuringTurn();
            }
        }
    }

    // Called by patch
    public void onApplyPower(AbstractPower powerToApply, AbstractCreature target, AbstractCreature source) {
        // For reflecting powers
        if (source == null || !source.isPlayer) return;
        if (target == this && powerToApply instanceof StrengthPower && powerToApply.amount < 0) copy_negstr += -powerToApply.amount;
        if (target == this && powerToApply instanceof GainStrengthPower && powerToApply.amount > 0) copy_gainstr += powerToApply.amount;
        if (target == this && powerToApply instanceof WeakPower && powerToApply.amount > 0) copy_weak += powerToApply.amount;
        if (target == this && powerToApply instanceof VulnerablePower && powerToApply.amount > 0) copy_vuln += powerToApply.amount;
        if (target.isPlayer && powerToApply instanceof StrengthPower && powerToApply.amount > 0) copy_str += powerToApply.amount;
        if (target.isPlayer && powerToApply instanceof LoseStrengthPower && powerToApply.amount > 0) copy_str -= powerToApply.amount;
        if (target.isPlayer && powerToApply instanceof DexterityPower && powerToApply.amount > 0) copy_dex += powerToApply.amount;
        if (target.isPlayer && powerToApply instanceof LoseDexterityPower && powerToApply.amount > 0) copy_dex -= powerToApply.amount;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        // For registering poison in ReflectAttackPower
        for (AbstractPower p : this.powers) {
            if (p instanceof ReflectAttackPower) ((ReflectAttackPower)p).applyPowers();
        }
    }

    public void calculateCopyPowers() {
        apply_weak = copy_weak * 2 / 3;
        apply_vuln = copy_vuln * 2 / 3;
        apply_negstr = copy_negstr * 2 / 3;
        apply_gainstr = Math.min(apply_negstr, copy_gainstr * 2 / 3);
        apply_str = Math.max(0, Math.min(MAX_STR, copy_str * 2 / 3));
        apply_dex = Math.max(0, Math.min(MAX_STR, copy_dex * 2 / 3));
    }

    @Override
    protected void getMove(int num) {
        calculateCopyPowers();
        MovePicker moves = new MovePicker();
        if (!this.doneReflect) {
            moves.add(MOVES[0], REFLECT, Intent.UNKNOWN, 2.0f);
        }
        if (apply_weak + apply_vuln + apply_negstr + apply_str + apply_dex >= COPY_THRESHOLD && !this.lastMove(COPY_POWER)) {
            if (apply_weak + apply_vuln + apply_negstr > 0) {
                moves.add(MOVES[1], COPY_POWER, Intent.DEFEND_DEBUFF, 1.0f);
            } else {
                moves.add(MOVES[1], COPY_POWER, Intent.DEFEND_BUFF, 1.0f);
            }
        }
        if (!this.lastMove(ATTACK_1)) {
            moves.add(ATTACK_1, Intent.ATTACK, this.damage.get(0).base, 1.0f);
        }
        if (!this.lastMove(ATTACK_2)) {
            moves.add(ATTACK_2, Intent.ATTACK, this.damage.get(1).base, ATTACK_2_TIMES, true, 1.0f);
        }
        EnemyMoveInfo move = moves.pickRandomMove(this);
        if (move.nextMove == COPY_POWER) {
            copy_str = copy_dex = copy_weak = copy_vuln = copy_negstr = copy_gainstr = 0;
        }
    }

    @Override
    public void die() {
        super.die();
        this.useFastShakeAnimation(5.0f);
        CardCrawlGame.screenShake.rumble(4.0f);
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m.isDying || !(m instanceof MirrorImage)) continue;
            AbstractDungeon.actionManager.addToBottom(new SuicideAction(m));
        }
        this.onBossVictoryLogic();
        this.onFinalBossVictoryLogic();
    }
}