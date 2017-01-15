package timboe.hunted.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Tim on 06/01/2017.
 */
public class ParticleEffectActor extends Torch {
  public ParticleEffect particleEffect = null;
  Vector2 acc = new Vector2();
  protected float PAOffsetX = 0;
  protected float PAOffsetY = 0;

  public ParticleEffectActor(int x, int y) {
    super(x,y);
  }

  public ParticleEffectActor(ParticleEffect pe) {
    super(0,0);
    particleEffect = pe;
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (textureRegion[currentFrame] != null) super.draw(batch, parentAlpha);
    if (particleEffect != null) particleEffect.draw(batch);
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    if (particleEffect != null) {
      acc.set(getWidth() / 2, getHeight() / 2);
      localToStageCoordinates(acc);
      particleEffect.setPosition(acc.x + PAOffsetX, acc.y + PAOffsetY);
      particleEffect.update(delta);
    }
  }

}