package conspire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import conspire.Conspire;

public abstract class AbstractConspirePower extends AbstractPower {
    public AbstractConspirePower(String id, String name, AbstractCreature owner) {
        this.ID = id;
        this.name = name;
        this.owner = owner;
        this.img = ImageMaster.loadImage(Conspire.powerImage(id));
    }
}