package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;

/**
 * Created by Tim on 02/01/2017.
 */
public class Torch extends EntityBase {

  public boolean isOn = false;
  private boolean isPartial;
  private Vector2 lightEffectPos;
  private boolean needsSecondLight;

  public Torch(float x, float y, float lX, float lY, float r, boolean partial, float angle) {
    super((int)x, (int)y);
    isPartial = partial;
    setAsTorchBody(x,y,r);
    lightEffectPos = new Vector2(lX, lY);
    // If the actual light is not in the same position as its effect - or the actual light is partial, need another
    needsSecondLight = (isPartial || body.getPosition().dst(lightEffectPos) < 1e-4);
    setMoveDirection(angle, false);
  }



  public void doCollision() {
    if (isOn) return;
    isOn = true;
    float range = isPartial ? 90f : 180f;
    Gdx.app.log("Torch", "Turning on " + this);
    addTorchToEntity(true, false, false, range, Param.WALL_FLAME, 0f, 0f);
    Physics.getInstance().litTorches.add(this);
    Sprites.getInstance().addFlameEffect(lightEffectPos);
  }

}
