package conspire.powers;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import basemod.interfaces.PostDrawSubscriber;

public class CubeRunePower extends AbstractConspirePower implements PostDrawSubscriber {
    public static final String POWER_ID = "conspire:CubeRune";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public AbstractCreature source;

    public CubeRunePower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, NAME, owner);
        this.source = source;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.updateDescription();
    }

    @Override
    public void receivePostDraw(AbstractCard c) {
        // TODO: interact with block from previous turns
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new DamageAction(this.owner, new DamageInfo(this.source, this.amount, DamageInfo.DamageType.THORNS)));
    }

    public static void loseBlockReplacement() {
        if (AbstractDungeon.player.hasPower(POWER_ID)) {
            int aboutToDraw = AbstractDungeon.player.gameHandSize;
            aboutToDraw = Math.min(aboutToDraw, 10 - AbstractDungeon.player.hand.size());
            AbstractDungeon.player.loseBlock(Math.max(0, AbstractDungeon.player.currentBlock - aboutToDraw));
        } else {
            AbstractDungeon.player.loseBlock();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}

