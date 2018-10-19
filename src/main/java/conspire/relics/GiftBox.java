package conspire.relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class GiftBox extends AbstractConspireRelic {
    public static final String ID = "conspire:GiftBox";

    public GiftBox() {
        super(ID, AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, this.DESCRIPTIONS[1]);
    }

    private static ArrayList<AbstractCard> getRewardCards() {
        ArrayList<AbstractCard> retVal = new ArrayList<AbstractCard>();
        AbstractCard.CardRarity rarity = AbstractCard.CardRarity.RARE;
        int numCards = 3;
        if (AbstractDungeon.player.hasRelic("Question Card")) {
            ++numCards;
        }
        if (AbstractDungeon.player.hasRelic("Busted Crown")) {
            numCards -= 2;
        }
        if (ModHelper.isModEnabled("Binary")) {
            --numCards;
        }
        for (int i = 0; i < numCards; ++i) {
            AbstractCard card = AbstractDungeon.getCard(rarity);
            while (retVal.contains(card)) {
                card = AbstractDungeon.getCard(rarity);
            }
            retVal.add(card);
        }
        for (AbstractCard c : retVal) {
            if ((c.type == AbstractCard.CardType.ATTACK && AbstractDungeon.player.hasRelic("Molten Egg 2")) ||
                    (c.type == AbstractCard.CardType.SKILL && AbstractDungeon.player.hasRelic("Toxic Egg 2")) ||
                    (c.type == AbstractCard.CardType.POWER && AbstractDungeon.player.hasRelic("Frozen Egg 2"))) {
                c.upgrade();
            }
        }
        return retVal;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GiftBox();
    }
}