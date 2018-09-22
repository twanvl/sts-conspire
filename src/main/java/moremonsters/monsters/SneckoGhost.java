package moremonsters.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import com.megacrit.cardcrawl.vfx.combat.IntimidateEffect;

import moremonsters.helpers.AscensionHelper;
import moremonsters.helpers.MovePicker;
import moremonsters.powers.GhostlyPower;
import moremonsters.powers.TemporaryConfusionPower;

public class SneckoGhost extends AbstractMonster {
    public static final String ID = "SneckoGhost";
    public static final String ENCOUNTER_NAME = ID;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // location
    private static final float HB_X = -30.0f;
    private static final float HB_Y = -20.0f;
    private static final float HB_W = 310.0f * 1.2f;
    private static final float HB_H = 305.0f * 1.2f;
    // stats
    private static final int HP_MIN = 140;
    private static final int HP_MAX = 144;
    private static final int HP_MIN_A = 150;
    private static final int HP_MAX_A = 154;
    private static final int BITE_DAMAGE = 18;
    private static final int BITE_DAMAGE_A2 = BITE_DAMAGE + 3;
    private static final int TAIL_DAMAGE = 10;
    private static final int TAIL_DAMAGE_A2 = 10;
    private static final int VULNERABLE_AMT = 2;
    private int biteDmg;
    private int tailDmg;
    private int dazedAmt;
    private int confusionAmt;
    private int vulnerableAmt;
    // moves
    private static final byte GLARE = 1;
    private static final byte BITE = 2;
    private static final byte TAIL = 3;

    public SneckoGhost() {
        this(0.0f, 0.0f);
    }

    public SneckoGhost(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, null, x, y);
        this.loadAnimation("images/monsters/SneckoGhost/skeleton.atlas", "images/monsters/SneckoGhost/skeleton.json", 1.0f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "GhostlyIdle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "GhostlyIdle", 0.1f);
        e.setTimeScale(0.8f);
        this.type = EnemyType.ELITE;
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        // damage amounts
        this.biteDmg = AscensionHelper.deadlier(this.type) ? BITE_DAMAGE_A2 : BITE_DAMAGE;
        this.tailDmg = AscensionHelper.deadlier(this.type) ? TAIL_DAMAGE_A2 : TAIL_DAMAGE;
        this.dazedAmt = AscensionHelper.harder(this.type) ? 4 : 3;
        this.confusionAmt = 1;
        this.vulnerableAmt = VULNERABLE_AMT;
        this.damage.add(new DamageInfo(this, biteDmg));
        this.damage.add(new DamageInfo(this, tailDmg));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new GhostlyPower(this)));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case GLARE: {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntimidateEffect(this.hb.cX, this.hb.cY), 0.5f));
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(AbstractDungeon.player, 1.0f, 1.0f));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new TemporaryConfusionPower(AbstractDungeon.player, this.confusionAmt, true)));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Dazed(), this.dazedAmt, true, false));
                break;
            }
            case BITE: {
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX + MathUtils.random(-50.0f, 50.0f) * Settings.scale, AbstractDungeon.player.hb.cY + MathUtils.random(-50.0f, 50.0f) * Settings.scale, Color.CHARTREUSE.cpy()), 0.3f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.NONE));
                break;
            }
            case TAIL: {
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.SLASH_DIAGONAL));
                if (AbstractDungeon.ascensionLevel >= 17) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.vulnerableAmt, true), 2));
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.vulnerableAmt, true), 2));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    public void changeState(String stateName) {
        switch (stateName) {
            case "GHOSTLY": {
                this.state.setAnimation(0, "GhostlyIdle", true);
                break;
            }
            case "ATTACK": {
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0f);
                break;
            }
            case "ATTACK_2": {
                this.state.setAnimation(0, "Attack_2", false);
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
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.lastMove(GLARE) && !AbstractDungeon.player.hasPower(TemporaryConfusionPower.POWER_ID)) {
            moves.add(MOVES[0], GLARE, Intent.STRONG_DEBUFF,                          80.0f);
        }
        if (true) {
            moves.add(MOVES[1], BITE,  Intent.ATTACK,        this.damage.get(0).base, 40.0f);
        }
        if (!this.lastTwoMoves(TAIL)) {
            moves.add(MOVES[2], TAIL,  Intent.ATTACK_DEBUFF, this.damage.get(1).base, 60.0f);
        }
        moves.pickRandomMove(this);
    }

    @Override
    public void die() {
        super.die();
        CardCrawlGame.sound.play("SNECKO_DEATH");
    }
}
