package conspire.cards.blue;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Shiv;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.BlurPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.GrowthPower;
import com.megacrit.cardcrawl.powers.MalleablePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.abstracts.CustomCard;
import conspire.Conspire;
import conspire.actions.ModifyMagicNumberAction;

public class SharedLibrary extends CustomCard {
    public static final String ID = "conspire:SharedLibrary";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final int COST = 2;

    private static final Class<?>[] COPYABLE_POWERS = { StrengthPower.class, DexterityPower.class, WeakPower.class, VulnerablePower.class, MalleablePower.class, PlatedArmorPower.class, ThornsPower.class, MetallicizePower.class, ArtifactPower.class, BlurPower.class, BarricadePower.class, GrowthPower.class, RitualPower.class};
    public static final Logger logger = LogManager.getLogger(SharedLibrary.class.getName());

    public SharedLibrary() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION + EXTENDED_DESCRIPTION[0], CardType.POWER, CardColor.BLUE, CardRarity.UNCOMMON, CardTarget.SELF_AND_ENEMY);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (Class<?> powerClass : COPYABLE_POWERS) {
            try {
                String powerID = (String) powerClass.getDeclaredField("POWER_ID").get(null);
                AbstractPower power = m.getPower(powerID);
                if (power != null) {
                    // inspired by ConsoleTargetedPower
                    AbstractPower powerCopy = null;
                    try {
                        powerCopy = (AbstractPower) powerClass.getConstructor(AbstractCreature.class, int.class).newInstance(p, power.amount);
                    } catch (Exception e) {
                        try {
                            powerCopy = (AbstractPower) powerClass.getConstructor(AbstractCreature.class).newInstance(p);
                        } catch (Exception e2) {
                            logger.info("failed to instantiate " + power.name);
                        }
                    }
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, powerCopy, power.amount, true));
                }
            } catch (Exception e) {
                logger.error("Exception occurred when copying power " + powerClass.getName(), e);
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        this.rawDescription = DESCRIPTION + EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        super.calculateCardDamage(m);
        this.rawDescription = DESCRIPTION;
        ArrayList<String> toApply = new ArrayList<>();
        for (Class<?> powerClass : COPYABLE_POWERS) {
            try {
                String powerID = (String) powerClass.getDeclaredField("POWER_ID").get(null);
                AbstractPower power = m.getPower(powerID);
                if (power != null) {
                    toApply.add(power.amount + " " + power.name);
                }
            } catch (Exception e) {
                logger.error("Exception occurred when getting power id of " + powerClass.getName(), e);
            }
        }
        if (toApply.isEmpty()) {
            this.rawDescription += EXTENDED_DESCRIPTION[1];
        } else {
            this.rawDescription += EXTENDED_DESCRIPTION[2] + String.join(", ", toApply) + EXTENDED_DESCRIPTION[3];
        }
        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = DESCRIPTION + EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public AbstractCard makeCopy() {
        return new SharedLibrary();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}
