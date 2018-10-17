package conspire.patches;

import java.util.Collections;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import conspire.relics.IceCreamScoop;

public class IceCreamScoopPatch {
    static AbstractOrb draggingOrb = null;
    private static final int HUBRIS_MAX_ORBS = 15;

    @SpirePatch(clz=AbstractPlayer.class, method="combatUpdate")
    public static class CombatUpdate {
        public static void Postfix(AbstractPlayer self) {
            update();
        }
    }

    private static void update() {
        // start dragging
        if (draggingOrb == null && InputHelper.justClickedLeft && AbstractDungeon.player.hasRelic(IceCreamScoop.ID)) {
            for (AbstractOrb o : AbstractDungeon.player.orbs) {
                if (!(o instanceof EmptyOrbSlot) && o.hb.hovered) {
                    draggingOrb = o;
                    break;
                }
            }
        }
        // update drag
        if (draggingOrb != null) {
            int slot = findSlot(draggingOrb);
            if (slot == -1) {
                // orb no longer exists in player's orb list
                draggingOrb = null;
                return;
            }
            float position = getMouseOrbSlot();
            position = Math.min(position, nonEmptySlots()); // don't swap with empty slots
            int newSlot = Math.round(position);
            if (InputHelper.justReleasedClickLeft) {
                moveOrbToSlot(slot, newSlot);
                draggingOrb.setSlot(newSlot, AbstractDungeon.player.maxOrbs); // snap back
                draggingOrb = null;
            } else {
                moveOrbToSlot(slot, newSlot);
                setOrbSlot(draggingOrb, position);
            }
        }
    }

    private static int findSlot(AbstractOrb orb) {
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); ++i) {
            if (AbstractDungeon.player.orbs.get(i) == orb) return i;
        }
        return -1;
    }

    private static int nonEmptySlots() {
        for (int i = 0; i < AbstractDungeon.player.orbs.size() && i < AbstractDungeon.player.maxOrbs; ++i) {
            if (AbstractDungeon.player.orbs.get(i) instanceof EmptyOrbSlot) return i - 1;
        }
        return AbstractDungeon.player.maxOrbs - 1;
    }

    private static void moveOrbToSlot(int from, int to) {
        if (from == to) return;
        for (int i = Math.min(from,to); i < Math.max(from,to); ++i ) {
            Collections.swap(AbstractDungeon.player.orbs, i, i + 1);
        }
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); ++i) {
            AbstractDungeon.player.orbs.get(i).setSlot(i, AbstractDungeon.player.maxOrbs);
        }
    }

    // floating point slot position for mouse cursor
    private static float getMouseOrbSlot() {
        return getOrbSlot(InputHelper.mX, InputHelper.mY);
    }

    // inverse of setSlot
    private static float getOrbSlot(float mX, float mY) {
        int maxOrbs = AbstractDungeon.player.maxOrbs;
        float x = mX - AbstractDungeon.player.drawX;
        float y = mY - (AbstractDungeon.player.drawY + AbstractDungeon.player.hb_h / 2.0f);
        if (maxOrbs > 10) {
            y -= 100.0f * Settings.scale * ((float)(maxOrbs - 10) / ((float)HUBRIS_MAX_ORBS - 10));
        }
        float orbAngle = (float) Math.atan2(y, x) * MathUtils.radiansToDegrees;
        float totalAngle = 100.0f + (float)maxOrbs * 12.0f;
        // from AbstractOrb.setSlot
        float slotNum = (orbAngle - 90.f + totalAngle / 2.0f) / totalAngle * (maxOrbs - 1.0f);
        slotNum = Math.min(maxOrbs-1.f, Math.max(0.f, slotNum));
        return slotNum;
    }

    // like AbstractOrb.setSlot, but can take float slotNum
    private static void setOrbSlot(AbstractOrb orb, float slotNum) {
        int maxOrbs = AbstractDungeon.player.maxOrbs;
        float dist = 160.0f * Settings.scale + (float)maxOrbs * 10.0f * Settings.scale;
        if (maxOrbs > 10) { // Hubris
            dist = 160.0f * Settings.scale + 10 * 10.0f * Settings.scale;
        }
        float angle = 100.0f + (float)maxOrbs * 12.0f;
        float offsetAngle = angle / 2.0f;
        angle *= (float)slotNum / ((float)maxOrbs - 1.0f);
        angle += 90.0f - offsetAngle;
        orb.tX = dist * MathUtils.cosDeg(angle) + AbstractDungeon.player.drawX;
        orb.tY = dist * MathUtils.sinDeg(angle) + AbstractDungeon.player.drawY + AbstractDungeon.player.hb_h / 2.0f;
        if (maxOrbs == 1) {
            orb.tX = AbstractDungeon.player.drawX;
            orb.tY = 160.0f * Settings.scale + AbstractDungeon.player.drawY + AbstractDungeon.player.hb_h / 2.0f;
        }
        if (maxOrbs > 10) { // Hubris
            orb.tY += 100.0f * Settings.scale * ((float)(maxOrbs - 10) / ((float)HUBRIS_MAX_ORBS - 10));
        }
        orb.hb.move(orb.tX, orb.tY);
        // move immediately
        orb.cX = orb.tX;
        orb.cY = orb.tY;
    }
}
