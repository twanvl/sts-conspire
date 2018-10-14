package conspire.events;

import java.util.ListIterator;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Apparition;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.cards.green.Defend_Green;
import com.megacrit.cardcrawl.cards.green.Strike_Green;
import com.megacrit.cardcrawl.cards.blue.Defend_Blue;
import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import conspire.Conspire;
import conspire.cards.colorless.GhostlyDefend;
import conspire.cards.colorless.GhostlyStrike;

public class LoneGhost extends AbstractImageEvent {
    public static final String ID = "conspire:LoneGhost";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String ACCEPT_BODY = DESCRIPTIONS[1];
    private static final String ARGUE_BODY = DESCRIPTIONS[2];
    private static final String LEAVE_BODY = DESCRIPTIONS[3];
    private static int GOLD_LOSS = 100;
    private CUR_SCREEN screenNum = CUR_SCREEN.INTRO;
    private static enum CUR_SCREEN {
        INTRO,
        COMPLETE;
    }

    public LoneGhost() {
        super(NAME, "test", Conspire.eventImage(ID));
        this.body = DESCRIPTIONS[0];
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1] + GOLD_LOSS + OPTIONS[2], AbstractDungeon.player.gold < 100, new Apparition());
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (this.screenNum == CUR_SCREEN.INTRO) {
            switch (buttonPressed) {
                case 0: {
                    this.logMetric("Agree");
                    this.imageEventText.updateBodyText(ACCEPT_BODY);
                    this.replaceAttacksAndDefends();
                    break;
                }
                case 1: {
                    this.logMetric("Argue");
                    this.imageEventText.updateBodyText(ARGUE_BODY);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Apparition(), (float)Settings.WIDTH / 2.0f, (float)Settings.HEIGHT / 2.0f));
                    UnlockTracker.markCardAsSeen(Apparition.ID);
                    AbstractDungeon.player.loseGold(GOLD_LOSS);
                    break;
                }
                case 2: {
                    this.logMetric("Disagree");
                    this.imageEventText.updateBodyText(LEAVE_BODY);
                    // No effect
                    break;
                }
            }
            this.screenNum = CUR_SCREEN.COMPLETE;
            this.imageEventText.clearAllDialogs();
            this.imageEventText.updateDialogOption(0, OPTIONS[4]);
        } else {
            this.openMap();
        }
    }

    private void replaceAttacksAndDefends() {
        ListIterator<AbstractCard> i = AbstractDungeon.player.masterDeck.group.listIterator();
        while (i.hasNext()) {
            AbstractCard c = i.next();
            if (c.hasTag(BaseModCardTags.BASIC_STRIKE) || (c instanceof CustomCard && ((CustomCard)c).isStrike()) || c instanceof Strike_Red || c instanceof Strike_Green || c instanceof Strike_Blue) {
                AbstractCard c2 = new GhostlyStrike(c);
                i.set(c2);
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c2.makeStatEquivalentCopy(), MathUtils.random(0.1f, 0.9f) * Settings.WIDTH, MathUtils.random(0.2f, 0.8f) * Settings.HEIGHT));
            } else if (c.hasTag(BaseModCardTags.BASIC_DEFEND) || (c instanceof CustomCard && ((CustomCard)c).isDefend()) || c instanceof Defend_Red || c instanceof Defend_Green || c instanceof Defend_Blue) {
                AbstractCard c2 = new GhostlyDefend(c);
                i.set(c2);
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c2.makeStatEquivalentCopy(), MathUtils.random(0.1f, 0.9f) * Settings.WIDTH, MathUtils.random(0.2f, 0.8f) * Settings.HEIGHT));
            }
        }
    }

    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric(ID, actionTaken);
    }

}

