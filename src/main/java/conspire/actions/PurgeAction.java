package conspire.actions;

import java.util.UUID;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import conspire.cards.red.Purge;

public class PurgeAction extends AbstractGameAction {
    public static final String[] TEXT = Purge.EXTENDED_DESCRIPTION;
    private boolean isRandom;
    private boolean anyNumber;
    private boolean canPickZero;
    private boolean upTo;


    public PurgeAction(int amount, boolean isRandom, boolean anyNumber, boolean canPickZero, boolean upTo) {
        this.anyNumber = anyNumber;
        this.canPickZero = canPickZero;
        this.upTo = upTo;
        this.isRandom = isRandom;
        this.setValues(AbstractDungeon.player, AbstractDungeon.player, amount);
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = AbstractGameAction.ActionType.EXHAUST;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (p.hand.size() == 0) {
                this.isDone = true;
                return;
            }
            if (!this.upTo && !this.canPickZero && !this.anyNumber && p.hand.size() <= this.amount) {
                // NOTE: The game's ExhaustAction doesn't check this.canPickZero (or this.upTo)
                this.amount = p.hand.size();
                int tmp = p.hand.size();
                for (int i = 0; i < tmp; ++i) {
                    AbstractCard c = p.hand.getTopCard();
                    p.hand.moveToExhaustPile(c);
                    purgeCard(c);
                }
                CardCrawlGame.dungeon.checkForPactAchievement();
                return;
            }
            if (this.isRandom) {
                for (int i = 0; i < this.amount; ++i) {
                    AbstractCard c = p.hand.getRandomCard(true);
                    p.hand.moveToExhaustPile(c);
                    purgeCard(c);
                }
                CardCrawlGame.dungeon.checkForPactAchievement();
            } else {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, this.anyNumber, this.canPickZero, false, false, this.upTo);
                this.tickDuration();
                return;
            }
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                p.hand.moveToExhaustPile(c);
                purgeCard(c);
            }
            CardCrawlGame.dungeon.checkForPactAchievement();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        this.tickDuration();
    }

    public static boolean purgeCard(AbstractCard toPurge) {
        return purgeCard(toPurge.uuid);
    }

    public static boolean purgeCard(UUID targetUUID) {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid.equals(targetUUID)) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, Settings.WIDTH / 2, Settings.HEIGHT / 2));
                AbstractDungeon.player.masterDeck.removeCard(c);
                return true;
            }
        }
        return false;
    }
}

