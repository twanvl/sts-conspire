package moremonsters.monsters;

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
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.WeakPower;

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
    private static final float HB_Y = -6.0f;
    private static final float HB_W = 260.0f;
    private static final float HB_H = 220.0f;
    // stats
    private static final int HP_MIN = 30;
    private static final int HP_MAX = 35;
    private static final int HP_MIN_A7 = HP_MIN + 5;
    private static final int HP_MAX_A7 = HP_MAX + 5;
    private static final int THORNS = 2;
    private static final int THORNS_A2 = 3;
    private static final int ATTACK_DMG = 4;
    private static final int ATTACK_DMG_A2 = 5;
    private static final int PRICK_DMG = 1;
    private static final int PRICK_DMG_A2 = 1;
    private static final int PRICK_WEAK = 1;
    private static final int PRICK_WEAK_A17 = 1;
    private static final int GROW_STRENGTH = 2;
    private static final int GROW_STRENGTH_A17 = 3;
    private int thornsAmt;
    private int attackDmg;
    private int prickDmg;
    private int prickWeak;
    private int growStrength;
    // moves
    private static final byte ATTACK = 1;
    private static final byte PRICK  = 2;
    private static final byte GROW   = 3;

    public RoseBush(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, "images/monsters/RoseBush.png", x, y);
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(HP_MIN_A7, HP_MAX_A7);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.thornsAmt    = AbstractDungeon.ascensionLevel >= 2 ? THORNS_A2 : THORNS;
        this.attackDmg    = AbstractDungeon.ascensionLevel >= 2 ? ATTACK_DMG_A2 : ATTACK_DMG;
        this.prickDmg     = AbstractDungeon.ascensionLevel >= 2 ? PRICK_DMG_A2 : PRICK_DMG;
        this.prickWeak    = AbstractDungeon.ascensionLevel >= 2 ? PRICK_WEAK_A17 : PRICK_WEAK;
        this.growStrength = AbstractDungeon.ascensionLevel >= 17 ? GROW_STRENGTH_A17 : GROW_STRENGTH;
        this.damage.add(new DamageInfo(this, attackDmg));
        this.damage.add(new DamageInfo(this, prickDmg));
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
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.prickWeak, true), prickWeak));
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