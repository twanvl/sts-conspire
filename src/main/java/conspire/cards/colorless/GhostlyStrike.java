package conspire.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import conspire.Conspire;

public class GhostlyStrike extends CustomCard {
    public static final String ID = "conspire:GhostlyStrike";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int ATTACK_DMG = 6;

    public GhostlyStrike() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.BASIC, CardTarget.ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.isEthereal = true;
        this.tags.add(AbstractCard.CardTags.STRIKE);
        this.tags.add(BaseModCardTags.BASIC_STRIKE);
    }

    public GhostlyStrike(AbstractCard card) {
        this();
        this.upgraded = card.upgraded;
        this.timesUpgraded = card.timesUpgraded;
        this.baseDamage = card.baseDamage;
        this.baseBlock = card.baseBlock;
        this.baseMagicNumber = card.baseMagicNumber;
        this.cost = card.cost;
        this.inBottleLightning = card.inBottleLightning;
        this.inBottleFlame = card.inBottleFlame;
        this.inBottleTornado = card.inBottleTornado;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.isDebug) {
            if (Settings.isInfo) {
                this.multiDamage = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
                for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); ++i) {
                    this.multiDamage[i] = 150;
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
            } else {
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, 150, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            }
        } else {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new GhostlyStrike();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(3);
        }
    }
}
