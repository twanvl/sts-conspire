package conspire.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.TreasureRoom;

import conspire.relics.TreasureMap;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class TreasureMapPatch {
    private static final String[] TEXT = CardCrawlGame.languagePack.getRelicStrings(TreasureMap.ID).DESCRIPTIONS;

    // Places where relics are generated
    //  Look for: returnRandomRelic
    //  Look for: returnRandomScreenlessRelic

    @SpirePatch(clz=MonsterRoomElite.class, method="dropReward")
    @SpirePatch(clz=AbstractChest.class, method="open")
    public static class MonsterRoomDropReward {
        public static ExprEditor Instrument () {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("addRelicToRewards")) {
                        m.replace("{ conspire.relics.TreasureMap.map.addRelicToRewardsForCurrentRoom($0, $1); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz=MonsterRoomElite.class, method="returnRandomRelicTier")
    public static class MonsterRoomEliteRelicTier {
        public static SpireReturn<AbstractRelic.RelicTier> Prefix(MonsterRoomElite self) {
            AbstractRelic.RelicTier tier = TreasureMap.map.getRelicTierForCurrentRoom();
            if (tier != null) {
                return SpireReturn.Return(tier);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    // Chest tiers
    @SpirePatch(clz=TreasureRoom.class, method="onPlayerEntry")
    public static class TreasureRoomOnPlayerEntry {
        public static ExprEditor Instrument () {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("getRandomChest")) {
                        m.replace("{ $_ = conspire.relics.TreasureMap.map.getChestForCurrentRoom(); }");
                    }
                }
            };
        }
    }

    // Initialize the Treasure Map when starting a new game, or when entering the next act
    // However, don't do this when loading a save game
    @SpirePatch(clz=AbstractDungeon.class, method="generateMap")
    public static class GenerateMap {
        public static void Postfix() {
            if (!CardCrawlGame.loadingSave) {
                TreasureMap.map.clear();
            }
            if (AbstractDungeon.player.hasRelic(TreasureMap.ID)) {
                TreasureMap.map.initialize();
            }
        }
    }

    // Rendering on the map
    @SpirePatch(clz=MapRoomNode.class, method="render")
    public static class MapRoomNodeRender {
        public static void Postfix(MapRoomNode self, SpriteBatch sb) {
            if (self.hb.hovered && AbstractDungeon.player.hasRelic(TreasureMap.ID)) {
                AbstractRelic relic = TreasureMap.map.getRelicForRoom(self);
                if (relic != null) {
                    ArrayList<PowerTip> tips = new ArrayList<>();
                    int removePadding = 20;
                    TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(relic.img, removePadding,removePadding, relic.img.getWidth()-2*removePadding,relic.img.getHeight()-2*removePadding);
                    tips.add(new PowerTip(relic.name + " ", TEXT[1], region));
                    TipHelper.queuePowerTips(self.hb.x + self.hb.width + 20.0f * Settings.scale, self.hb.y + self.hb.height, tips);
                }
            }
        }
    }
}
