package timboe.hunted.world;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Tim on 30/12/2016.
 */
public class Room extends Rectangle{

  private boolean isLarge;

  Room(float x, float y, float w, float h) {
    super(x,y,w,h);
    if (w >= WorldGen.getInstance().ROOM_MEAN_SIZE && h > WorldGen.getInstance().ROOM_MEAN_SIZE) isLarge = true;
  }

  public boolean GetIsLarge() {
    return isLarge;
  }
}
