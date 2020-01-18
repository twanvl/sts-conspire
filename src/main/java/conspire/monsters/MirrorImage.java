package conspire.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Point;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.TintEffect;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpriterAnimation;
import conspire.helpers.AscensionHelper;
import conspire.helpers.MovePicker;

public class MirrorImage extends AbstractMonster {
    public static final String ID = "conspire:MirrorImage";
    public static final String ENCOUNTER_NAME = ID;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // stats
    private static final int HP_MIN = 75;
    private static final int HP_MAX = 80;
    private static final int HP_MIN_A = HP_MIN + 5;
    private static final int HP_MAX_A = HP_MAX + 5;
    private static final int ATTACK_DMG   = 6;
    private static final int ATTACK_DMG_A = 9;
    private static final int BLOCK_AMT    = 5;
    private static final int BLOCK_AMT_A  = 8;
    private int attackDmg;
    private int blockAmt;
    // moves
    private static final byte ATTACK_0 = 1;
    private static final byte ATTACK_1 = 2;
    private static final byte ATTACK_2 = 3;
    private static final byte ATTACK_3 = 4;

    public MirrorImage(float x, float y) {
        super(NAME, ID, HP_MAX, AbstractDungeon.player.hb_x / Settings.scale, AbstractDungeon.player.hb_y / Settings.scale, AbstractDungeon.player.hb_w / Settings.scale, AbstractDungeon.player.hb_h / Settings.scale, null, x, y);
        this.img = new Texture(0, 0, Pixmap.Format.RGBA8888); // empty image
        if (AscensionHelper.tougher(this.type)) {
            this.setHp(HP_MIN_A, HP_MAX_A);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }
        this.dialogX = -(AbstractDungeon.player.dialogX - AbstractDungeon.player.drawX); // - because we flip
        this.dialogY =  (AbstractDungeon.player.dialogY - AbstractDungeon.player.drawY);
        // damage amounts
        this.attackDmg = AscensionHelper.deadlier(this.type) ? ATTACK_DMG_A : ATTACK_DMG;
        this.blockAmt  = AscensionHelper.tougher(this.type) ? BLOCK_AMT_A : BLOCK_AMT;
        this.damage.add(new DamageInfo(this, attackDmg));
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            // Draw the Player's image
            // ... store player's values
            float playerDrawX = AbstractDungeon.player.drawX;
            float playerDrawY = AbstractDungeon.player.drawY;
            float playerAnimX = AbstractDungeon.player.animX;
            float playerAnimY = AbstractDungeon.player.animY;
            TintEffect playerTint = AbstractDungeon.player.tint;
            boolean playerFlipHorizontal = AbstractDungeon.player.flipHorizontal;
            float deltaTime = Gdx.graphics.getDeltaTime();
            int spriterSpeed = setSpriterAnimationSpeed(0);
            // ... set our values
            setDeltaTime(0);
            AbstractDungeon.player.drawX = this.drawX;
            AbstractDungeon.player.drawY = this.drawY;
            AbstractDungeon.player.animX = this.animX;
            AbstractDungeon.player.animY = this.animY + 10;
            AbstractDungeon.player.tint = this.tint;
            AbstractDungeon.player.flipHorizontal = !this.flipHorizontal;
            applyFlipToSkeleton();
            updateSpriterAnimationPosition();
            // ... draw
            AbstractDungeon.player.renderPlayerImage(sb);
            // ... restore player's values
            AbstractDungeon.player.drawX = playerDrawX;
            AbstractDungeon.player.drawY = playerDrawY;
            AbstractDungeon.player.animX = playerAnimX;
            AbstractDungeon.player.animY = playerAnimY;
            AbstractDungeon.player.tint = playerTint;
            AbstractDungeon.player.flipHorizontal = playerFlipHorizontal;
            applyFlipToSkeleton();
            updateSpriterAnimationPosition();
            setDeltaTime(deltaTime);
            setSpriterAnimationSpeed(spriterSpeed);
        }
        super.render(sb);
    }

    private void setDeltaTime(float deltaTime) {
        // When we call AbstractPlayer.renderPlayerImage, this updates animations based on Gdx.graphics.getDeltaTime().
        // So it would update the animation twice, resulting in a sped up animation.
        // As a fix, we set deltaTime to 0
        if (Gdx.graphics instanceof LwjglGraphics) {
            ReflectionHacks.setPrivate(Gdx.graphics, LwjglGraphics.class, "deltaTime", deltaTime);
        }
    }

    private void applyFlipToSkeleton() {
        // For some reason the world transform gets updated *after* setting the flip values. So we fix that here
        if (ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractCreature.class, "skeleton") != null) {
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractCreature.class, "skeleton");
            boolean flipVertical = (boolean)ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractCreature.class, "flipVertical");
            skeleton.setFlip(AbstractDungeon.player.flipHorizontal, flipVertical);
        }
    }

    private void updateSpriterAnimationPosition() {
        // Just like the spine skeletons, the SpriterAnimation does update before setting position
        if (AbstractDungeon.player instanceof CustomPlayer) {
            CustomPlayer p = (CustomPlayer)AbstractDungeon.player;
            AbstractAnimation anim = (AbstractAnimation)ReflectionHacks.getPrivate(p, CustomPlayer.class, "animation");
            if (anim instanceof SpriterAnimation) {
                Player myPlayer = (Player)ReflectionHacks.getPrivate(anim, SpriterAnimation.class, "myPlayer");
                Point pos = new Point();
                pos.x = p.drawX + p.animX;
                pos.y = p.drawY + p.animY + AbstractDungeon.sceneOffsetY;
                myPlayer.setPosition(pos);
            }
        }
    }

    private int setSpriterAnimationSpeed(int speed) {
        // Spriter animations don't use Gdx.deltaTime, rather they have a hardcoded speed
        if (AbstractDungeon.player instanceof CustomPlayer) {
            CustomPlayer p = (CustomPlayer)AbstractDungeon.player;
            AbstractAnimation anim = (AbstractAnimation)ReflectionHacks.getPrivate(p, CustomPlayer.class, "animation");
            if (anim instanceof SpriterAnimation) {
                Player myPlayer = (Player)ReflectionHacks.getPrivate(anim, SpriterAnimation.class, "myPlayer");
                int oldSpeed = myPlayer.speed;
                myPlayer.speed = speed;
                return oldSpeed;
            }
        }
        return 0;
    }

    void renderImage() {
        // (re)use the rendering of AbstractPlayer
    }

    @Override
    public void takeTurn() {
        int defends = 0, attacks = 0;
        switch (this.nextMove) {
            case ATTACK_0: {
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[0], 0.5f, 1.7f));
                defends = 3; attacks = 0;
                break;
            }
            case ATTACK_1: {
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[1], 0.5f, 1.7f));
                defends = 2; attacks = 1;
                break;
            }
            case ATTACK_2: {
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[2], 0.5f, 1.7f));
                defends = 1; attacks = 2;
                break;
            }
            case ATTACK_3: {
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[3], 0.5f, 1.7f));
                defends = 0; attacks = 3;
                break;
            }
        }
        for (int i = 0 ; i < defends ; ++i) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.blockAmt));
        }
        for (int i = 0 ; i < attacks ; ++i) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        MovePicker moves = new MovePicker();
        if (!this.lastMove(ATTACK_0)) moves.add(ATTACK_0, Intent.DEFEND, 1.0f);
        if (!this.lastMove(ATTACK_1)) moves.add(ATTACK_1, Intent.ATTACK_DEFEND, this.damage.get(0).base, 1.0f);
        if (!this.lastMove(ATTACK_2)) moves.add(ATTACK_2, Intent.ATTACK_DEFEND, this.damage.get(0).base, 2, true, 1.0f);
        if (!this.lastMove(ATTACK_3)) moves.add(ATTACK_3, Intent.ATTACK, this.damage.get(0).base, 3, true, 1.0f);
        moves.pickRandomMove(this);
    }

}