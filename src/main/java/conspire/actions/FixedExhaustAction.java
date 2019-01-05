package conspire.actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class FixedExhaustAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ExhaustAction");
    public static final String[] TEXT = uiStrings.TEXT;
    private AbstractPlayer p;
    private boolean isRandom;
    private boolean anyNumber;
    private boolean canPickZero;
    private boolean upTo;
    public static int numExhausted;
    private ArrayList<CardQueueItem> cardQueueBackup;

    public FixedExhaustAction(AbstractCreature target, AbstractCreature source, int amount, boolean isRandom, boolean anyNumber, boolean canPickZero, boolean upTo) {
        this.anyNumber = anyNumber;
        this.canPickZero = canPickZero;
        this.upTo = upTo;
        this.p = (AbstractPlayer)target;
        this.isRandom = isRandom;
        this.setValues(target, source, amount);
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = AbstractGameAction.ActionType.EXHAUST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (this.p.hand.size() == 0) {
                this.isDone = true;
                return;
            }
            if (!this.upTo && !this.canPickZero && !this.anyNumber && this.p.hand.size() <= this.amount) {
                // NOTE: The game's ExhaustAction doesn't check this.canPickZero (or this.upTo)
                numExhausted = this.amount = this.p.hand.size();
                int tmp = this.p.hand.size();
                for (int i = 0; i < tmp; ++i) {
                    AbstractCard c = this.p.hand.getTopCard();
                    this.p.hand.moveToExhaustPile(c);
                }
                CardCrawlGame.dungeon.checkForPactAchievement();
                this.isDone = true;
                return;
            }
            if (this.isRandom) {
                for (int i = 0; i < this.amount; ++i) {
                    this.p.hand.moveToExhaustPile(this.p.hand.getRandomCard(true));
                }
                CardCrawlGame.dungeon.checkForPactAchievement();
                this.isDone = true;
                return;
            } else {
                numExhausted = this.amount;
                // Note: handCardSelectScreen.open clears the card queue, which will break auto-play-at-end-of-turn cards
                // so back it up now
                cardQueueBackup = AbstractDungeon.actionManager.cardQueue;
                AbstractDungeon.actionManager.cardQueue = new ArrayList<>();
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, this.anyNumber, this.canPickZero, false, false, this.upTo);
                this.tickDuration();
                return;
            }
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                this.p.hand.moveToExhaustPile(c);
            }
            CardCrawlGame.dungeon.checkForPactAchievement();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            // restore card queue backup
            AbstractDungeon.actionManager.cardQueue = cardQueueBackup;
        }
        this.tickDuration();
    }
}

