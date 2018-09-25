package moremonsters;

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

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import moremonsters.monsters.FuzzyDie;
import moremonsters.monsters.HeadLouse;
import moremonsters.monsters.MysteriousRune;
import moremonsters.monsters.RoseBush;
import moremonsters.monsters.SneckoGhost;

@SpireInitializer
public class MoreMonsters implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditStringsSubscriber {
    public static final String MODNAME = "More Monsters";
    public static final String AUTHOR = "twanvl";
    public static final String DESCRIPTION = "Adds new monsters, elites and bosses.";

    public MoreMonsters() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new MoreMonsters();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("images/MoreMonstersBadge.png");
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
        BaseMod.addMonster(HeadLouse.ENCOUNTER_NAME, HeadLouse.ENCOUNTER_NAME, () -> new HeadLouse(180.0f, 0.0f));
        BaseMod.addStrongMonsterEncounter(TheCity.ID, new MonsterInfo(HeadLouse.ENCOUNTER_NAME, 1.0f));
        // Snecko Ghost
        BaseMod.addMonster(SneckoGhost.ENCOUNTER_NAME, SneckoGhost.NAME, () -> new SneckoGhost(0.0f, 0.0f));
        BaseMod.addEliteEncounter(TheCity.ID, new MonsterInfo(SneckoGhost.ENCOUNTER_NAME, 1.0f));
        // Mysterious Rune
        BaseMod.addMonster(MysteriousRune.ENCOUNTER_NAME, MysteriousRune.NAME, () -> new MysteriousRune(0.0f, 0.0f));
        BaseMod.addBoss(TheCity.ID, MysteriousRune.ENCOUNTER_NAME, "images/monsters/MysteriousRune/boss.png", "images/monsters/MysteriousRune/boss-outline.png");
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStrings(CardStrings.class, loadJson("localization/eng/more-monsters-cards.json"));
        BaseMod.loadCustomStrings(MonsterStrings.class, loadJson("localization/eng/more-monsters-monsters.json"));
        BaseMod.loadCustomStrings(PowerStrings.class, loadJson("localization/eng/more-monsters-powers.json"));
    }
    private static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    @Override
    public void receiveEditCards() {

    }
}

