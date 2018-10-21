package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ReflectBlockPowerPatch {
    @SpirePatch(clz=AbstractCreature.class, method="addBlock")
    public static class AddBlock {
        public static ExprEditor Instrument () {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals("com.megacrit.cardcrawl.powers.AbstractPower") && m.getMethodName().equals("onPlayerGainedBlock")) {
                        m.replace("{ if (p instanceof conspire.powers.ReflectBlockPower) {((conspire.powers.ReflectBlockPower)p).onCreatureGainedBlock(this,tmp);} $_ = $proceed($$); }");
                    }
                }
            };
        }
    }
}
