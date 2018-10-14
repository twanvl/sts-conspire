package conspire.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SlowCooker extends AbstractConspireRelic {
    public static final String ID = "conspire:SlowCooker";
    private int turn = 0;

    public SlowCooker() {
        super(ID, AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster -= 2; // less energy for the first turn
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster += 2; // less energy for the first turn
    }

    @Override
    public void atPreBattle() {
        this.turn = 0;
    }

    @Override
    public void atTurnStart() {
        // Note: reducing AbstractDungeon.player.energy.energy here for the first turn doesn't work.
        // On the first turn atTurnStart() is called *after* energy is initialized,
        // on later turns it is called before the energy is initialized. Yuck.
        // The first turn uses a completely different code path (via AbstractRoom.update), and gives energy.energyMaster with GainEnergyAndEnableControlsAction
        // Later turns go via GameActionManager.getNextAction -> DrawCardAction (!) and give energy.energy
        if (this.turn == 0) {
            // nothing special on turn 1, that was set up before
        } else if (this.turn == 1) {
            AbstractDungeon.player.energy.energy += 3;
            this.flash();
        }
        ++this.turn;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SlowCooker();
    }
}