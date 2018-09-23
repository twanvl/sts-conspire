package moremonsters.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class DodecahedronRunePower extends AbstractPower {
    public static final String POWER_ID = "DodecahedronRune";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int heal;
    private int strength;

    public DodecahedronRunePower(AbstractCreature owner, int amount, int heal, int strength) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.heal = heal;
        this.strength = strength;
        this.updateDescription();
        this.img = ImageMaster.loadImage("images/powers/32/"+POWER_ID+".png");
    }

    public void onUseEnergy(int amount) {
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, POWER_ID, amount));
    }

    @Override
    public void onRemove() {
        AbstractDungeon.actionManager.addToBottom(new HealAction(this.owner, this.owner, this.heal));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.strength), this.strength));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + heal + DESCRIPTIONS[2] + strength + DESCRIPTIONS[3];
    }
}

