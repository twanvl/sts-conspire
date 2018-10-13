package conspire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import conspire.powers.DodecahedronRunePower;

@SpirePatch(clz = EnergyManager.class, method = "use", paramtypez = {int.class})
public class EnergyManagerUsePatch {
    public static void Postfix(EnergyManager self, int amount) {
        if (AbstractDungeon.getMonsters() == null || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) return;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            for (AbstractPower p : m.powers) {
                if (p instanceof DodecahedronRunePower) {
                    ((DodecahedronRunePower)p).onUseEnergy(amount);
                }
            }
        }
    }
}