package moremonsters.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.LouseDefensive;
import com.megacrit.cardcrawl.monsters.exordium.LouseNormal;
import com.megacrit.cardcrawl.powers.CurlUpPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

import moremonsters.actions.SpawnLouseAction;
import moremonsters.helpers.AscensionHelper;
import moremonsters.helpers.MovePicker;
import moremonsters.powers.SheddingPower;

public class HeadLouse extends AbstractMonster {
    public static final String ID = "HeadLouse";
    public static final String ENCOUNTER_NAME = "Head Lice";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = 0.0f;
    private static final float HB_Y = 0.0f;
    private static final float HB_W = 290.0f;
    private static final float HB_H = 248.0f;
    // stats
    private static final int HP_MIN = 110;
    private static final int HP_MAX = 115;
    private static final int HP_MIN_A = HP_MIN + 10;
    private static final int HP_MAX_A = HP_MAX + 10;
    private static final int MIN_LICE     = 1;
    public  static final int MAX_LICE     = 4;
    private static final int START_LICE   = 2;
    private static final int START_LICE_A = 3;
    private static final int SUMMON_LICE   = 1;
    private static final int CURL_UP_AMT   = 15;
    private static final int CURL_UP_AMT_A = 18;
    private static final int ATTACK_DMG   = 10;
    private static final int ATTACK_DMG_A = 12;
    private static final int BUFF_STRENGTH   = 3;
    private static final int BUFF_STRENGTH_A = 4;
    private static final int MINION_BUFF_STRENGTH   = 1;
    private static final int MINION_BUFF_STRENGTH_A = 2;
    private static final int BLOCK_AMT   = 6;
    private static final int BLOCK_AMT_A = 8;
    private static final int MINION_BLOCK_AMT   = 5;
    private static final int MINION_BLOCK_AMT_A = 7;
    private int attackDmg;
    private int blockAmt;
    private int minionBlockAmt;
    private int strAmt;
    private int minionStrAmt;
    // moves
    private static final byte SHED   = 1;
    private static final byte DEFEND = 2;
    private static final byte BUFF   = 3;
    private static final byte DEFEND_BUFF = 4;
    private static final byte ATTACK = 5;
    // minions
    private AbstractMonster[] lice = new AbstractMonster[MAX_LICE];
    private boolean isOpen = true;

    public HeadLouse() {
        this(200.f,0.f);
    }

    public HeadLouse(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, null, x, y);
        this.loadAnimation("images/monsters/HeadLouse/skeleton.atlas", "images/monsters/HeadLouse/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.attackDmg      = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.strAmt         = AscensionHelper.deadlier(this.type) ? BUFF_STRENGTH_A : BUFF_STRENGTH;
        this.minionStrAmt   = AscensionHelper.deadlier(this.type) ? MINION_BUFF_STRENGTH_A : MINION_BUFF_STRENGTH;
        this.blockAmt       = AscensionHelper.tougher(this.type) ? BLOCK_AMT_A : BLOCK_AMT;
        this.minionBlockAmt = AscensionHelper.harder(this.type) ? MINION_BLOCK_AMT_A : MINION_BLOCK_AMT;
        this.damage.add(new DamageInfo(this, attackDmg));
    }

