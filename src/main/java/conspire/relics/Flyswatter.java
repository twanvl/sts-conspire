package conspire.relics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class Flyswatter extends AbstractConspireRelic {
    public static final String ID = "conspire:Flyswatter";
    private boolean used = false;
    private HashSet<String> skippedCards = new HashSet<>();
    private int numCardChoicesProcessed = 0;

    public Flyswatter() {
        super(ID, AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.FLAT);
    }

    // get a card that was not previously skipped (if you have Flyswatter)
    public static AbstractCard getCard(AbstractCard.CardRarity rarity, boolean anyclass) {
        AbstractCard card = anyclass ? CardLibrary.getAnyColorCard(rarity) : AbstractDungeon.getCard(rarity);
        // Check if card was skipped previously. Do this a limited number of times
        for (int i = 0 ; i < 4 && checkSkippedStatic(card) ; ++i) {
            card = anyclass ? CardLibrary.getAnyColorCard(rarity) : AbstractDungeon.getCard(rarity);
        }
        return card;
    }

    public static boolean checkSkippedStatic(AbstractCard card) {
        AbstractRelic relic = AbstractDungeon.player.getRelic(ID);
        if (relic != null) {
            return ((Flyswatter)relic).checkSkipped(card);
        } else {
            return false;
        }
    }

    private boolean checkSkipped(AbstractCard card) {
        // Has this card previously been skipped?
        updateSkippedCards();
        String metricID = card.getMetricID();
        if (skippedCards.contains(metricID)) {
            this.used = true;
            return true;
        } else {
            return false;
        }
    }

    private void updateSkippedCards() {
        // This is a bit hacky, but we can use the metric data
        //  see CardRewardScreen.recordMetrics
        // Note 2: We use a HashSet to cache the set of skipped cards, this way we don't loop over the metric data each time
        for (int i = numCardChoicesProcessed ; i < CardCrawlGame.metricData.card_choices.size() ; ++i) {
            @SuppressWarnings("unchecked")
            HashMap<String,Object> choice = (HashMap<String,Object>)CardCrawlGame.metricData.card_choices.get(i);
            Object picked = choice.get("picked");
            if (picked != null && (picked.equals("SKIP") || picked.equals("Singing Bowl"))) {
                // player picked skip or singing bowl
                @SuppressWarnings("unchecked")
                ArrayList<String> notpicked = (ArrayList<String>)choice.get("not_picked");
                skippedCards.addAll(notpicked);
            }
        }
        numCardChoicesProcessed = CardCrawlGame.metricData.card_choices.size();
    }

    public void flashIfUsed() {
        if (this.used) {
            this.flash();
        }
        this.used = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Flyswatter();
    }
}