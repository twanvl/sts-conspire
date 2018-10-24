package conspire.relics;

import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import conspire.Conspire;

public abstract class AbstractConspireRelic extends AbstractRelic {
    public AbstractConspireRelic(String id, RelicTier tier, LandingSound sfx) {
        super(id, "", tier, sfx);
        img = ImageMaster.loadImage(Conspire.relicImage(id));
        outlineImg = ImageMaster.loadImage(Conspire.relicOutlineImage(id));
        this.imgUrl = Conspire.relicImgUrl(id);
    }
}