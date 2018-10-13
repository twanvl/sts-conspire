package conspire;

import java.nio.charset.StandardCharsets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import conspire.cards.status.PyramidRune;
import conspire.monsters.FuzzyDie;
import conspire.monsters.HeadLouse;
import conspire.monsters.MysteriousRune;
import conspire.monsters.RoseBush;
import conspire.monsters.SneckoGhost;

@SpireInitializer
public class Conspire implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
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

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStrings(CardStrings.class, loadJson("conspire/localization/eng/conspire-cards.json"));
        BaseMod.loadCustomStrings(MonsterStrings.class, loadJson("conspire/localization/eng/conspire-monsters.json"));
        BaseMod.loadCustomStrings(PowerStrings.class, loadJson("conspire/localization/eng/conspire-powers.json"));
    }
    private static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new PyramidRune());
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
}