    @Override
    public void usePreBattleAction() {
        int curlUpAmt = AscensionHelper.tougher(this.type) ? CURL_UP_AMT_A : CURL_UP_AMT;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CurlUpPower(this, curlUpAmt)));
        int toSummon = AscensionHelper.deadlier(this.type) ? START_LICE_A : START_LICE;
        for (int i = 0; i < toSummon; i++) {
            AbstractDungeon.actionManager.addToBottom(spawnLouseInFreeSlotAction(true));
        }
    }

    @Override
    public void changeState(String stateName) {
        if (stateName.equals("CLOSED")) {
            this.state.setAnimation(0, "transitiontoclosed", false);
            this.state.addAnimation(0, "idle closed", true, 0.0f);
            this.isOpen = false;
        } else if (stateName.equals("OPEN")) {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "idle", true, 0.0f);
            this.isOpen = true;
        } else if (stateName.equals("REAR_IDLE")) {
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
    public void takeTurn() {
        if (this.nextMove != ATTACK) {
            if (!this.isOpen) {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REAR"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2f));
            } else {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REAR_IDLE"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.9f));
            }
        }
        switch (this.nextMove) {
            case SHED: {
                int n = numAliveMinions();
                int toSummon = n < MIN_LICE ? SUMMON_LICE+1 : SUMMON_LICE;
                if (!this.hasPower(SheddingPower.POWER_ID)) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SheddingPower(this)));
                    toSummon = 1;
                }
                if (AscensionHelper.deadlier(this.type)) toSummon++;
                toSummon = Math.min(MAX_LICE - numAliveMinions(), toSummon);
                for (int i = 0; i < toSummon; i++) {
                    AbstractDungeon.actionManager.addToBottom(spawnLouseInFreeSlotAction(false));
                }
                break;
            }
            case DEFEND: {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m == this) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.blockAmt));
                    } else if (!m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.minionBlockAmt));
                    }
                }
                break;
            }
            case BUFF: {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m == this) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.strAmt), this.strAmt));
                    } else if (!m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.minionStrAmt), this.minionStrAmt));
                    }
                }
                break;
            }
            case DEFEND_BUFF: {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m == this) {
                        if (AbstractDungeon.aiRng.randomBoolean()) {
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.blockAmt / 2));
                        } else {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.strAmt), this.strAmt));
                        }
                    } else if (!m.isDying) {
                        if (AbstractDungeon.aiRng.randomBoolean()) {
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.minionBlockAmt));
                        } else {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.minionStrAmt), this.minionStrAmt));
                        }
                    }
                }
                break;
            }
            case ATTACK: {
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "OPEN"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5f));
                }
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    public int numAliveMinions() {
        int count = 0;
        for (int i = 0; i < lice.length; i++) {
            if (lice[i] != null && !lice[i].isDying) ++count;
        }
        return count;
    }

    private AbstractMonster makeLouse(int slot) {
        float x,y;
        switch (slot) {
            case 0:  x = -70.f;  y = -40.f; break;
            case 2:  x = -370.f; y = -35.f; break;
            case 4:  x = -670.f; y = -43.f; break;
            case 1:  x = -235.f; y =  48.f; break;
            case 3:  x = -535.f; y =  49.f; break;
            default: x = -330.f; y =  0; break;
        }
        x += MathUtils.random(-5.0f, 5.0f);
        y += MathUtils.random(-5.0f, 5.0f);
        AbstractMonster m = getLouse(x,y);
        lice[slot] = m;
        return m;
    }

    private AbstractMonster getLouse(float x, float y) {
        boolean haveDefensive = false;
        for (int i = 0; i < lice.length; i++) {
            if (lice[i] != null && !lice[i].isDying && lice[i] instanceof LouseDefensive) haveDefensive = true;
        }
        switch (haveDefensive ? AbstractDungeon.miscRng.random(1) : AbstractDungeon.miscRng.random(2)) {
            case 0: return new LouseNormal(x, y);
            case 1: return new LouseWeak(x, y);
            default: return new LouseDefensive(x, y); // max 1 defensive louse, to prevent huge weak stacks
        }
    }

    private int freeSlot() {
        for (int i = 0; i < lice.length; i++) {
            if (lice[i] == null || lice[i].isDying) return i;
        }
        return 0;
    }

    public AbstractGameAction spawnLouseInFreeSlotAction(boolean curlUp) {
        int slot = freeSlot();
        return new SpawnLouseAction(makeLouse(slot), curlUp, slot);
    }

    @Override
    protected void getMove(int num) {
        if (this.moveHistory.isEmpty()) {
            this.setMove(MOVES[0], SHED, Intent.UNKNOWN);
        }
        MovePicker moves = new MovePicker();
        int n = numAliveMinions();
        if (n <= 2)                                  moves.add(MOVES[0], SHED,   Intent.UNKNOWN, 3.0f);
        if (n >= MIN_LICE && !this.lastMove(DEFEND)) moves.add(MOVES[1], DEFEND, Intent.DEFEND, 1.0f);
        if (n >= MIN_LICE && !this.lastMove(BUFF))   moves.add(MOVES[2], BUFF,   Intent.BUFF, 0.5f);
        if (n >= MIN_LICE && !this.lastMove(DEFEND_BUFF)) moves.add(DEFEND_BUFF, Intent.DEFEND_BUFF, 1.0f);
        if (n >= MIN_LICE && !this.lastMove(ATTACK)) moves.add(ATTACK, Intent.ATTACK, this.damage.get(0).base, 2.0f);
        moves.pickRandomMove(this);
    }

    @Override
    public void die() {
        super.die();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m.isDead || m.isDying) continue;
            AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
            AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
            AbstractDungeon.actionManager.addToTop(new VFXAction(m, new InflameEffect(m), 0.2f));
        }
    }
}