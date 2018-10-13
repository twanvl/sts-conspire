package conspire.actions;

import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Lethality;
import com.megacrit.cardcrawl.daily.mods.TimeDilation;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.PhilosopherStone;

public class SpawnLouseAction extends AbstractGameAction {
    private float startDuration;
    private AbstractMonster m;
    private int slot;
    private boolean curlUp;

    public SpawnLouseAction(AbstractMonster m, boolean curlUp, int slot) {
        this.actionType = ActionType.SPECIAL;
        this.startDuration = Settings.FAST_MODE ? Settings.ACTION_DUR_FAST : Settings.ACTION_DUR_LONG;
        this.duration = this.startDuration;
        this.m = m;
        this.curlUp = curlUp;
        this.slot = slot;
        if (AbstractDungeon.player.hasRelic(PhilosopherStone.ID)) {
            this.m.addPower(new StrengthPower(this.m, 2));
        }
    }

    @Override
    public void update() {
        float sourceX = ((float)Settings.WIDTH * 0.75f + 200.f * Settings.scale) - this.m.drawX;
        if (this.duration == this.startDuration) {
            this.m.animX =sourceX;
            this.m.init();
            this.m.applyPowers();
            AbstractDungeon.getCurrRoom().monsters.addMonster(this.slot, this.m);
            if (ModHelper.isModEnabled(Lethality.ID)) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.m, this.m, new StrengthPower(this.m, 3), 3));
            }
            if (ModHelper.isModEnabled(TimeDilation.ID)) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.m, this.m, new SlowPower(this.m, 0)));
            }
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.m, this.m, new MinionPower(this.m)));
        }
        this.tickDuration();
        if (this.isDone) {
            this.m.animX = 0.0f;
            this.m.showHealthBar();
            if (this.curlUp) {
                this.m.usePreBattleAction();
            }
        } else {
            this.m.animX = Interpolation.fade.apply(0.0f, sourceX, this.duration / this.startDuration);
        }
    }
}

