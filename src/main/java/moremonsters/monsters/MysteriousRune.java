package moremonsters.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.defect.DecreaseMaxOrbAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import moremonsters.cards.PyramidRune;
import moremonsters.helpers.AscensionHelper;
import moremonsters.helpers.MovePicker;
import moremonsters.powers.CubeRunePower;
import moremonsters.powers.DomeRunePower;

public class MysteriousRune extends AbstractMonster {
    public static final String ID = "MysteriousRune";
    public static final String ENCOUNTER_NAME = "MysteriousRune";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = 0.0f;
    private static final float HB_Y = 0.0f;
    private static final float HB_W = 260.0f;
    private static final float HB_H = 430.0f;
    // stats
    private static final int HP_MIN = 300;
    private static final int HP_MAX = 305;
    private static final int HP_MIN_A = HP_MIN + 25;
    private static final int HP_MAX_A = HP_MAX + 25;
    private static final int ATTACK_DMG = 15;
    private static final int ATTACK_DMG_A = ATTACK_DMG + 3;
    private static final int DEBUFF_DMG = 5;
    private static final int DEBUFF_DMG_A = DEBUFF_DMG + 2;
    private static final int PYRAMID_AMT = 3;
    private static final int PYRAMID_AMT_A = 4;
    private static final int CAPACITOR_AMT = 2;
    private static final int CAPACITOR_AMT_A = 2;
    private static final int ARTIFACT_AMT = 2;
    private int attackDmg;
    private int debuffAttackDmg;
    private int pyramidAmt;
    private int capacitorAmt;
    // moves
    private static final byte ATTACK    = 1;
    private static final byte DOME      = 2;
    private static final byte CUBE      = 3;
    private static final byte PYRAMID   = 4;
    private static final byte CAPACITOR = 5;
    /*
    sequence: {any debuf}, attack
    */
    private boolean doneDome = false;
    private boolean doneCube = false;
    private boolean doneCapacitor = false;

    public MysteriousRune(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, "images/monsters/MysteriousRune.png", x, y);
        this.loadAnimation("images/monsters/MysteriousRune/skeleton.atlas", "images/monsters/MysteriousRune/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Idle", "Hit", 0.1f);
        this.stateData.setMix("Idle", "Glow", 0.2f);
        this.stateData.setMix("Idle", "Dark", 0.2f);
        this.stateData.setMix("Hit", "Idle", 0.5f);
        this.stateData.setMix("Glow", "Idle", 0.5f);
        this.stateData.setMix("Dark", "Idle", 0.5f);
        this.type = EnemyType.ELITE;
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.attackDmg = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.debuffAttackDmg = AscensionHelper.deadlier(this.type) ? DEBUFF_DMG_A : DEBUFF_DMG;
        this.pyramidAmt = AscensionHelper.harder(this.type) ? PYRAMID_AMT_A : PYRAMID_AMT;
        this.capacitorAmt = AscensionHelper.harder(this.type) ? CAPACITOR_AMT_A : CAPACITOR_AMT;
        this.damage.add(new DamageInfo(this, attackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, ARTIFACT_AMT)));
    }

    @Override
    public void changeState(String stateName) {
        switch (stateName) {
            case "GLOW": {
                this.state.setAnimation(0, "Glow", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
            }
            case "DARK": {
                this.state.setAnimation(0, "Dark", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0f);
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case DOME: {
                doneDome = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DARK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DomeRunePower(AbstractDungeon.player)));
                break;
            }
            case CUBE: {
                doneCube = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DARK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new CubeRunePower(AbstractDungeon.player, this, 1), 1));
                break;
            }
            case PYRAMID: {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "GLOW"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new PyramidRune(), this.pyramidAmt, true, false));
                break;
            }
            case CAPACITOR: {
                doneCapacitor = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DARK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(4), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new DecreaseMaxOrbAction(this.capacitorAmt));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.lastTwoMoves(ATTACK) && !this.moveHistory.isEmpty()) {
            moves.add(ATTACK, Intent.ATTACK, this.damage.get(0).base, 1.0f);
        }
        if (this.lastMove(ATTACK) || this.moveHistory.isEmpty()) {
            if (!doneDome) moves.add(MOVES[0], DOME, Intent.ATTACK_DEBUFF, this.damage.get(1).base, 1.0f);
            if (!doneCube) moves.add(MOVES[1], CUBE, Intent.ATTACK_DEBUFF, this.damage.get(2).base, 1.0f);
            if (!this.lastMove(PYRAMID)) moves.add(MOVES[2], PYRAMID, Intent.ATTACK_DEBUFF, this.damage.get(3).base, 1.0f);
            if (!doneCapacitor && AbstractDungeon.player.hasOrb()) moves.add(MOVES[3], CAPACITOR, Intent.ATTACK_DEBUFF, this.damage.get(4).base, 1.0f);
        }
        moves.pickRandomMove(this);
    }
}
