package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Sprites;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Tim on 28/12/2016.
 */
public class WorldGen {
  private static WorldGen ourInstance = new WorldGen();

  public static WorldGen getInstance() {
    return ourInstance;
  }

  private Vector<Room> rooms;
  private Vector<Room> corridors;
  private Random r;

  private final int ROOM_PLACE_TRIES = 2000;
  public final int ROOM_MEAN_SIZE = 15;
  private final int ROOM_STD_D = 5;
  private final int ROOM_BORDER = 1; // minimum spacing between rooms
  private final int CORRIDOR_MAX_LENGTH = 20;
  private final int CORRIDOR_CHANCE = 90; //%

  private WorldGen() {
    r = new Random();
    rooms = new Vector<Room>();
    corridors = new Vector<Room>();
  }

  public void generateWorld() {
    int tryN = 0;
    boolean success = false;
    while (!success && ++tryN < 10 ) {
      success = tryWorld();
    }
    if (!success) {
      Gdx.app.error("WorldGen", "World generation failed");
      Gdx.app.exit();
    } else {
      Gdx.app.log("WorldGen", "World generation finished on " + tryN + " attempt.");
    }
  }

  private boolean tryWorld() {
    reset();
    placeRooms();
    shrinkRooms();
    makeCorridors();
    removeUnconnected();
    if (!allConnected()) {
      Gdx.app.log("WorldGen", "Warning - world not fully navigable");
      return false;
    }
    addRoomsToTileMap();
    disableInvisibleTiles();
    Sprites.getInstance().addTileActors();
    Sprites.getInstance().addTileRigidBodies();
    Room firstRoom = rooms.firstElement();
    Sprites.getInstance().getPlayer().setPhysicsPosition(firstRoom.getX(), firstRoom.getY());
    return true;
  }

  private void reset() {
    rooms.clear();
    corridors.clear();
    Physics.getInstance().reset();
    Sprites.getInstance().reset();
  }

  private void placeRooms() {
    int t = 0;
    final int minX = 1;
    final int minY = 1;
    final int maxX = HuntedGame.TILE_X - HuntedGame.MIN_ROOM_SIZE;
    final int maxY = HuntedGame.TILE_Y - HuntedGame.MIN_ROOM_SIZE;
    while (t < ROOM_PLACE_TRIES) {
      boolean pass = true;
      final long w = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      final long h = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      final long x = minX + r.nextInt(maxX - minX);
      final long y = minY + r.nextInt(maxY - minY);
      Room room = new Room(x, y, w, h);
      if (w < HuntedGame.MIN_ROOM_SIZE + (2 * ROOM_BORDER)) pass = false;
      else if (h < HuntedGame.MIN_ROOM_SIZE + (2 * ROOM_BORDER)) pass = false;
      else if (x + w >= HuntedGame.TILE_X) pass = false;
      else if (y + h >= HuntedGame.TILE_Y) pass = false;
      for (final Rectangle testRoom : rooms) {
        if (!pass) break;
        if (testRoom.overlaps(room)) {
          pass = false;
        }
      }
      if (pass) rooms.add(room);
      else ++t;
    }
  }

  private void shrinkRooms() {
    for (Room room : rooms) {
      room.set(room.getX(), room.getY(), room.getWidth() - ROOM_BORDER, room.getHeight() - ROOM_BORDER);
    }
  }

  private void removeUnconnected() {
    Vector<Room> toRemove = new Vector<Room>();
    for (final Room room : rooms) {
      if (room.getCorridors().size() == 0) toRemove.add(room);
    }
    rooms.removeAll(toRemove);
  }

  private boolean allConnected() {
    // Can we get from one room to all others?
    HashSet<Room> connectedRooms = new HashSet<Room>();
    ArrayList<Room> roomsToExplore = new ArrayList<Room>();
    roomsToExplore.add( rooms.firstElement() );
    while (roomsToExplore.size() > 0) {
      Room room = roomsToExplore.remove(0);
      connectedRooms.add(room);
      for (final Room connected : room.getConnectedRooms()) {
        if (!connectedRooms.contains(connected) && !roomsToExplore.contains(connected)) {
          roomsToExplore.add(connected);
        }
      }
    }
    return (connectedRooms.size() == rooms.size());
  }

  private boolean prob(int chanceOfPass) {
    return (r.nextInt(100) + 1) <= chanceOfPass;
  }

