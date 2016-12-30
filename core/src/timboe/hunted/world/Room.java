package timboe.hunted.world;

import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Tim on 30/12/2016.
 */
public class Room extends Rectangle{

//  private boolean isLarge;
  private boolean isCorridor;
  private HashMap<Room, Room> linksTo;

  Room(float x, float y, float w, float h) {
    super(x,y,w,h);
    isCorridor = false;
//    isLarge = false;
//    if (w >= WorldGen.getInstance().ROOM_LARGE_SIZE && h >= WorldGen.getInstance().ROOM_LARGE_SIZE) isLarge = true;
    linksTo = new HashMap<Room, Room>();
  }

  public void SetCorridor() {
    isCorridor = true;
//    isLarge = false;
  }

  public boolean GetIsCorridor() {
    return isCorridor;
  }

//  public boolean GetIsLarge() {
//    return  true;
//    return isLarge;
//  }

  public void SetLinksTo(final Room room, final Room corridor) {
    linksTo.put(room, corridor); // Links to room via corridor
  }

  public boolean GetLinksTo(final Room toTest) {
    return linksTo.containsKey(toTest);
  }

  public Vector<Room> GetCorridors() {
    Vector<Room> v = new Vector<Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getValue());
    }
    return v;
  }

}
