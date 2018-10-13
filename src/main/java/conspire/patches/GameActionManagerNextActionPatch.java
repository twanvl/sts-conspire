package conspire.patches;

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
                    m.replace("conspire.powers.CubeRunePower.loseBlockReplacement();");
                }
            }
        };
    }
}
