package conspire.relics;

import java.util.ArrayList;
import java.util.HashMap;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class Flyswatter extends AbstractConspireRelic {
    public static final String ID = "conspire:Flyswatter";
    boolean used = false;

    public Flyswatter() {
        super(ID, AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.FLAT);
    }

    public static boolean checkSkipped(AbstractCard card) {
        if (!AbstractDungeon.player.hasRelic(ID)) return false;
        // Has this card previously been skipped?
        // This is a bit hacky, but we can use the metric data
        // see CardRewardScreen.recordMetrics
        String metricID = card.getMetricID();
        for (@SuppressWarnings("rawtypes") HashMap card_choice_ : CardCrawlGame.metricData.card_choices) {
            @SuppressWarnings("unchecked")
            HashMap<String,Object> choice = (HashMap<String,Object>)card_choice_;
            Object picked = choice.get("picked");
            if (picked != null && (picked.equals("SKIP") || picked.equals("Singing Bowl"))) {
                // player picked skip or singing bowl
                @SuppressWarnings("unchecked")
                ArrayList<String> notpicked = (ArrayList<String>)choice.get("not_picked");
                if (notpicked.contains(metricID)) {
                    Flyswatter relic = (Flyswatter)AbstractDungeon.player.getRelic(Flyswatter.ID);
                    relic.used = true;
                    return true;
                }
            }
        }
        return false;
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