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
  private float scent;

  Room(float x, float y, float w, float h) {
    super(x,y,w,h);
    isCorridor = false;
    linksTo = new HashMap<Room, Room>();
    scent = 0;
  }

  public void setCorridor() {
    isCorridor = true;
  }

  public boolean getIsCorridor() {
    return isCorridor;
  }

  public void setLinksTo(final Room room, final Room corridor) {
    linksTo.put(room, corridor); // Links to room via corridor
  }

  public boolean getLinksTo(final Room toTest) {
    return linksTo.containsKey(toTest);
  }

  public Vector<Room> getCorridors() {
    Vector<Room> v = new Vector<Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getValue());
    }
    return v;
  }

  public Vector<Room> getConnectedRooms() {
    Vector<Room> v = new Vector<Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getKey());
    }
    return v;
  }

}
