package conspire.helpers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;

// This class helps with implementing getMove. It rebalances the probabilities when some options can't be chosen multiple times in a row.
public class MovePicker {
    public static class MoveOption extends EnemyMoveInfo {
        String moveName;
        float probability;
        MoveOption(String moveName, byte nextMove, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage, float probability) {
            super(nextMove, intent, baseDamage, multiplier, isMultiDamage);
            this.moveName = moveName;
            this.probability = probability;
        }
    }

    private ArrayList<MoveOption> moves = new ArrayList<>();
    private float totalProbability = 0.f;

    public final void add(String moveName, byte nextMove, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage, float probability) {
        this.moves.add(new MoveOption(moveName, nextMove, intent, baseDamage, multiplier, isMultiDamage, probability));
        totalProbability += probability;
    }

    public final void add(byte nextMove, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage, float probability) {
        this.add(null, nextMove, intent, baseDamage, multiplier, isMultiDamage, probability);
    }

    public final void add(byte nextMove, Intent intent, int baseDamage, float probability) {
        this.add(null, nextMove, intent, baseDamage, 0, false, probability);
    }

    public final void add(String moveName, byte nextMove, Intent intent, int baseDamage, float probability) {
        this.add(moveName, nextMove, intent, baseDamage, 0, false, probability);
    }

    public final void add(String moveName, byte nextMove, Intent intent, float probability) {
        this.add(moveName, nextMove, intent, -1, 0, false, probability);
    }

    public final void add(byte nextMove, Intent intent, float probability) {
        this.add(null, nextMove, intent, -1, 0, false, probability);
    }

    public MoveOption getRandomMove() {
        float r = AbstractDungeon.aiRng.random(totalProbability);
        for (MoveOption pick : moves) {
            r -= pick.probability;
            if (r <= 0.f) return pick;
        }
        return moves.get(0);
    }

    public void pickRandomMove(AbstractMonster self) {
        MoveOption pick = this.getRandomMove();
        self.setMove(pick.moveName, pick.nextMove, pick.intent, pick.baseDamage, pick.multiplier, pick.isMultiDamage);
    }
}
