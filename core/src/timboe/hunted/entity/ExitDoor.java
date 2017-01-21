package timboe.hunted.entity;

import timboe.hunted.Param;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;
import timboe.hunted.manager.Sounds;

/**
 * Created by Tim on 08/01/2017.
 */
public class ExitDoor extends EntityBase {

  boolean blocked = true;
  boolean sound = false;

  public ExitDoor(int x, int y) {
    super(x,y);
    // Block my exit
    setAsPhysicsBody(x + 1, y, 1, 1);
    setTexture("entry",5);
  }

  @Override
  public void act (float delta) {
    // TODO do we need to call on the super here? We don't animate any of these so prob not
    if (GameState.getInstance().progress[0] < Param.SWITCH_TIME) return;
    if (!sound) {
      sound = true;
      Sounds.getInstance().doorOpen();
    }
    if (currentFrame == 3 && blocked) {
      blocked = false;
      Physics.getInstance().world.destroyBody(body);
    }
    if (currentFrame == nFrames-1) return;
    if (GameState.getInstance().frame % Param.ANIM_SPEED == 0) ++currentFrame;
  }
}
