package conspire.relics;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import conspire.Conspire;
import conspire.cards.special.SausageOptionDexterity;
import conspire.cards.special.SausageOptionFocus;
import conspire.cards.special.SausageOptionStrength;

public class SpecialSausage extends AbstractConspireRelic implements CustomSavable<SpecialSausage.Buff> {
    public static final String ID = "conspire:SpecialSausage";
    private static final int STR_AMT = 2;
    private static final int DEX_AMT = 2;
    private static final int FOCUS_AMT = 2;
    public enum Buff {
        STRENGTH, DEXTERITY, FOCUS
    }
    private Buff buff;

    public SpecialSausage() {
        super(ID, AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        if (buff == null) {
            onEquip();
            return;
        }
        switch (buff) {
            case STRENGTH: {
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, STR_AMT), STR_AMT));
                break;
            }
            case DEXTERITY: {
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new DexterityPower(p, DEX_AMT), DEX_AMT));
                break;
            }
            case FOCUS: {
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new FocusPower(p, FOCUS_AMT), FOCUS_AMT));
                break;
            }
        }
        AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(p, this));
    }

    @Override
    public void onEquip() {
        // Show a screen to select a buff
        buff = null;
        ArrayList<AbstractCard> choices = new ArrayList<>();
        choices.add(new SausageOptionStrength(this, STR_AMT));
        choices.add(new SausageOptionDexterity(this, DEX_AMT));
        choices.add(new SausageOptionFocus(this, FOCUS_AMT));
        AbstractDungeon.cardRewardScreen.chooseOneOpen(choices);
    }

    public void setBuff(Buff buff) {
        this.buff = buff;
        this.setDescriptionAfterLoading();
    }

    @Override
    public String getUpdatedDescription() {
        if (buff != null) {
            switch (buff) {
                case STRENGTH:  return DESCRIPTIONS[5] + STR_AMT + DESCRIPTIONS[6];
                case DEXTERITY: return DESCRIPTIONS[5] + DEX_AMT + DESCRIPTIONS[7];
                case FOCUS:     return DESCRIPTIONS[5] + FOCUS_AMT + DESCRIPTIONS[8];
            }
        }
        return DESCRIPTIONS[0] + STR_AMT + DESCRIPTIONS[1] + DEX_AMT + DESCRIPTIONS[2] + FOCUS_AMT + DESCRIPTIONS[3];
    }

    public void setDescriptionAfterLoading() {
        this.description = getUpdatedDescription();
        // TODO: figure out how to change relic name
        /*
        if (buff != null) {
            switch (buff) {
                case STRENGTH:  this.name = DESCRIPTIONS[9];
                case DEXTERITY: this.name = DESCRIPTIONS[10];
                case FOCUS:     this.name = DESCRIPTIONS[11];
            }
        }*/
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SpecialSausage();
    }

    @Override
    public Class<Buff> savedType() {
        return Buff.class;
    }

    @Override
    public void onLoad(Buff buff) {
        this.buff = buff;
        this.setDescriptionAfterLoading();
    }

    @Override
    public Buff onSave() {
        return this.buff;
    }
}