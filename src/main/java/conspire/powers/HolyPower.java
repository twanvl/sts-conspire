package conspire.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class HolyPower extends AbstractConspirePower {
    public static final String POWER_ID = "conspire:Holy";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HolyPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, owner);
        this.amount = amount;
        this.updateDescription();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (damage > 0.0f) {
            damage = 0.0f;
        }
        return damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        return 0;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
