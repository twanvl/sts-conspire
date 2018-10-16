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
                    if (m.getMethodName().equals("contains")) {
                        m.replace("{ $_ = $proceed($$) || (dupeCount < 3 && conspire.relics.Flyswatter.checkSkipped(card)); }");
                    }
                }
            };
        }
    }

}
