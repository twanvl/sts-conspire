package conspire.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SpicySausage extends AbstractConspireRelic {
    public static final String ID = "conspire:SpicySausage";
    private static final int STR_AMT = 2;
    private static final int DEX_AMT = 2;

    public SpicySausage() {
        super(ID, AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, STR_AMT), STR_AMT));
        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, DEX_AMT), DEX_AMT));
        AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STR_AMT + DESCRIPTIONS[1] + DEX_AMT + DESCRIPTIONS[2];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SpicySausage();
    }
}