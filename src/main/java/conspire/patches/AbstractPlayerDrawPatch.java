// Make TemporaryConfusion work like confusion
package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.AbstractPower;

import basemod.interfaces.PostDrawSubscriber;
import conspire.powers.CubeRunePower;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(clz = AbstractPlayer.class, method = "draw", paramtypez = {int.class})
public class AbstractPlayerDrawPatch {
    public static ExprEditor Instrument () {
        return new ExprEditor() {
            boolean first = true;
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                // check for TemporaryConfusion
                if (m.getMethodName().equals("hasPower") && first) {
                    first = false;
                    m.replace("{ $_ = ($proceed($$) || com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasPower(conspire.powers.TemporaryConfusionPower.POWER_ID)); }");
                }
            }
        };
    }
}