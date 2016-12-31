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
    linksTo.put(corridor, room); // Corridor links to room
  }

  public boolean getLinksTo(final Room toTest) {
    return linksTo.containsValue(toTest);
  }

  public Vector<Room> getCorridors() {
    Vector<Room> v = new Vector<Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getKey());
    }
    return v;
  }

  public Vector<Room> getConnectedRooms() {
    Vector<Room> v = new Vector<Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getValue());
    }
    return v;
  }

  public void addToScent(float toAdd) {
    scent = Math.min(scent + toAdd, 1);
  }

  public float getScent() { return scent; }

  public HashMap.Entry<Room,Room> getNeighborRoomWithHighestSccentTrail() {
    HashMap.Entry<Room,Room> toReturn = linksTo.entrySet().iterator().next();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (entry.getKey().getScent() > toReturn.getKey().getScent()) {
        toReturn = entry;
      }
    }
    return toReturn;
  }
}
