package moremonsters.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import moremonsters.helpers.AscensionHelper;
import moremonsters.helpers.MovePicker;

public class RoseBush extends AbstractMonster {
    public static final String ID = "RoseBush";
    public static final String ENCOUNTER_NAME = "RoseBush";
    public static final String DOUBLE_ENCOUNTER_NAME = "2 RoseBushes";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = 0.0f;
    private static final float HB_Y = 0.0f;
    private static final float HB_W = 270.0f;
    private static final float HB_H = 240.0f;
    // stats
    private static final int HP_MIN = 30;
    private static final int HP_MAX = 35;
    private static final int HP_MIN_A = HP_MIN + 5;
    private static final int HP_MAX_A = HP_MAX + 5;
    private static final int THORNS = 2;
    private static final int THORNS_A = 3;
    private static final int ATTACK_DMG = 4;
    private static final int ATTACK_DMG_A = 5;
    private static final int PRICK_DMG = 1;
    private static final int PRICK_DMG_A = 1;
    private static final int PRICK_WEAK = 1;
    private static final int PRICK_WEAK_A = 2;
    private static final int GROW_STRENGTH = 2;
    private static final int GROW_STRENGTH_A = 3;
    private int thornsAmt;
    private int attackDmg;
    private int prickDmg;
    private int prickWeak;
    private int growStrength;
    private RoseColor color;
    // moves
    private static final byte ATTACK = 1;
    private static final byte PRICK  = 2;
    private static final byte GROW   = 3;

    enum RoseColor {
        RED, WHITE, YELLOW
    }

    public RoseBush(float x, float y) {
        this(x,y,randomColor());
    }

    public RoseBush(float x, float y, RoseColor color) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, null, x, y);
        this.color = color;
        this.loadAnimation(atlasFile(color), "images/monsters/RoseBush/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.thornsAmt    = AscensionHelper.deadlier(this.type) ? THORNS_A : THORNS;
        this.attackDmg    = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.prickDmg     = AscensionHelper.deadlier(this.type) ? PRICK_DMG_A : PRICK_DMG;
        this.prickWeak    = AscensionHelper.harder(this.type) ? PRICK_WEAK_A : PRICK_WEAK;
        this.growStrength = AscensionHelper.harder(this.type) ? GROW_STRENGTH_A : GROW_STRENGTH;
        this.damage.add(new DamageInfo(this, attackDmg));
        this.damage.add(new DamageInfo(this, prickDmg));
    }

    private static RoseColor randomColor() {
        int i = AbstractDungeon.monsterHpRng.random(2);
        if (i == 0)      return RoseColor.RED;
        else if (i == 1) return RoseColor.WHITE;
        else             return RoseColor.YELLOW;
    }

    private static String atlasFile(RoseColor c) {
        switch (c) {
            case RED:    return "images/monsters/RoseBush/skeletonR.atlas";
            case WHITE:  return "images/monsters/RoseBush/skeletonW.atlas";
            case YELLOW: return "images/monsters/RoseBush/skeletonY.atlas";
        }
        return "";
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThornsPower(this, this.thornsAmt)));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_LIGHT));
                break;
            }
            case PRICK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.BLUNT_LIGHT));
                switch (this.color) {
                    case RED: {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.prickWeak, true), prickWeak));
                        break;
                    }
                    case WHITE: {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.prickWeak, true), prickWeak));
                        break;
                    }
                    case YELLOW: {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, this.prickWeak, true), prickWeak));
                        break;
                    }
                }
                break;
            }
            case GROW: {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.growStrength), this.growStrength));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.lastTwoMoves(ATTACK)) moves.add(          ATTACK, Intent.ATTACK,        this.damage.get(0).base, 2.0f);
        if (!this.lastMove(PRICK))      moves.add(MOVES[0], PRICK,  Intent.ATTACK_DEBUFF, this.damage.get(1).base, 2.0f);
        if (!this.lastMove(GROW))       moves.add(MOVES[1], GROW,   Intent.BUFF,                                   1.0f);
        moves.pickRandomMove(this);
    }
}