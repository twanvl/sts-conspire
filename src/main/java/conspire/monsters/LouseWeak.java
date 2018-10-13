package conspire.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.CurlUpPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LouseWeak extends AbstractMonster {
    public static final String ID = "FuzzyLouseWeak";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FuzzyLouseNormal");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 10;
    private static final int HP_MAX = 15;
    private static final int A_2_HP_MIN = 11;
    private static final int A_2_HP_MAX = 16;
    private static final byte BITE = 3;
    private static final byte STRENGTHEN = 4;
    private boolean isOpen = true;
    private static final String CLOSED_STATE = "CLOSED";
    private static final String OPEN_STATE = "OPEN";
    private static final String REAR_IDLE = "REAR_IDLE";
    private int biteDamage;
    private static final int STR_AMOUNT = 2;
    private static final int BLOCK_AMOUNT = 2;

    public LouseWeak(float x, float y) {
        super(NAME, ID, 17, 0.0f, -5.0f, 180.0f, 140.0f, null, x, y);
        this.loadAnimation("conspire/images/monsters/LouseWeak/skeleton.atlas", "images/monsters/theBottom/louseGreen/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(HP_MIN, HP_MAX);
        } else {
            this.setHp(A_2_HP_MIN, A_2_HP_MAX);
        }
        this.biteDamage = AbstractDungeon.ascensionLevel >= 2 ? AbstractDungeon.monsterHpRng.random(6, 8) : AbstractDungeon.monsterHpRng.random(5, 7);
        this.damage.add(new DamageInfo(this, this.biteDamage));
    }

    @Override
    public void usePreBattleAction() {
        if (AbstractDungeon.ascensionLevel >= 17) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CurlUpPower(this, AbstractDungeon.monsterHpRng.random(9, 12))));
        } else if (AbstractDungeon.ascensionLevel >= 7) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CurlUpPower(this, AbstractDungeon.monsterHpRng.random(4, 8))));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CurlUpPower(this, AbstractDungeon.monsterHpRng.random(3, 7))));
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case BITE: {
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, OPEN_STATE));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5f));
                }
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction((AbstractCreature)AbstractDungeon.player, (DamageInfo)this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            }
            case STRENGTHEN: {
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REAR"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2f));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, REAR_IDLE));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.9f));
                }
                if (AbstractDungeon.ascensionLevel >= 17) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, STR_AMOUNT+1), STR_AMOUNT+1));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, STR_AMOUNT), STR_AMOUNT));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, BLOCK_AMOUNT));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    public void changeState(String stateName) {
        if (stateName.equals(CLOSED_STATE)) {
            this.state.setAnimation(0, "transitiontoclosed", false);
            this.state.addAnimation(0, "idle closed", true, 0.0f);
            this.isOpen = false;
        } else if (stateName.equals(OPEN_STATE)) {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "idle", true, 0.0f);
            this.isOpen = true;
        } else if (stateName.equals(REAR_IDLE)) {
            this.state.setAnimation(0, "rear", false);
            this.state.addAnimation(0, "idle", true, 0.0f);
            this.isOpen = true;
        } else {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "rear", false, 0.0f);
            this.state.addAnimation(0, "idle", true, 0.0f);
            this.isOpen = true;
        }
    }

    @Override
    protected void getMove(int num) {
        if (AbstractDungeon.ascensionLevel >= 17) {
            if (num < 25) {
                if (this.lastMove(STRENGTHEN)) {
                    this.setMove(BITE, Intent.ATTACK, ((DamageInfo)this.damage.get((int)0)).base);
                } else {
                    this.setMove(MOVES[0], STRENGTHEN, Intent.DEFEND_BUFF);
                }
            } else if (this.lastTwoMoves(BITE)) {
                this.setMove(MOVES[0], STRENGTHEN, Intent.DEFEND_BUFF);
            } else {
                this.setMove(BITE, Intent.ATTACK, ((DamageInfo)this.damage.get((int)0)).base);
            }
        } else if (num < 25) {
            if (this.lastTwoMoves(STRENGTHEN)) {
                this.setMove(BITE, Intent.ATTACK, ((DamageInfo)this.damage.get((int)0)).base);
            } else {
                this.setMove(MOVES[0], STRENGTHEN, Intent.DEFEND_BUFF);
            }
        } else if (this.lastTwoMoves(BITE)) {
            this.setMove(MOVES[0], STRENGTHEN, Intent.DEFEND_BUFF);
        } else {
            this.setMove(BITE, Intent.ATTACK, ((DamageInfo)this.damage.get((int)0)).base);
        }
    }
}

