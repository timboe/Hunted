package timboe.hunted.entity;

import timboe.hunted.Param;
import timboe.hunted.manager.GameState;

/**
 * Created by Tim on 08/01/2017.
 */
public class ExitDoor extends Tile{

  public ExitDoor(int x, int y) {
    super(x,y);
  }

  @Override
  public void act (float delta) {
    // TODO do we need to call on the super here? We don't animate any of these so prob not
    if (GameState.getInstance().progress[0] < Param.SWITCH_TIME) return;
    if (currentFrame == nFrames-1) return;
    if (GameState.getInstance().frame % Param.ANIM_SPEED == 0) ++currentFrame;
  }
}
