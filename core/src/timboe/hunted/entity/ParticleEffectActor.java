package timboe.hunted.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Tim on 06/01/2017.
 */
public class ParticleEffectActor extends Actor {
  ParticleEffect particleEffect;
  Vector2 acc = new Vector2();

  public ParticleEffectActor(ParticleEffect pe) {
    super();
    particleEffect = pe;
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    particleEffect.draw(batch);
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    acc.set(getWidth()/2, getHeight()/2);
    localToStageCoordinates(acc);
    particleEffect.setPosition(acc.x, acc.y);
    particleEffect.update(delta);
  }


  public void start() {
    particleEffect.start();
  }

  public void allowCompletion() {
    particleEffect.allowCompletion();
  }

}