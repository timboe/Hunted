package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;

/**
 * Created by Tim on 02/01/2017.
 */
public class Torch extends EntityBase {

  boolean isOn = false;

  public Torch(float x, float y, float r) {
    super((int)x, (int)y);
    setAsTorchBody(x,y,r);
  }

  public void doCollision() {
    if (isOn) return;
    isOn = true;
    Gdx.app.log("Torch", "Turning on " + this);
    addTorchToEntity(false,0f, 0f);
  }

}
