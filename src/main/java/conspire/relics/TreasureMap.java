package conspire.relics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.LargeChest;
import com.megacrit.cardcrawl.rewards.chests.MediumChest;
import com.megacrit.cardcrawl.rewards.chests.SmallChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.TreasureRoom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.abstracts.CustomSaveable;

public class TreasureMap extends AbstractConspireRelic {
    public static final String ID = "conspire:TreasureMap";
    public static final Logger logger = LogManager.getLogger(TreasureMap.class.getName());

    public TreasureMap() {
        super(ID, AbstractRelic.RelicTier.SHOP, AbstractRelic.LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        map.initialize();
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TreasureMap();
    }

    // The actual map data can hang around after we lose the map
    public static TreasureMapData map = new TreasureMapData();
    public static class TreasureMapData {
        static enum ChestTier { SMALL_CHEST, MEDIUM_CHEST, LARGE_CHEST }

        // We need to track:
        //  * For elite rooms:
        //     * relic tier (for MonsterRoomElite.returnRandomRelicTier)
        //     * the actual relic (for MonsterRoomElite.dropReward())
        //     * a second relic if we have the black star? Or we can leave that out for now
        //  * For chest rooms
        //     * the chest tier
        //       * might as well store the actual chest
        //     * the relic tier that was rolled
        //     * the relic
        class RoomInfo {
            ChestTier chestTier;
            RelicTier relicTier;
            String relicName;
            RoomInfo() {}
        }
        private ArrayList<ArrayList<RoomInfo>> rooms;

        private RoomInfo getRoomInfo(MapRoomNode node) {
            if (rooms == null || node == null) return null;
            return rooms.get(node.y).get(node.x);
        }

        private void clearRoomInfo(MapRoomNode node) {
            if (rooms == null || node == null) return;
            rooms.get(node.y).set(node.x, null);
        }

        public void initialize() {
            if (rooms != null) return;
            logger.info("TreasureMap.initialize");
            // Note: don't assign this.rooms yet, because we want to use MonsterRoomElite.returnRandomRelicTier,
            // but we also patch that method as getRelicTierForCurrentRoom.
            // As long as rooms == null, we use the original implementation.
            ArrayList<ArrayList<RoomInfo>> theRooms = new ArrayList<>();
            for (ArrayList<MapRoomNode> mapRow : AbstractDungeon.map) {
                ArrayList<RoomInfo> row = new ArrayList<>();
                for (MapRoomNode node : mapRow) {
                    row.add(initializeRoom(node));
                }
                theRooms.add(row);
            }
            rooms = theRooms;
        }

        private RoomInfo initializeRoom(MapRoomNode node) {
            if (!node.hasEdges()) return null;
            if (node.taken) {
                // Don't put room nodes that we have already visited into the treasure map
                return null;
            }
            if (node.room instanceof MonsterRoomElite) {
                // relic tier according to MonsterRoomElite.returnRandomRelicTier
                RoomInfo info = new RoomInfo();
                try {
                    Method method = MonsterRoomElite.class.getDeclaredMethod("returnRandomRelicTier");
                    method.setAccessible(true);
                    info.relicTier = (AbstractRelic.RelicTier)method.invoke(node.room);
                } catch (Exception ex) {
                    logger.error("Eror calling MonsterRoomElite.returnRandomRelicTier", ex);
                    return null;
                }
                info.relicName = AbstractDungeon.returnRandomRelicKey(info.relicTier);
                return info;
            } else if (node.room instanceof TreasureRoom) {
                // chest tier according to AbstractDungeon.getRandomChest
                RoomInfo info = new RoomInfo();
                AbstractChest chest = AbstractDungeon.getRandomChest(); // this is cheap
                if (chest instanceof SmallChest) {
                    info.chestTier = ChestTier.SMALL_CHEST;
                } else if (chest instanceof MediumChest) {
                    info.chestTier = ChestTier.MEDIUM_CHEST;
                } else if (chest instanceof LargeChest) {
                    info.chestTier = ChestTier.LARGE_CHEST;
                }
                switch (chest.relicReward) {
                    case COMMON_RELIC: info.relicTier = AbstractRelic.RelicTier.COMMON; break;
                    case UNCOMMON_RELIC: info.relicTier = AbstractRelic.RelicTier.UNCOMMON; break;
                    case RARE_RELIC: info.relicTier = AbstractRelic.RelicTier.RARE; break;
                    default: info.relicTier = AbstractRelic.RelicTier.UNCOMMON;
                }
                info.relicName = AbstractDungeon.returnRandomRelicKey(info.relicTier);
                return info;
            } else {
                return null;
            }
        }

        public void clearOnLoad() {
            // loading a savegame without TreasureMap information
            rooms = null;
        }
        public void clear() {
            if (rooms == null) return;
            returnRelicsToPool();
            rooms = null;
        }
        private void returnRelicsToPool() {
            for (ArrayList<RoomInfo> row : rooms) {
                for (RoomInfo info : row) {
                    if (info !=null && info.relicName != null) {
                        switch (info.relicTier) {
                            case COMMON: AbstractDungeon.commonRelicPool.add(info.relicName); break;
                            case UNCOMMON: AbstractDungeon.uncommonRelicPool.add(info.relicName); break;
                            case RARE: AbstractDungeon.rareRelicPool.add(info.relicName); break;
                            default: break;
                        }
                    }
                }
            }
            Collections.shuffle(AbstractDungeon.commonRelicPool, new java.util.Random(AbstractDungeon.relicRng.randomLong()));
            Collections.shuffle(AbstractDungeon.uncommonRelicPool, new java.util.Random(AbstractDungeon.relicRng.randomLong()));
            Collections.shuffle(AbstractDungeon.rareRelicPool, new java.util.Random(AbstractDungeon.relicRng.randomLong()));
        }

        public AbstractRelic getRelicForRoom(MapRoomNode node) {
            RoomInfo info = getRoomInfo(node);
            if (info == null || info.relicName == null) return null;
            return RelicLibrary.getRelic(info.relicName);
        }

        public AbstractRelic.RelicTier getRelicTierForCurrentRoom() {
            RoomInfo info = getRoomInfo(AbstractDungeon.currMapNode);
            if (info == null) return null;
            return info.relicTier;
        }

        public AbstractRelic getRelicForCurrentRoom() {
            return getRelicForRoom(AbstractDungeon.currMapNode);
        }

        public AbstractChest getChestForCurrentRoom() {
            RoomInfo info = getRoomInfo(AbstractDungeon.currMapNode);
            if (info != null && info.chestTier == ChestTier.SMALL_CHEST) {
                return new SmallChest();
            } else if (info != null && info.chestTier == ChestTier.MEDIUM_CHEST) {
                return new MediumChest();
            } else if (info != null && info.chestTier == ChestTier.LARGE_CHEST) {
                return new LargeChest();
            } else {
                return AbstractDungeon.getRandomChest();
            }
        }

        public void addRelicToRewardsForCurrentRoom(AbstractRoom room, AbstractRelic.RelicTier tier) {
            AbstractRelic relic = getRelicForCurrentRoom();
            if (relic != null) {
                room.addRelicToRewards(relic.makeCopy());
                clearRoomInfo(AbstractDungeon.currMapNode);
            } else {
                room.addRelicToRewards(tier);
            }
        }
    }

    public static class TreasureMapDataSaver implements CustomSaveable<TreasureMapData> {
        @Override
        public Class<TreasureMapData> savedType() {
            return TreasureMapData.class;
        }
        @Override
        public TreasureMapData onSave() {
            return TreasureMap.map;
        }
        @Override
        public void onLoad(TreasureMapData data) {
            if (data != null) {
                TreasureMap.map = data;
            } else {
                TreasureMap.map.clearOnLoad();
            }
        }
    }
}