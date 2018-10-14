package conspire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ObtainGoldAction extends AbstractGameAction {
    private int amount;

    public ObtainGoldAction(int amount) {
        this.actionType = AbstractGameAction.ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_XFAST) {
            if (amount >= 0) {
                AbstractDungeon.player.gainGold(amount);
            } else {
                AbstractDungeon.player.loseGold(-amount);
            }
        }
        this.tickDuration();
    }
}
