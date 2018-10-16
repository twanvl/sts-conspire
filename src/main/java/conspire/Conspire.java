package conspire;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import conspire.cards.blue.SharedLibrary;
import conspire.cards.colorless.Banana;
import conspire.cards.colorless.GhostlyDefend;
import conspire.cards.colorless.GhostlyStrike;
import conspire.cards.colorless.SpireCoStock;
import conspire.cards.curse.Blindness;
import conspire.cards.green.DoublingDagger;
import conspire.cards.green.PoisonWeapons;
import conspire.cards.red.ExplosiveBarrier;
import conspire.cards.red.HitWhereItHurts;
import conspire.cards.red.Purge;
import conspire.cards.status.PyramidRune;
import conspire.events.Investor;
import conspire.events.LoneGhost;
import conspire.monsters.FuzzyDie;
import conspire.monsters.HeadLouse;
import conspire.monsters.MysteriousRune;
import conspire.monsters.RoseBush;
import conspire.monsters.SneckoGhost;
import conspire.potions.EchoDraught;
import conspire.potions.TimeTravelPotion;
import conspire.relics.Boomerang;
import conspire.relics.DecoderRing;
import conspire.relics.Flyswatter;
import conspire.relics.GlowingRock;
import conspire.relics.RoyalGoblet;
import conspire.relics.SeveredTorchhead;
import conspire.relics.SlowCooker;
import conspire.relics.TopHat;
import conspire.relics.InfiniteJournal;

@SpireInitializer
public class Conspire implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditKeywordsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber {
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
        BaseMod.addMonsterEncounter(TheCity.ID, new MonsterInfo(FuzzyDie.ENCOUNTER_NAME, 1.0f));
        // Head Lice
        BaseMod.addMonster(HeadLouse.ENCOUNTER_NAME, HeadLouse.ENCOUNTER_NAME, () -> new HeadLouse());
        BaseMod.addEliteEncounter(TheCity.ID, new MonsterInfo(HeadLouse.ENCOUNTER_NAME, 1.0f));
        // Snecko Ghost
        BaseMod.addMonster(SneckoGhost.ENCOUNTER_NAME, SneckoGhost.NAME, () -> new SneckoGhost(0.0f, 0.0f));
        BaseMod.addEliteEncounter(TheCity.ID, new MonsterInfo(SneckoGhost.ENCOUNTER_NAME, 1.0f));
        // Mysterious Rune
        BaseMod.addMonster(MysteriousRune.ENCOUNTER_NAME, MysteriousRune.NAME, () -> new MysteriousRune(0.0f, 0.0f));
        BaseMod.addBoss(TheCity.ID, MysteriousRune.ENCOUNTER_NAME, "conspire/images/monsters/MysteriousRune/boss.png", "conspire/images/monsters/MysteriousRune/boss-outline.png");
    }

    public void receiveEditEvents() {
        BaseMod.addEvent(LoneGhost.ID, LoneGhost.class, Exordium.ID);
        BaseMod.addEvent(Investor.ID, Investor.class);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, "conspire/localization/eng/conspire-cards.json");
        BaseMod.loadCustomStringsFile(EventStrings.class, "conspire/localization/eng/conspire-events.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class, "conspire/localization/eng/conspire-monsters.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class, "conspire/localization/eng/conspire-potions.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "conspire/localization/eng/conspire-powers.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "conspire/localization/eng/conspire-relics.json");
    }

    @Override
    public void receiveEditKeywords() {
        BaseMod.addKeyword(HitWhereItHurts.KEYWORD_NAMES, HitWhereItHurts.KEYWORD_DESCRIPTION);
    }

    @Override
    public void receiveEditCards() {
        // red
        BaseMod.addCard(new ExplosiveBarrier());
        BaseMod.addCard(new HitWhereItHurts());
        BaseMod.addCard(new Purge());
        // green
        BaseMod.addCard(new DoublingDagger());
        BaseMod.addCard(new PoisonWeapons());
        // blue
        BaseMod.addCard(new SharedLibrary());
        // colorless
        BaseMod.addCard(new Banana());
        BaseMod.addCard(new GhostlyDefend());
        BaseMod.addCard(new GhostlyStrike());
        BaseMod.addCard(new SpireCoStock());
        // status
        BaseMod.addCard(new PyramidRune());
        // curse
        BaseMod.addCard(new Blindness());
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new Boomerang(), RelicType.SHARED);
        BaseMod.addRelic(new DecoderRing(), RelicType.SHARED);
        BaseMod.addRelic(new Flyswatter(), RelicType.SHARED);
        BaseMod.addRelic(new GlowingRock(), RelicType.SHARED);
        BaseMod.addRelic(new InfiniteJournal(), RelicType.SHARED);
        BaseMod.addRelic(new RoyalGoblet(), RelicType.SHARED);
        BaseMod.addRelic(new SeveredTorchhead(), RelicType.SHARED);
        BaseMod.addRelic(new SlowCooker(), RelicType.SHARED);
        BaseMod.addRelic(new TopHat(), RelicType.SHARED);
    }

    public void receiveEditPotions() {
        BaseMod.addPotion(EchoDraught.class, new Color(1.0f,0.8f,0.0f,1.0f), null, new Color(1.0f,0.5f,0.0f,1.0f), EchoDraught.POTION_ID);
        BaseMod.addPotion(TimeTravelPotion.class, Color.SKY.cpy(), Color.DARK_GRAY.cpy(), null, TimeTravelPotion.POTION_ID);
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
}
