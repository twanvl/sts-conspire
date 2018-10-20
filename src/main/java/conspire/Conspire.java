package conspire;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDrawSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import conspire.cards.blue.Rain;
import conspire.cards.blue.SharedLibrary;
import conspire.cards.colorless.Banana;
import conspire.cards.colorless.GhostlyDefend;
import conspire.cards.colorless.GhostlyStrike;
import conspire.cards.colorless.SpireCoStock;
import conspire.cards.curse.Blindness;
import conspire.cards.curse.NecroticWound;
import conspire.cards.green.DoublingDagger;
import conspire.cards.green.PoisonWeapons;
import conspire.cards.red.Charge;
import conspire.cards.red.ExplosiveBarrier;
import conspire.cards.red.HitWhereItHurts;
import conspire.cards.red.Purge;
import conspire.cards.status.InfernalBerry;
import conspire.cards.status.PyramidRune;
import conspire.cards.status.Treasure;
import conspire.events.Investor;
import conspire.events.LoneGhost;
import conspire.events.MimicChestEvent;
import conspire.monsters.FuzzyDie;
import conspire.monsters.HeadLouse;
import conspire.monsters.HollyBat;
import conspire.monsters.MimicChest;
import conspire.monsters.MysteriousRune;
import conspire.monsters.RoseBush;
import conspire.monsters.SneckoGhost;
import conspire.potions.EchoDraught;
import conspire.potions.TimeTravelPotion;
import conspire.powers.CubeRunePower;
import conspire.relics.Boomerang;
import conspire.relics.DecoderRing;
import conspire.relics.Dentures;
import conspire.relics.Flyswatter;
import conspire.relics.GiftBox;
import conspire.relics.GlowingRock;
import conspire.relics.IceCreamScoop;
import conspire.relics.InfiniteJournal;
import conspire.relics.RoyalGoblet;
import conspire.relics.SeveredTorchhead;
import conspire.relics.SlowCooker;
import conspire.relics.TopHat;

@SpireInitializer
public class Conspire implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditKeywordsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostDrawSubscriber {
    public static final String MODNAME = "Conspire";
    public static final String AUTHOR = "twanvl";
    public static final String DESCRIPTION = "Adds new monsters, elites, bosses, relics and cards.";

    public static final Logger logger = LogManager.getLogger(Conspire.class.getName());

