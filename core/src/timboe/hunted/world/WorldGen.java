package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.entity.Tile;
import timboe.hunted.render.Sprites;

import java.util.*;

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
  private Vector<Room> allRooms;
  private Random r;

  private Room nearestCentre;
  private Room entryRoom;
  private Vector<Room> keyRooms;

  private final int ROOM_PLACE_TRIES = 2000;
  public final int ROOM_MEAN_SIZE = 15;
  private final int ROOM_STD_D = 5;
  private final int ROOM_BORDER_X = 2; // minimum spacing between rooms
  private final int ROOM_BORDER_Y = 4;
  private final int CORRIDOR_MAX_LENGTH = 20;
  private final int CORRIDOR_CHANCE = 95; //%

  private WorldGen() {
    r = new Random();
    rooms = new Vector<Room>();
    corridors = new Vector<Room>();
    allRooms = new Vector<Room>();
    keyRooms = new Vector<Room>();
  }

  public Vector<Room> getRooms() { return rooms; }

  public Vector<Room> getCorridors() { return corridors; }

  public Vector<Room> getAllRooms() { return allRooms; }

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
    boolean success = true;
    reset();
    placeRooms();
    shrinkRooms();
    makeCorridors();
    removeUnconnected();
    if (!getAllConnected()) {
      Gdx.app.error("WorldGen", "Warning - world not fully navigable");
      return false;
    }
    addRoomsToTileMap();
    Sprites.getInstance().disableInvisibleTiles();
    Sprites.getInstance().addTileActors();
    Sprites.getInstance().addTileRigidBodies();
    success &= placeBigBad();
    success &= placeEntrance();
    success &= placeKeyRooms();
    Sprites.getInstance().textureWalls();
    return success;
  }

  private void reset() {
    rooms.clear();
    corridors.clear();
    allRooms.clear();
    keyRooms.clear();
    entryRoom = null;
    nearestCentre = null;
    Physics.getInstance().reset();
    Sprites.getInstance().reset();
  }

  private void placeRooms() {
    int t = 0;
    final int minX = 1;
    final int minY = 1;
    final int maxX = Param.TILE_X - Param.MIN_ROOM_SIZE;
    final int maxY = Param.TILE_Y - Param.MIN_ROOM_SIZE;
    while (t < ROOM_PLACE_TRIES) {
      boolean pass = true;
      long w = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      long h = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      if (w % 2 == 0) ++w; // Force odd
      if (h % 2 == 0) ++h; // Force odd
      final long x = minX + r.nextInt(maxX - minX);
      final long y = minY + r.nextInt(maxY - minY);
      Room room = new Room(x, y, w, h);
      if (w < Param.MIN_ROOM_SIZE + ROOM_BORDER_X) pass = false;
      else if (h < Param.MIN_ROOM_SIZE + ROOM_BORDER_Y) pass = false;
      else if (x + w >= Param.TILE_X) pass = false;
      else if (y + h >= Param.TILE_Y - 1) pass = false; // Note - we need to keep two clear at the top
      for (final Rectangle testRoom : rooms) {
        if (!pass) break;
        if (testRoom.overlaps(room)) {
          pass = false;
        }
      }
      if (pass) {
        rooms.add(room);
        allRooms.add(room);
      }
      else ++t;
    }
  }

  private boolean placeBigBad() {
    // Find the room nearest the centre
    nearestCentre = null;
    float minDist = 9999f;
    final Vector2 target = new Vector2(Param.TILE_X/2, Param.TILE_Y/2);
    for (Room room : rooms) {
      Vector2 dist = new Vector2(room.x + room.width/2, room.y + room.height/2);
      dist.sub(target);
      if (dist.len() < minDist) {
        minDist = dist.len();
        nearestCentre = room;
      }
    }
    if (nearestCentre == null) {
      Gdx.app.error("WorldGen","Warning - could not place BigBad");
      return false;
    }
    // Place baddy
    Sprites.getInstance().getBigBad().setPhysicsPosition(Math.round(nearestCentre.x + nearestCentre.width/2), Math.round(nearestCentre.y + nearestCentre.height/2));
    Sprites.getInstance().getPlayer().setPhysicsPosition(nearestCentre.x + 1, nearestCentre.y + 1);
    return true;
  }

  private boolean placeEntrance() {
    // Find a room at the top. Needs to be in top 75%, wide enough, no north connections
    Vector<Room> entryRoomOptions = new Vector<Room>();
    for (final Room room : rooms) {
      if (room.y < 3*Param.TILE_Y/4) continue;
      if (room.width < 9) continue;
      boolean hasNortherer = false;
      for (Room connected : room.getConnectedRooms()) {
        if (connected.y > room.y) {
          hasNortherer = true;
          break;
        }
      }
      if (!hasNortherer) entryRoomOptions.add(room);
    }
    if (entryRoomOptions.size() == 0) {
      Gdx.app.error("WorldGen", "Warning - Could not find a maze entrance");
      return false;
    }
    entryRoom = entryRoomOptions.elementAt( r.nextInt(entryRoomOptions.size()) );
    Sprites.getInstance().addEntryRoom(entryRoom);
    return true;
  }

  public boolean placeKeyRooms() {
    final int exclusionDist = Math.min(Param.TILE_X, Param.TILE_Y) / 4; // Rooms must be far apart
    final Vector2 entryRoomPos = entryRoom.getPosition(new Vector2());
    int attempt = 0;
    while (keyRooms.size() != Param.KEY_ROOMS && ++attempt < ROOM_PLACE_TRIES) {
      Room room = rooms.get( Utility.r.nextInt(rooms.size()) );
      if (room.getConnectedRooms().size() > 2) continue; // Room can have at most two connections
      if (room.width < 10 || room.height < 10) continue; // Room has to be large enough
      Vector2 roomPos = room.getPosition(new Vector2());
      boolean vetoed = roomPos.dst(entryRoomPos) < exclusionDist;
      for (Room testRoom : keyRooms) {
        Vector2 testPos = testRoom.getPosition(new Vector2());
        vetoed |= (roomPos.dst(testPos) < exclusionDist);
        if (vetoed) break;
      }
      if (vetoed) continue;
      Vector<Rectangle> options = new Vector<Rectangle>();
      Vector<Room> connectedCorridors = room.getCorridors();
      // Can I fit in the machinery?
      for (int x = (int)room.x + 3; x < room.x + room.width - 6; ++x) {
        for (int y = (int)room.y + 3; y < room.y + room.height - 6; ++y) {
          Rectangle r = new Rectangle(x, y, 4, 4);
          boolean overlaps = false;
          for (Room corridor : connectedCorridors ) {
            overlaps |= r.overlaps(corridor.corridorProjection);
          }
          if (!overlaps) options.add(r);
        }
      }
      if (options.size() == 0) continue;
      installKeyRoom(room, options.get(Utility.r.nextInt(options.size())), keyRooms.size());
    }
    // Do we have three rooms?
    if (keyRooms.size() != Param.KEY_ROOMS) {
      Gdx.app.log("WorldGen","Unable to place "+Param.KEY_ROOMS+" key rooms, only managed " + keyRooms.size());
      return false;
    }
    return true;
  }

  private void installKeyRoom(Room room, Rectangle keyLoc, int shrineN) {
    Sprites.getInstance().addKeyShrine((int)keyLoc.x, (int)keyLoc.y, shrineN);
    keyRooms.add(room);
  }

  private void shrinkRooms() {
    for (Room room : rooms) {
      room.set(room.getX(), room.getY(), room.getWidth() - ROOM_BORDER_X, room.getHeight() - ROOM_BORDER_Y);
    }
  }

  private void removeUnconnected() {
    Vector<Room> toRemove = new Vector<Room>();
    for (final Room room : rooms) {
      if (room.getCorridors().size() == 0) toRemove.add(room);
    }
    rooms.removeAll(toRemove);
    allRooms.removeAll(toRemove);
    if (toRemove.size() > 0) Gdx.app.log("WorldGen","Removing " + toRemove.size() + " unconnected rooms.");
  }

  private boolean getAllConnected() {
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
    Gdx.app.log("WorldGen","Connections between " + connectedRooms.size() + " of " + rooms.size() + " rooms.");
    return (connectedRooms.size() == rooms.size());
  }

  private void makeCorridors() {
    // Connect large rooms
    Room intersectionY = new Room(0, 0, 0, 0);
    Room intersectionX = new Room(0, 0, 0, 0);
    for (Room room : rooms) {
      Room extendedY = new Room(room.getX(), 0, room.getWidth(), Param.TILE_Y); // Project out in y
      Room extendedX = new Room(0, room.getY(), Param.TILE_X, room.getHeight()); // Project out in x
      for (Room toCheck : rooms) {
        if (toCheck == room) continue; // Must also be not me
        if (toCheck.getLinksTo(room)) continue; // Must not be linked in the other direction
        boolean overlapY = Intersector.intersectRectangles(extendedY, toCheck, intersectionY);
        if (overlapY && intersectionY.getWidth() >= Param.CORRIDOR_SIZE) { // Enough space for a corridor
          Room below = room;
          Room above = toCheck;
          if (toCheck.getY() < room.getY()) { // Check length if toCheck is above/below. It is BELOW
            below = toCheck;
            above = room;
          }
          final int corridorLength = (int) (above.getY() - (below.getY() + below.getHeight()));
          if (corridorLength <= CORRIDOR_MAX_LENGTH && Utility.prob(CORRIDOR_CHANCE)) { // It fits
            int startX = 0;
            int possibleOffset = (int) intersectionY.getWidth() - Param.CORRIDOR_SIZE;
            if (possibleOffset > 0) startX = r.nextInt(possibleOffset);
            Room c = new Room(intersectionY.getX() + startX,
              below.getY() + below.getHeight(),
              Param.CORRIDOR_SIZE,
              corridorLength);
            // Check that the corridor does not intercept any other large rooms
            boolean overlap = false;
            Room fatC = new Room(c.getX() - 2, c.getY(), c.getWidth() + 4, c.getHeight());
            for (Room overlapCheck : rooms) {
              if (overlapCheck.overlaps(fatC)) overlap = true;
            }
            if (!overlap) {
              c.setCorridor(Room.CorridorDirection.VERTICAL, below, above);
              corridors.add(c);
              allRooms.add(c);
            }
          }
        }
        boolean overlapX = Intersector.intersectRectangles(extendedX, toCheck, intersectionX);
        if (overlapX && intersectionX.getHeight() >= Param.CORRIDOR_SIZE) { // Enough space for a corridor
          Room left = room;
          Room right = toCheck;
          if (toCheck.getX() < room.getX()) { // Check if toCheck is left/right. It is LEFT
            left = toCheck;
            right = room;
          }
          final int corridorLength = (int) (right.getX() - (left.getX() + left.getWidth()));
          if (corridorLength <= CORRIDOR_MAX_LENGTH && Utility.prob(CORRIDOR_CHANCE)) { // It fits
            int startY = 0;
            int possibleOffset = (int) intersectionX.getHeight() - Param.CORRIDOR_SIZE;
            if (possibleOffset > 0) startY = r.nextInt(possibleOffset);
            Room c = new Room(left.getX() + left.getWidth(),
              intersectionX.getY() + startY,
              corridorLength,
              Param.CORRIDOR_SIZE);
            // Check that the corridor does not intercept any other large rooms
            boolean overlap = false;
            Room fatC = new Room(c.getX(), c.getY() - 2, c.getWidth(), c.getHeight() + 4);
            for (Room overlapCheck : rooms) {
              if (overlapCheck.overlaps(fatC)) overlap = true;
            }
            if (!overlap) {
              c.setCorridor(Room.CorridorDirection.HORIZONTAL, left, right);
              corridors.add(c);
              allRooms.add(c);
            }
          }
        }
      }
    }
  }

  private void addRoomsToTileMap() {
    for (Room room : allRooms) {
      for (int x = (int) room.x; x < (int) room.x + (int) room.width; ++x) {
        for (int y = (int) room.y; y < (int) room.y + (int) room.height; ++y) {
          if (x >= Param.TILE_X || y >= Param.TILE_Y) {
            Gdx.app.error("coord", "Invalid coordinate in [" + this + "] (" + x + "," + y + ")");
            continue;
          }
          Sprites.getInstance().getTile(x, y).setIsFloor(room);
          if (room.getIsCorridor()) Sprites.getInstance().getTile(x, y).setTexture("floorZ"); //TODO debug
        }
      }
    }
  }







}

