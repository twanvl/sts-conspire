package conspire.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class RoyalGoblet extends AbstractConspireRelic {
    public static final String ID = "conspire:RoyalGoblet";

    public RoyalGoblet() {
        super(ID, AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
        --AbstractDungeon.player.masterHandSize;
    }

    @Override
    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
        ++AbstractDungeon.player.masterHandSize;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RoyalGoblet();
    }
}