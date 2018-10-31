package conspire.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class RunicOctahedron extends AbstractConspireRelic {
    public static final String ID = "conspire:RunicOctahedron";
    public static final int DRAW_LOSS = 1;
    public static final int NUM_CARDS = 2;

    public RunicOctahedron() {
        super(ID, AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DRAW_LOSS + DESCRIPTIONS[1] + NUM_CARDS + DESCRIPTIONS[2];
    }

    @Override
    public void atTurnStart() {
        this.counter = 0;
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.masterHandSize -= DRAW_LOSS;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.masterHandSize += DRAW_LOSS;
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        ++this.counter;
        if (this.counter % NUM_CARDS == 0) {
            this.counter = 0;
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
            this.flash();
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RunicOctahedron();
    }
}