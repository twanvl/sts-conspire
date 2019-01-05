package conspire.potions;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import conspire.powers.EchoDraughtPower;

public class EchoDraught extends AbstractPotion {
    public static final String POTION_ID = "conspire:EchoDraught";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);
    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    public EchoDraught() {
        super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.H, PotionColor.ENERGY);
        this.potency = this.getPotency();
        this.description = DESCRIPTIONS[0];
        this.isThrown = false;
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new EchoDraughtPower(AbstractDungeon.player, this.potency), this.potency));
    }

    @Override
    public AbstractPotion makeCopy() {
        return new EchoDraught();
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 1;
    }
}
