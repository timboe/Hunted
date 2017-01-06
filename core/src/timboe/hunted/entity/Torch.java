package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import timboe.hunted.Param;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;

/**
 * Created by Tim on 02/01/2017.
 */
public class Torch extends EntityBase {

  public boolean isOn = false;

  public Torch(float x, float y, float r, float angle) {
    super((int)x, (int)y);
    setAsTorchBody(x,y,r);
    setMoveDirection(angle, false);
  }



  public void doCollision() {
    if (isOn) return;
    isOn = true;
    Gdx.app.log("Torch", "Turning on " + this);
    addTorchToEntity(true, false, false, 90f, Param.WALL_FLAME, 0f, 0f);
    Physics.getInstance().litTorches.add(this);
    Sprites.getInstance().addFlameEffect(body.getPosition());
  }

}