    public Conspire() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new Conspire();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("conspire/images/ConspireBadge.png");
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, null);
        receiveEditMonsters();
        receiveEditEvents();
        receiveEditPotions();
        receiveEditSounds();
    }

    public void receiveEditMonsters() {
        // Rose Bush
        BaseMod.addMonster(RoseBush.ENCOUNTER_NAME, RoseBush.NAME, () -> new RoseBush(0.0f, 0.0f));
        BaseMod.addMonster(RoseBush.DOUBLE_ENCOUNTER_NAME, RoseBush.DOUBLE_ENCOUNTER_NAME, () -> new MonsterGroup(
                new AbstractMonster[] { new RoseBush(-280.0f, 10.0f), new RoseBush(80.0f, 30.0f) }));
        BaseMod.addMonsterEncounter(Exordium.ID, new MonsterInfo(RoseBush.ENCOUNTER_NAME, 2.0f));
        BaseMod.addStrongMonsterEncounter(Exordium.ID, new MonsterInfo(RoseBush.DOUBLE_ENCOUNTER_NAME, 1.5f));
        // Fuzzy Dice
        BaseMod.addMonster(FuzzyDie.ENCOUNTER_NAME, FuzzyDie.ENCOUNTER_NAME, () -> new MonsterGroup(
                new AbstractMonster[] { new FuzzyDie(-280.0f, 10.0f), new FuzzyDie(80.0f, 30.0f) }));
        BaseMod.addMonsterEncounter(TheCity.ID, new MonsterInfo(FuzzyDie.ENCOUNTER_NAME, 2.0f));
        // Holly Bat
        BaseMod.addMonster(HollyBat.ENCOUNTER_NAME, HollyBat.NAME, () -> new HollyBat(0.0f, 0.0f));
        BaseMod.addStrongMonsterEncounter(TheCity.ID, new MonsterInfo(HollyBat.ENCOUNTER_NAME, 4.0f));
        // Head Lice
        BaseMod.addMonster(HeadLouse.ENCOUNTER_NAME, HeadLouse.ENCOUNTER_NAME, () -> new HeadLouse());
        BaseMod.addEliteEncounter(TheCity.ID, new MonsterInfo(HeadLouse.ENCOUNTER_NAME, 1.0f));
        // Snecko Ghost
        BaseMod.addMonster(SneckoGhost.ENCOUNTER_NAME, SneckoGhost.NAME, () -> new SneckoGhost(0.0f, 0.0f));
        BaseMod.addEliteEncounter(TheCity.ID, new MonsterInfo(SneckoGhost.ENCOUNTER_NAME, 1.0f));
        // Mysterious Rune
        BaseMod.addMonster(MysteriousRune.ENCOUNTER_NAME, MysteriousRune.NAME, () -> new MysteriousRune(0.0f, 0.0f));
        BaseMod.addBoss(TheCity.ID, MysteriousRune.ENCOUNTER_NAME, "conspire/images/monsters/MysteriousRune/boss.png", "conspire/images/monsters/MysteriousRune/boss-outline.png");
        // Mimic Chest
        BaseMod.addMonster(MimicChest.ENCOUNTER_NAME, MimicChest.NAME, () -> new MimicChest());
    }

    public void receiveEditEvents() {
        BaseMod.addEvent(LoneGhost.ID, LoneGhost.class, Exordium.ID);
        BaseMod.addEvent(Investor.ID, Investor.class);
        BaseMod.addEvent(MimicChestEvent.ID, MimicChestEvent.class);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, "conspire/localization/eng/conspire-cards.json");
        BaseMod.loadCustomStringsFile(EventStrings.class, "conspire/localization/eng/conspire-events.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class, "conspire/localization/eng/conspire-monsters.json");
        BaseMod.loadCustomStringsFile(OrbStrings.class, "conspire/localization/eng/conspire-orbs.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class, "conspire/localization/eng/conspire-potions.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "conspire/localization/eng/conspire-powers.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "conspire/localization/eng/conspire-relics.json");
    }

    @Override
    public void receiveEditKeywords() {
        loadCustomKeywordsStringsFile("conspire/localization/eng/conspire-keywords.json");
    }

    void loadCustomKeywordsStringsFile(String filepath) {
        loadCustomKeywordsStrings(Gdx.files.internal(filepath).readString(String.valueOf(StandardCharsets.UTF_8)));
    }
    void loadCustomKeywordsStrings(String strings) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<Map<String, Keyword>>(){}.getType();
        @SuppressWarnings("unchecked")
        Map<String,Keyword> keywords = (Map<String,Keyword>)gson.fromJson(strings, typeToken);
        for (Keyword kw : keywords.values()) {
            BaseMod.addKeyword(kw.NAMES, kw.DESCRIPTION);
        }
    }

    @Override
    public void receiveEditCards() {
        // red
        BaseMod.addCard(new Charge());
        BaseMod.addCard(new ExplosiveBarrier());
        BaseMod.addCard(new HitWhereItHurts());
        BaseMod.addCard(new Purge());
        // green
        BaseMod.addCard(new DoublingDagger());
        BaseMod.addCard(new PoisonWeapons());
        // blue
        BaseMod.addCard(new Rain());
        BaseMod.addCard(new SharedLibrary());
        // colorless
        BaseMod.addCard(new Banana());
        BaseMod.addCard(new GhostlyDefend());
        BaseMod.addCard(new GhostlyStrike());
        BaseMod.addCard(new SpireCoStock());
        // status
        BaseMod.addCard(new InfernalBerry());
        BaseMod.addCard(new PyramidRune());
        BaseMod.addCard(new Treasure());
        // curse
        BaseMod.addCard(new Blindness());
        BaseMod.addCard(new NecroticWound());
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new Boomerang(), RelicType.SHARED);
        BaseMod.addRelic(new DecoderRing(), RelicType.SHARED);
        BaseMod.addRelic(new Dentures(), RelicType.SHARED);
        BaseMod.addRelic(new Flyswatter(), RelicType.SHARED);
        BaseMod.addRelic(new GiftBox(), RelicType.SHARED);
        BaseMod.addRelic(new GlowingRock(), RelicType.SHARED);
        BaseMod.addRelic(new InfiniteJournal(), RelicType.SHARED);
        BaseMod.addRelic(new RoyalGoblet(), RelicType.SHARED);
        BaseMod.addRelic(new SeveredTorchhead(), RelicType.SHARED);
        BaseMod.addRelic(new SlowCooker(), RelicType.SHARED);
        BaseMod.addRelic(new TopHat(), RelicType.SHARED);
        // blue
        BaseMod.addRelic(new IceCreamScoop(), RelicType.BLUE);
    }

    public void receiveEditPotions() {
        BaseMod.addPotion(EchoDraught.class, new Color(1.0f,0.8f,0.0f,1.0f), null, new Color(1.0f,0.5f,0.0f,1.0f), EchoDraught.POTION_ID);
        BaseMod.addPotion(TimeTravelPotion.class, Color.SKY.cpy(), Color.DARK_GRAY.cpy(), null, TimeTravelPotion.POTION_ID);
    }

    public void receiveEditSounds() {
        addSound("conspire:ORB_WATER_PASSIVE", "conspire/audio/sound/water1.ogg");
        addSound("conspire:ORB_WATER_EVOKE", "conspire/audio/sound/water2.ogg");
        addSound("conspire:ORB_WATER_CHANNEL", "conspire/audio/sound/water3.ogg");
    }

    private static void addSound(String id, String path) {
        @SuppressWarnings("unchecked")
        HashMap<String,Sfx> map = (HashMap<String,Sfx>) ReflectionHacks.getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");
        map.put(id, new Sfx(path, false));
    }

    public static String removeModId(String id) {
        if (id.startsWith("conspire:")) {
            return id.substring(id.indexOf(':') + 1);
        } else {
            logger.warn("Missing mod id on: " + id);
            return id;
        }
    }

    public static String cardImage(String id) {
        return "conspire/images/cards/" + Conspire.removeModId(id) + ".png";
    }
    public static String orbImage(String id) {
        return "conspire/images/orbs/" + Conspire.removeModId(id) + ".png";
    }
    public static String powerImage(String id) {
        return "conspire/images/powers/32/" + Conspire.removeModId(id) + ".png";
    }
    public static String relicImage(String id) {
        return "conspire/images/relics/" + Conspire.removeModId(id) + ".png";
    }
    public static String relicLargeImage(String id) {
        return "conspire/images/relics/large/" + Conspire.removeModId(id) + ".png";
    }
    public static String relicOutlineImage(String id) {
        return "conspire/images/relics/outline/" + Conspire.removeModId(id) + ".png";
    }
    public static String eventImage(String id) {
        return "conspire/images/events/" + Conspire.removeModId(id) + ".jpg";
    }

    // For specific relics
    @Override
    public void receivePostDraw(AbstractCard c) {
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof CubeRunePower) ((CubeRunePower)p).receivePostDraw(c);
        }
    }
}
