package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import conspire.relics.Flyswatter;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class FlyswatterPatch {
    @SpirePatch(clz=RewardItem.class, method="claimReward")
    public static class ClaimReward {
        public static void Postfix(RewardItem self) {
            if (self.type == RewardItem.RewardType.CARD && AbstractDungeon.player.hasRelic(Flyswatter.ID)) {
                Flyswatter relic = (Flyswatter)AbstractDungeon.player.getRelic(Flyswatter.ID);
                relic.flashIfUsed();
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="getRewardCards")
    public static class GetRewardCards {
        public static ExprEditor Instrument () {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    /*
                    // TODO: this is an alternative patch, but it migth be a bit unfriendly towards other patches
                    if (m.getClassName().equals("com.megacrit.cardcrawl.dungeons.AbstractDungeon") && m.getMethodName().equals("getCard")) {
                        m.replace("{ $_ = conspire.relics.Flyswatter.getCard(rarity, false); }");
                    } else if (m.getClassName().equals("com.megacrit.cardcrawl.helpers.CardLibrary") && m.getMethodName().equals("getAnyColorCard")) {
                        m.replace("{ $_ = conspire.relics.Flyswatter.getCard(rarity, true); }");
                    }*/
                    if (m.getMethodName().equals("contains")) {
                        // Note: the base game code does *NOT* increment the dupeCount variable, so we do that ourselves here.
                        m.replace("{ $_ = $proceed($$) || (dupeCount++ < 3 && conspire.relics.Flyswatter.checkSkippedStatic(card)); }");
                    }
                }
            };
        }
    }

}
