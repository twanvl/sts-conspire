package conspire.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DeflatedDodgeball extends AbstractConspireRelic {
    public static final String ID = "conspire:DeflatedDodgeball";

    public DeflatedDodgeball() {
        super(ID, AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DeflatedDodgeball();
    }
}