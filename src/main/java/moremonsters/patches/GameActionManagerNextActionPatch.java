// Make TemporaryConfusion work like confusion
package moremonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(clz = GameActionManager.class, method = "getNextAction", paramtypez = {})
public class GameActionManagerNextActionPatch {
    public static ExprEditor Instrument () {
        return new ExprEditor() {
            boolean first = true;
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                // check for CubeRunePower
                if (m.getMethodName().equals("loseBlock") && first) {
                    first = false;
                    // Note: this hideous code is needed to prevent CannotCompileException: inconsistent stack height -1
                    //m.replace("if ( com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasPower(moremonsters.powers.CubeRunePower.POWER_ID))) { $_ = moremonsters.powers.CubeRunePower.loseBlockReplacement(); } else { $_ = $proceed($$); }");
                    /*m.replace(
                        "if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasPower(moremonsters.powers.CubeRunePower.POWER_ID)) {"+
                            "moremonsters.powers.CubeRunePower.loseBlockReplacement();"+
                        "} else {"+
                            "$_ = $proceed($$);"+
                        "}");*/
                    m.replace("moremonsters.powers.CubeRunePower.loseBlockReplacement();");
                }
            }
        };
    }
}
