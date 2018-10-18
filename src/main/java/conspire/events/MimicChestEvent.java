package conspire.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;

import conspire.monsters.MimicChest;

public class MimicChestEvent extends AbstractEvent {
    public static final String ID = "conspire:MimicChest";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String ENCOUNTER_ID = MimicChest.ENCOUNTER_NAME;

    // chest before the fight
    boolean inFight = false;
    Texture chestImg;
    private Hitbox chestHb;
    private float shinyTimer = 0.0f;
    private static final float SHINY_INTERVAL = 0.2f;

    public MimicChestEvent() {
        // no text
        this.hasFocus = false; // show proceed button
        this.hasDialog = false;
        this.roomEventText.clear();
        this.roomEventText.hide();
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE; // Allows skipping the chest
        // Chest image (MediumChest)
        this.chestImg = ImageMaster.loadImage("images/npcs/mediumChest.png");
        this.chestHb = new Hitbox(256.0f * Settings.scale, 270.0f * Settings.scale);
        this.chestHb.move(AbstractChest.CHEST_LOC_X, AbstractChest.CHEST_LOC_Y - 90.0f * Settings.scale);
        // Proceed button
        AbstractDungeon.overlayMenu.proceedButton.setLabel(TreasureRoom.TEXT[0]);
        AbstractDungeon.overlayMenu.proceedButton.show();
    }

    @Override
    public void update() {
        super.update();
        if (!inFight) {
            chestHb.update();
            if ((chestHb.hovered && InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp) {
                // begin fight
                beginFight();
            }
        }
        // TODO: patch ProceedButton.update?
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
    }

    private void beginFight() {
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE; // Don't allow skipping the fight
        inFight = true;
        if (Settings.isDailyRun) {
            AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.eventRng.random(30));
        } else {
            AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.eventRng.random(25, 35));
        }
        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelicTier());
        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(ENCOUNTER_ID);
        this.enterCombat();
        AbstractDungeon.lastCombatMetricKey = ENCOUNTER_ID;
    }

    private void updateShiny() {
        if (!inFight) {
            this.shinyTimer -= Gdx.graphics.getDeltaTime();
            if (this.shinyTimer < 0.0f && !Settings.DISABLE_EFFECTS) {
                this.shinyTimer = SHINY_INTERVAL;
                AbstractDungeon.topLevelEffects.add(new ChestShineEffect());
                AbstractDungeon.effectList.add(new SpookyChestEffect());
                AbstractDungeon.effectList.add(new SpookyChestEffect());
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        // render chest image, from AbstractChest.render
        if (!inFight) {
            sb.setColor(Color.WHITE);
            sb.draw(this.chestImg, AbstractChest.CHEST_LOC_X - 256.0f, AbstractChest.CHEST_LOC_Y - 256.0f + AbstractDungeon.sceneOffsetY, 256.0f, 256.0f, 512.0f, 512.0f, Settings.scale, Settings.scale, 0.f, 0, 0, 512, 512, false, false);
            if (this.chestHb.hovered) {
                sb.setBlendFunction(770, 1);
                sb.setColor(new Color(1.0f, 1.0f, 1.0f, 0.3f));
                sb.draw(this.chestImg, AbstractChest.CHEST_LOC_X - 256.0f, AbstractChest.CHEST_LOC_Y - 256.0f + AbstractDungeon.sceneOffsetY, 256.0f, 256.0f, 512.0f, 512.0f, Settings.scale, Settings.scale, 0.f, 0, 0, 512, 512, false, false);
                sb.setBlendFunction(770, 771);
            }
            if (Settings.isControllerMode) {
                sb.setColor(Color.WHITE);
                sb.draw(CInputActionSet.select.getKeyImg(), AbstractChest.CHEST_LOC_X - 32.0f - 150.0f * Settings.scale, AbstractChest.CHEST_LOC_Y - 32.0f - 210.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 64, 64, false, false);
            }
            this.chestHb.render(sb);
            this.updateShiny();
        }
    }

    @Override
    public void renderRoomEventPanel(SpriteBatch sb) {
        // don't render event panel
    }
}
