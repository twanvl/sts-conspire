package conspire.cards.red;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import basemod.abstracts.CustomCard;
import conspire.Conspire;

public class HitWhereItHurts extends CustomCard {
    public static final String ID = "conspire:HitWhereItHurts";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 2;
    private static final int DAMAGE = 12;

    public HitWhereItHurts() {
        super(ID, NAME, Conspire.cardImage(ID), COST, DESCRIPTION, CardType.ATTACK, CardColor.RED, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.calculateCardDamage(m);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void applyPowers() {
        this.baseDamage = DAMAGE;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        AbstractPower vulnerablePower = m.getPower(VulnerablePower.POWER_ID);
        int vulnerable = vulnerablePower != null ? vulnerablePower.amount : 0;
        this.baseDamage = (int)Math.round(DAMAGE * Math.pow(this.upgraded ? 1.5 : 1.25, vulnerable));
        super.calculateCardDamage(m);
    }

    @Override
    public AbstractCard makeCopy() {
        return new HitWhereItHurts();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}
