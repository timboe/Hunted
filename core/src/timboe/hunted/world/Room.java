package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;

/**
 * Created by Tim on 30/12/2016.
 */
public class Room extends Rectangle{

  public enum CorridorDirection {VERTICAL, HORIZONTAL, NONE}

  private CorridorDirection corridorDirection = CorridorDirection.NONE;
  private HashMap<Room, Room> linksTo;
  private float scent = 0f;
  private Random r;

  Room(float x, float y, float w, float h) {
    super(x,y,w,h);
    r = new Random();
    linksTo = new HashMap<Room, Room>();
  }

  public void setCorridor(CorridorDirection d) {
    corridorDirection = d;
  }

  public boolean getIsCorridor() {
    return (corridorDirection != CorridorDirection.NONE);
  }

  public CorridorDirection getCorridorDirection() { return  corridorDirection; }

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

  public HashMap.Entry<Room,Room> getNeighborRoomWithHighestScentTrail() {
    HashMap.Entry<Room,Room> toReturn = linksTo.entrySet().iterator().next();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (entry.getKey().getScent() > toReturn.getKey().getScent()) {
        toReturn = entry;
      }
    }
    return toReturn;
  }

  public HashMap.Entry<Room,Room> getRandomNeighbourRoom(HashSet<Room> roomsVisited) {
    // Try and choose a room not visited
    HashMap<Room, Room> choices = new HashMap<Room, Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (!roomsVisited.contains(entry.getValue())) {
        choices.put(entry.getKey(), entry.getValue());
      }
    }
    if (choices.size() == 0) choices.putAll(linksTo); // No un-visited so random choice between all
    List<Room> keys = new ArrayList<Room>(choices.keySet()); // Round-about way of choosing a random entry
    Room chosen = keys.get(r.nextInt(keys.size()));
    for (HashMap.Entry<Room,Room> entry : choices.entrySet()) {
      if (entry.getKey() == chosen) {
        return entry;
      }
    }
    Gdx.app.error("Room", "Was unable to choose new room for AI");
    return null;
  }
}
