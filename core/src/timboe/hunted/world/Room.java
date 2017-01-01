package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Rectangle;
import timboe.hunted.Param;

import java.util.*;

/**
 * Created by Tim on 30/12/2016.
 */
public class Room extends Rectangle{

  public enum CorridorDirection {VERTICAL, HORIZONTAL, NONE}

  private CorridorDirection corridorDirection = CorridorDirection.NONE;
  private HashMap<Room, Room> linksTo;
  private float scent = 0f;
  private float connections = 0f;
  private Random r;

  Room(float x, float y, float w, float h) {
    super(x,y,w,h);
    r = new Random();
    linksTo = new HashMap<Room, Room>();
  }

  public void setCorridor(CorridorDirection d, Room a, Room b) {
    setLinksTo(a,b);
    setLinksTo(b,a);
    a.setLinksTo(this, b);
    b.setLinksTo(this, a);
    corridorDirection = d;
  }

  public boolean getIsCorridor() {
    return (corridorDirection != CorridorDirection.NONE);
  }

  public CorridorDirection getCorridorDirection() { return  corridorDirection; }

  public void setLinksTo(final Room from, final Room to) {
    // For rooms: corridors -> connecting rooms
    // For corridors: room -> connecting room (both directions)
    linksTo.put(from, to);
    ++connections;
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
    scent = Math.min(scent + toAdd, 1f);
  }

  public void updatePhysics() {
    // Spread scent about
    float toSpread = scent * Param.SMELL_SPREAD;
    scent -= toSpread;
    if (scent < 1e-6) scent = 0f;
    toSpread *= Param.SMELL_DISSAPATE; // Only spread half, other half is gone for good
    toSpread /= connections;
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (getIsCorridor()) entry.getValue().addToScent( toSpread ); // Spread to rooms
      else entry.getKey().addToScent( toSpread ); // Spread to corridors
    }
  }

  public float getScent() { return scent; }

  public HashMap.Entry<Room,Room> getNeighborRoomWithHighestScentTrail() {
    HashMap.Entry<Room,Room> toReturn = linksTo.entrySet().iterator().next();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (entry.getKey().getScent() > toReturn.getKey().getScent()) { // Check corridors
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
