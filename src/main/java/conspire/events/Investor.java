package conspire.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import conspire.Conspire;
import conspire.cards.colorless.SpireCoStock;

public class Investor extends AbstractImageEvent {
    public static final String ID = "conspire:Investor";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private CUR_SCREEN screenNum = CUR_SCREEN.INTRO;
    private static enum CUR_SCREEN {
        INTRO,
        COMPLETE;
    }

    public Investor() {
        super(NAME, "test", Conspire.eventImage(ID));
        this.body = DESCRIPTIONS[0];
        this.imageEventText.setDialogOption(OPTIONS[0] + 50 + OPTIONS[1] + 1 + OPTIONS[2], AbstractDungeon.player.gold < 50, new SpireCoStock());
        this.imageEventText.setDialogOption(OPTIONS[0] + 100 + OPTIONS[1] + 2 + OPTIONS[3], AbstractDungeon.player.gold < 100, new SpireCoStock());
        this.imageEventText.setDialogOption(OPTIONS[0] + 200 + OPTIONS[1] + 4 + OPTIONS[3], AbstractDungeon.player.gold < 200, new SpireCoStock());
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.imageEventText.setDialogOption(OPTIONS[5] + MathUtils.ceil((float)AbstractDungeon.player.maxHealth * 0.05f) + OPTIONS[6]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4]);
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (this.screenNum == CUR_SCREEN.INTRO) {
            switch (buttonPressed) {
                case 0: {
                    logMetric("Invest 1");
                    invest(50,1);
                    break;
                }
                case 1: {
                    logMetric("Invest 2");
                    invest(100,2);
                    break;
                }
                case 2: {
                    logMetric("Invest 4");
                    invest(200,4);
                    break;
                }
                case 3: {
                    logMetric("Leave");
                    this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    if (AbstractDungeon.ascensionLevel >= 15) {
                        AbstractDungeon.player.damage(new DamageInfo(null, MathUtils.ceil((float)AbstractDungeon.player.maxHealth * 0.05f), DamageInfo.DamageType.HP_LOSS));
                    }
                    break;
                }
            }
            this.screenNum = CUR_SCREEN.COMPLETE;
            this.imageEventText.clearAllDialogs();
            this.imageEventText.updateDialogOption(0, OPTIONS[7]);
        } else {
            this.openMap();
        }
    }

    private void invest(int gold, int amt) {
        AbstractDungeon.player.loseGold(gold);
        for (int i = 0; i < amt; ++i) {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new SpireCoStock(), (float)Settings.WIDTH / 2.0f, (float)Settings.HEIGHT / 2.0f));
        }
        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
    }

    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric(ID, actionTaken);
    }

}

