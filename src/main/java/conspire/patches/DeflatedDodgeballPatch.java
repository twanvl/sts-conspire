package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;

import conspire.relics.DeflatedDodgeball;

@SpirePatch(clz=AbstractPlayer.class, method="damage")
public class DeflatedDodgeballPatch {
    private static final String[] TEXT = CardCrawlGame.languagePack.getRelicStrings(DeflatedDodgeball.ID).DESCRIPTIONS;

    public static SpireReturn Prefix(AbstractPlayer self, DamageInfo info) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && info.type == DamageType.NORMAL && info.output > 0 && self.hasRelic(DeflatedDodgeball.ID) && AbstractDungeon.cardRandomRng.random(99) < 10) {
            self.getRelic(DeflatedDodgeball.ID).flash();
            int damageAmount = 0;
            if (info.owner == self) {
                for (AbstractRelic r : self.relics) {
                    r.onAttack(info, damageAmount, self);
                }
            }
            if (info.owner != null) {
                for (AbstractPower p : info.owner.powers) {
                    p.onAttack(info, damageAmount, self);
                }
                for (AbstractPower p : self.powers) {
                    damageAmount = p.onAttacked(info, damageAmount);
                }
                for (AbstractRelic r : self.relics) {
                    damageAmount = r.onAttacked(info, damageAmount);
                }
            }
            AbstractDungeon.effectList.add(new BlockedWordEffect(self, self.hb.cX, self.hb.cY, TEXT[1]));
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }
}