  private void makeCorridors() {
    // Connect large rooms
    Room intersectionY = new Room(0, 0, 0, 0);
    Room intersectionX = new Room(0, 0, 0, 0);
    for (Room room : rooms) {
      Room extendedY = new Room(room.getX(), 0, room.getWidth(), HuntedGame.TILE_Y); // Project out in y
      Room extendedX = new Room(0, room.getY(), HuntedGame.TILE_X, room.getHeight()); // Project out in x
      //Gdx.app.log("dgb", "Taking room ("+room+"), extending to ("+extendedY+")");
      for (Room toCheck : rooms) {
        if (toCheck == room) continue; // Must also be not me
        if (toCheck.getLinksTo(room)) continue; // Must not be linked in the other direction
        boolean overlapY = Intersector.intersectRectangles(extendedY, toCheck, intersectionY);
        if (overlapY && intersectionY.getWidth() >= HuntedGame.CORRIDOR_SIZE) { // Enough space for a corridor
          Room below = room;
          Room above = toCheck;
          if (toCheck.getY() < room.getY()) { // Check length if toCheck is above/below. It is BELOW
            below = toCheck;
            above = room;
          }
          final int corridorLength = (int) (above.getY() - (below.getY() + below.getHeight()));
          if (corridorLength <= CORRIDOR_MAX_LENGTH && prob(CORRIDOR_CHANCE)) { // It fits
            int startX = 0;
            int possibleOffset = (int) intersectionY.getWidth() - HuntedGame.CORRIDOR_SIZE;
            if (possibleOffset > 0) startX = r.nextInt(possibleOffset);
            Room c = new Room(intersectionY.getX() + startX,
              below.getY() + below.getHeight(),
              HuntedGame.CORRIDOR_SIZE,
              corridorLength);
            // Check that the corridor does not intercept any other large rooms
            boolean overlap = false;
            Room fatC = new Room(c.getX() - 1, c.getY(), c.getWidth() + 2, c.getHeight());
            for (Room overlapCheck : rooms) {
              if (overlapCheck.overlaps(fatC)) overlap = true;
            }
            if (!overlap) {
              c.setCorridor();
              corridors.add(c);
              Gdx.app.log("dgb", "Adding V corridor [" + this + "] (" + c + ")");
              below.setLinksTo(above, c);
              above.setLinksTo(below, c);
            }
          }
        }
        boolean overlapX = Intersector.intersectRectangles(extendedX, toCheck, intersectionX);
        if (overlapX && intersectionX.getHeight() >= HuntedGame.CORRIDOR_SIZE) { // Enough space for a corridor
          Room left = room;
          Room right = toCheck;
          if (toCheck.getX() < room.getX()) { // Check if toCheck is left/right. It is LEFT
            left = toCheck;
            right = room;
          }
          final int corridorLength = (int) (right.getX() - (left.getX() + left.getWidth()));
          if (corridorLength <= CORRIDOR_MAX_LENGTH && prob(CORRIDOR_CHANCE)) { // It fits
            int startY = 0;
            int possibleOffset = (int) intersectionX.getHeight() - HuntedGame.CORRIDOR_SIZE;
            if (possibleOffset > 0) startY = r.nextInt(possibleOffset);
            Room c = new Room(left.getX() + left.getWidth(),
              intersectionX.getY() + startY,
              corridorLength,
              HuntedGame.CORRIDOR_SIZE);
            c.setCorridor();
            // Check that the corridor does not intercept any other large rooms
            boolean overlap = false;
            Room fatC = new Room(c.getX(), c.getY() - 1, c.getWidth(), c.getHeight() + 2);
            for (Room overlapCheck : rooms) {
              if (overlapCheck.overlaps(fatC)) overlap = true;
            }
            if (!overlap) {
              c.setCorridor();
              corridors.add(c);
              Gdx.app.log("dgb", "Adding V corridor [" + this + "] (" + c + ")");
              left.setLinksTo(right, c);
              right.setLinksTo(left, c);
            }
          }
        }
      }
    }
  }

  private void addRoomsToTileMap() {
    for (Room room : rooms) addRoomToTileMap(room);
    for (Room room : corridors) addRoomToTileMap(room);
  }

  private void addRoomToTileMap(Room room) {
    for (int x = (int) room.x; x < (int) room.x + (int) room.width; ++x) {
      for (int y = (int) room.y; y < (int) room.y + (int) room.height; ++y) {
        if (x >= HuntedGame.TILE_X || y >= HuntedGame.TILE_Y) {
          Gdx.app.error("coord", "Invalid coordinate in [" + this + "] (" + x + "," + y + ")");
          continue;
        }
        Sprites.getInstance().getTile(x, y).setIsFloor(room);
        if (room.getIsCorridor()) Sprites.getInstance().getTile(x, y).setIsCorridor();
      }
    }
  }

  private void disableInvisibleTiles() {
    for (int x = 0; x < HuntedGame.TILE_X; ++x) {
      for (int y = 0; y < HuntedGame.TILE_Y; ++y) {
        if (neighboursAllDirt(x, y)) Sprites.getInstance().getTile(x, y).setVisible(false);
      }
    }
  }

  private boolean neighboursAllDirt(final int x, final int y) {
    for (int d = 0; d < 8; ++d) {
      int cX = x;
      int cY = y;
      switch (d) {
        case 0:
          ++cY;
          break;
        case 1:
          ++cX;
          break;
        case 2:
          --cY;
          break;
        case 3:
          --cX;
          break;
        case 4:
          ++cX;
          ++cY;
          break;
        case 5:
          ++cX;
          --cY;
          break;
        case 6:
          --cX;
          ++cY;
          break;
        case 7:
          --cX;
          --cY;
          break;
      }
      if (cX >= HuntedGame.TILE_X || cY >= HuntedGame.TILE_Y) continue;
      if (cX < 0 || cY < 0) continue;
      if (Sprites.getInstance().getTile(cX, cY).getIsFloor()) return false;
    }
    return true;
  }

}

