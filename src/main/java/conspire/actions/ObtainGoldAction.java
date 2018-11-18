package conspire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

public class ObtainGoldAction extends AbstractGameAction {
    private int amount;
    private boolean rain = false;

    public ObtainGoldAction(int amount, AbstractCreature source, boolean rain) {
        this.actionType = AbstractGameAction.ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.amount = amount;
        this.source = source;
        this.target = AbstractDungeon.player;
        this.rain = rain;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_XFAST) {
            if (amount >= 0) {
                CardCrawlGame.sound.play("GOLD_JINGLE");
                if (rain) {
                    for (int i = 0; i < amount; ++i) {
                        AbstractDungeon.effectList.add(new GainPennyEffect(target, target.hb.cX, Settings.HEIGHT, target.hb.cX, target.hb.cY, false));
                    }
                } else if (source != null) {
                    for (int i = 0; i < amount; ++i) {
                        AbstractDungeon.effectList.add(new GainPennyEffect(target, source.hb.cX, source.hb.cY, target.hb.cX, target.hb.cY, false));
                    }
                }
                AbstractDungeon.player.gainGold(amount);
            } else {
                AbstractDungeon.player.loseGold(-amount);
            }
        }
        this.tickDuration();
    }
}
