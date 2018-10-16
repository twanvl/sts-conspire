package conspire.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public class TopHat extends AbstractConspireRelic {
    public static final String ID = "conspire:TopHat";

    public TopHat() {
        super(ID, AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TopHat();
    }
}