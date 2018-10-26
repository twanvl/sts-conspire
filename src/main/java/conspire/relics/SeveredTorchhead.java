package conspire.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import conspire.powers.RegainDexterityPower;
import conspire.powers.RegainStrengthPower;

public class SeveredTorchhead extends AbstractConspireRelic {
    public static final String ID = "conspire:SeveredTorchhead";

    public SeveredTorchhead() {
        super(ID, AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
    }

    @Override
    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        AbstractPlayer player = AbstractDungeon.player;
        if (c.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            if (removeArtifact()) return;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(player, player, new StrengthPower(player, -1), -1));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(player, player, new RegainStrengthPower(player, 1), 1));
        } else if (c.type == AbstractCard.CardType.SKILL) {
            this.flash();
            if (removeArtifact()) return;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(player, player, new DexterityPower(player, -1), -1));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(player, player, new RegainDexterityPower(player, 1), 1));
        }
    }

    private boolean removeArtifact() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player.hasPower(ArtifactPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(player, ApplyPowerAction.TEXT[0]));
            CardCrawlGame.sound.play("NULLIFY_SFX");
            player.getPower(ArtifactPower.POWER_ID).flashWithoutSound();
            player.getPower(ArtifactPower.POWER_ID).onSpecificTrigger();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SeveredTorchhead();
    }
}