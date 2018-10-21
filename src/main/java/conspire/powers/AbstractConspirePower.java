package conspire.powers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import conspire.Conspire;

public abstract class AbstractConspirePower extends AbstractPower {
    public static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("conspire/images/powers/powers.atlas"));

    public AbstractConspirePower(String id, String name, AbstractCreature owner) {
        this.ID = id;
        this.name = name;
        this.owner = owner;
        String filename = Conspire.removeModId(id);
        this.region48 = atlas.findRegion("48/" + filename);
        this.region128 = atlas.findRegion("128/" + filename);
    }
}