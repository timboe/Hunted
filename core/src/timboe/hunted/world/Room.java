package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sprites;

import java.util.*;

/**
 * Created by Tim on 30/12/2016.
 */
public class Room extends Rectangle {

  public enum CorridorDirection {VERTICAL, HORIZONTAL, NONE}

  private CorridorDirection corridorDirection = CorridorDirection.NONE;
  public Rectangle corridorProjection = null; // One box wide, down the centre of the corridor, projected out
  private HashMap<Room, Room> linksTo = new HashMap<Room, Room>();
  private float scent = 0f;
  private float connections = 0f;

  Room(float x, float y, float w, float h) {
    super(x,y,w,h);
  }

  public void setCorridor(CorridorDirection d, Room a, Room b) {
    setLinksTo(a,b);
    setLinksTo(b,a);
    a.setLinksTo(this, b);
    b.setLinksTo(this, a);
    corridorDirection = d;
    if (d == CorridorDirection.VERTICAL) {
      corridorProjection = new Rectangle(x + Param.CORRIDOR_SIZE/2, 0, 1, Param.TILE_Y );
      GameState.getInstance().waypoints.add(Sprites.getInstance().getTile((int)x + Param.CORRIDOR_SIZE/2, (int)(y + height + 2)));
      GameState.getInstance().waypoints.add(Sprites.getInstance().getTile((int)x + Param.CORRIDOR_SIZE/2, (int)(y - 3)));
    } else {
      corridorProjection = new Rectangle(0, y + Param.CORRIDOR_SIZE/2, Param.TILE_X, 1 );
      GameState.getInstance().waypoints.add(Sprites.getInstance().getTile((int)(x + width + 2) , (int)y + Param.CORRIDOR_SIZE/2));
      GameState.getInstance().waypoints.add(Sprites.getInstance().getTile((int)(x - 3),          (int)y + Param.CORRIDOR_SIZE/2));
    }

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

  public void removeRoomLink(Room toRemove) {
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (entry.getValue() == toRemove) {
        linksTo.remove(entry.getKey());
        return;
      }
    }
  }

  public boolean getLinksTo(final Room toTest) {
    return linksTo.containsValue(toTest);
  }

  public Vector<Room> getCorridors() {
    Vector<Room> v = new Vector<Room>();
    if (getIsCorridor()) return v; // Corridor has no corridor links
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getKey());
    }
    return v;
  }

  public Vector<Room> getConnectedRooms() { // If a corridor - then still works (link Ra->Rb, Rb->Ra)
    Vector<Room> v = new Vector<Room>();
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      v.add(entry.getValue());
    }
    return v;
  }

  public void addToScent(float toAdd) {
    scent = Math.min(scent + toAdd, 1f);
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (getIsCorridor()) {
        entry.getValue().scent = Math.min(entry.getValue().scent + toAdd/2f, .975f); // Spread to rooms
      } else {
        entry.getKey().scent = Math.min(entry.getKey().scent + toAdd/2f, .975f); // Spread to corridors
        entry.getValue().scent = Math.min(entry.getValue().scent + toAdd/4f, .95f); // And to rooms
      }
    }
  }

  public void updatePhysics() {
    // Scent dies away
    scent = Math.max(scent - Param.SMELL_DISSAPATE, 0f);
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

  public HashMap.Entry<Room,Room> getConnectionTo(Room toGetTo) {
    for (HashMap.Entry<Room,Room> entry : linksTo.entrySet()) {
      if (entry.getKey() == toGetTo) return entry; // if toGetTo was a corridor
      else if (entry.getValue() == toGetTo) return entry; // if toGetTo was a room
    }
    Gdx.app.log("getConnectionTo","No immediate connection to player room");
    return null;
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
    List<Room> keys = new ArrayList<Room>(choices.keySet()); // Round-about way of choosing a random exitDoor
    Room chosen = keys.get(Utility.r.nextInt(keys.size()));
    for (HashMap.Entry<Room,Room> entry : choices.entrySet()) {
      if (entry.getKey() == chosen) {
        return entry;
      }
    }
    Gdx.app.error("Room", "Was unable to choose new room for AI");
    return null;
  }
}
