package conspire.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.defect.DecreaseMaxOrbAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

import conspire.cards.status.PyramidRune;
import conspire.helpers.AscensionHelper;
import conspire.helpers.MovePicker;
import conspire.powers.CubeRunePower;
import conspire.powers.DodecahedronRunePower;
import conspire.powers.DomeRunePower;

public class MysteriousRune extends AbstractMonster {
    public static final String ID = "conspire:MysteriousRune";
    public static final String ENCOUNTER_NAME = "conspire:MysteriousRune";
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
    private static final int ATTACK_2_DMG = 6;
    private static final int ATTACK_2_DMG_A = ATTACK_DMG + 2;
    private static final int ATTACK_2_TIMES = 2;
    private static final int DEBUFF_DMG = 9;
    private static final int DEBUFF_DMG_A = DEBUFF_DMG + 2;
    private static final int PYRAMID_AMT = 3;
    private static final int PYRAMID_AMT_A = 4;
    private static final int CAPACITOR_AMT = 2;
    private static final int CAPACITOR_AMT_A = 2;
    private static final int DODECAHEDRON_COUNT = 12;
    private static final int DODECAHEDRON_HEAL = 20;
    private static final int DODECAHEDRON_HEAL_A = 20;
    private static final int DODECAHEDRON_STRENGTH = 3;
    private static final int DODECAHEDRON_STRENGTH_A = 4;
    private static final int ARTIFACT_AMT = 2;
    private int attackDmg;
    private int attack2Dmg;
    private int debuffAttackDmg;
    private int pyramidAmt;
    private int capacitorAmt;
    private int dodecahedronHeal;
    private int dodecahedronStrength;
    private int dodecahedronBlock = 12;
    // moves
    private static final byte ATTACK       = 1;
    private static final byte ATTACK_2     = 2;
    private static final byte DOME         = 3;
    private static final byte CUBE         = 4;
    private static final byte PYRAMID      = 5;
    private static final byte CAPACITOR    = 6;
    private static final byte DODECAHEDRON = 7;
    /*
    sequence: {any debuf}, attack
    */
    private boolean doneDome = false;
    private boolean doneCube = false;
    private boolean doneCapacitor = false;

    public MysteriousRune(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, "conspire/images/monsters/MysteriousRune.png", x, y);
        this.loadAnimation("conspire/images/monsters/MysteriousRune/skeleton.atlas", "conspire/images/monsters/MysteriousRune/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Idle", "Hit", 0.1f);
        this.stateData.setMix("Idle", "Glow", 0.2f);
        this.stateData.setMix("Idle", "Dark", 0.2f);
        this.stateData.setMix("Hit", "Idle", 0.5f);
        this.stateData.setMix("Glow", "Idle", 0.5f);
        this.stateData.setMix("Dark", "Idle", 0.5f);
        this.stateData.setMix("Buff", "Idle", 0.5f);
        this.type = EnemyType.ELITE;
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.attackDmg = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.attack2Dmg = AscensionHelper.deadlier(this.type) ? ATTACK_2_DMG_A : ATTACK_2_DMG;
        this.debuffAttackDmg = AscensionHelper.deadlier(this.type) ? DEBUFF_DMG_A : DEBUFF_DMG;
        this.pyramidAmt = AscensionHelper.harder(this.type) ? PYRAMID_AMT_A : PYRAMID_AMT;
        this.capacitorAmt = AscensionHelper.harder(this.type) ? CAPACITOR_AMT_A : CAPACITOR_AMT;
        this.dodecahedronStrength = AscensionHelper.harder(this.type) ? DODECAHEDRON_STRENGTH_A : DODECAHEDRON_STRENGTH;
        this.dodecahedronHeal = AscensionHelper.harder(this.type) ? DODECAHEDRON_HEAL_A : DODECAHEDRON_HEAL;
        this.damage.add(new DamageInfo(this, attackDmg));
        this.damage.add(new DamageInfo(this, attack2Dmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
        this.damage.add(new DamageInfo(this, debuffAttackDmg));
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, ARTIFACT_AMT)));
    }

    @Override
    public void changeState(String stateName) {
        switch (stateName) {
            case "ROTATE": {
                this.state.setAnimation(0, "Rotate", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
                break;
            }
            case "GLOW": {
                this.state.setAnimation(0, "Glow", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
                break;
            }
            case "DARK": {
                this.state.setAnimation(0, "Dark", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
                break;
            }
            case "BUFF": {
                this.state.setAnimation(0, "Buff", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
                break;
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
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ROTATE"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_HEAVY));
                break;
            }
            case ATTACK_2: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                for (int i = 0 ; i < ATTACK_2_TIMES ; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.BLUNT_LIGHT));
                }
                break;
            }
            case DOME: {
                doneDome = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DARK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DomeRunePower(AbstractDungeon.player)));
                break;
            }
            case CUBE: {
                doneCube = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DARK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new CubeRunePower(AbstractDungeon.player, this, 1), 1));
                break;
            }
            case PYRAMID: {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "GLOW"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(4), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new PyramidRune(), this.pyramidAmt, true, false));
                break;
            }
            case CAPACITOR: {
                doneCapacitor = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DARK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(5), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new DecreaseMaxOrbAction(this.capacitorAmt));
                break;
            }
            case DODECAHEDRON: {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "BUFF"));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, dodecahedronBlock));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new DodecahedronRunePower(this, DODECAHEDRON_COUNT, dodecahedronHeal, dodecahedronStrength)));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.lastMove(ATTACK) && !this.moveHistory.isEmpty()) {
            moves.add(MOVES[0], ATTACK, Intent.ATTACK, this.damage.get(0).base, 1.2f);
        }
        if (!this.lastMove(ATTACK_2) && !this.moveHistory.isEmpty()) {
            moves.add(MOVES[1], ATTACK_2, Intent.ATTACK, this.damage.get(1).base, ATTACK_2_TIMES, true, 0.8f);
        }
        if (this.lastMove(ATTACK) || this.lastMove(ATTACK_2) || this.moveHistory.isEmpty()) {
            if (!doneDome) moves.add(MOVES[2], DOME, Intent.ATTACK_DEBUFF, this.damage.get(2).base, 0.5f);
            if (!doneCube) moves.add(MOVES[3], CUBE, Intent.ATTACK_DEBUFF, this.damage.get(3).base, 1.0f);
            if (!this.lastMove(PYRAMID)) moves.add(MOVES[4], PYRAMID, Intent.ATTACK_DEBUFF, this.damage.get(4).base, 1.0f);
            if (!doneCapacitor && AbstractDungeon.player.hasOrb()) moves.add(MOVES[5], CAPACITOR, Intent.ATTACK_DEBUFF, this.damage.get(5).base, 1.0f);
            if (!this.hasPower(DodecahedronRunePower.POWER_ID)) moves.add(MOVES[6], DODECAHEDRON, Intent.BUFF, 2.0f);
        }
        moves.pickRandomMove(this);
    }

    @Override
    public void die() {
        this.useFastShakeAnimation(5.0f);
        CardCrawlGame.screenShake.rumble(4.0f);
        this.deathTimer += 1.5f;
        super.die();
        this.onBossVictoryLogic();
    }
}
