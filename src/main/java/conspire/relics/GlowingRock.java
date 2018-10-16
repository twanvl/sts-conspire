package conspire.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import conspire.actions.FixedExhaustAction;

public class GlowingRock extends AbstractConspireRelic {
    public static final String ID = "conspire:GlowingRock";
    private static boolean UPTO = true;

    public GlowingRock() {
        super(ID, AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.HEAVY);
    }

    public void onPlayerEndTurn() {
        AbstractDungeon.actionManager.addToBottom(new FixedExhaustAction(AbstractDungeon.player, AbstractDungeon.player, 1, false, false, true, true));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GlowingRock();
    }
}