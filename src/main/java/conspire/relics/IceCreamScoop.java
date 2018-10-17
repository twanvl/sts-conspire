package conspire.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public class IceCreamScoop extends AbstractConspireRelic {
    public static final String ID = "conspire:IceCreamScoop";

    public IceCreamScoop() {
        super(ID, AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new IceCreamScoop();
    }
}