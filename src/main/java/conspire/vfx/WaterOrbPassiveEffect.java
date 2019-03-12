package conspire.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class WaterOrbPassiveEffect extends AbstractGameEffect {
    private float effectDuration;
    private float x0, y0;
    private float x;
    private float y;
    private float sX, sY;
    private float tX, tY;
    private static Texture IMG = ImageMaster.loadImage("conspire/images/vfx/bubble.png");
    private Texture img = IMG;

    public WaterOrbPassiveEffect(float x0, float y0) {
        this.duration = this.effectDuration = 1.0f;
        this.startingDuration = this.effectDuration;
        this.x0 = x0;
        this.y0 = y0;
        this.x = x0 + MathUtils.random(-32.0f, 32.0f) * Settings.scale;
        this.y = y0 + MathUtils.random(-32.0f, -25.0f) * Settings.scale;
        this.sX = this.x;
        this.sY = this.y;
        this.tX = this.x + MathUtils.random(-2.0f, 2.0f) * Settings.scale;
        this.tY = y0 + 32.0f * Settings.scale;
        this.color = Color.WHITE.cpy();
        this.scale = MathUtils.random(0.1f, 0.2f) * Settings.scale;
        this.renderBehind = true;
    }

    @Override
    public void update() {
        this.x = Interpolation.linear.apply(this.tX, this.sX, this.duration / this.effectDuration);
        this.y = Interpolation.linear.apply(this.tY, this.sY, this.duration / this.effectDuration);
        super.update();
        float r = (float) Math.sqrt(Math.pow((this.x-this.x0) / Settings.scale,2) + Math.pow((this.y-this.y0) / Settings.scale,2));
        this.color.a = Math.min(1.0f, Math.max(0.0f, (25.0f - r)/5.0f ));
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        //sb.draw(this.img, this.x - (float)this.img.packedWidth / 2.0f, this.y - (float)this.img.packedWidth / 2.0f, (float)this.img.packedWidth / 2.0f, (float)this.img.packedHeight / 2.0f, this.img.packedWidth, this.img.packedHeight, this.scale * MathUtils.random(0.7f, 1.4f), this.scale * MathUtils.random(0.7f, 1.4f), this.rotation);
        sb.draw(this.img, this.x - (float)this.img.getWidth() / 2.0f, this.y - (float)this.img.getWidth() / 2.0f, (float)this.img.getWidth() / 2.0f, (float)this.img.getHeight() / 2.0f, this.img.getWidth(), this.img.getHeight(), this.scale, this.scale, this.rotation, 0, 0, this.img.getWidth(), this.img.getHeight(), false, false);
        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {}
}

