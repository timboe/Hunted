package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.entity.BigBad;
import timboe.hunted.entity.Chest;
import timboe.hunted.entity.Clutter;
import timboe.hunted.manager.*;

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
  private Room exitRoom;
  private Vector<Room> keyRooms;

  private final int ROOM_MEAN_SIZE = 15;
  private final int ROOM_STD_D = 5;
  private final int ROOM_BORDER_X = 2; // minimum spacing between rooms
  private final int ROOM_BORDER_Y = 4;
  private final int CORRIDOR_MAX_LENGTH = 20;
  private final int CORRIDOR_CHANCE = 100; //%

  private WorldGen() {
    r = new Random();
  }

  public Vector<Room> getAllRooms() { return allRooms; }

  public void updatePhysics(float delta) {
    for (Room room : allRooms) {
      room.updatePhysics(delta); // Smell dissipation
    }
  }

  public void generateWorld() {
    int tryN = 0;
    boolean success = false;
    while (!success && ++tryN < Param.WORLDGEN_TRIES) {
      success = tryWorld();
    }
    if (!success) {
      Gdx.app.error("WorldGen", "World generation failed");
      Gdx.app.exit();
    } else {
      Gdx.app.log("WorldGen", "World generation finished on " + tryN + " attempt.");
      GameState.getInstance().userControl = true;
    }
  }

  private boolean tryWorld() {
    reset();
    placeRooms();
    shrinkRooms();
    makeCorridors();
    removeUnconnected();
    if (!getAllConnected()) return false;
    convertCrossidorsToRooms();
    addRoomsToTileMap();
    Sprites.getInstance().crinkleEdges(rooms, corridors);
    Sprites.getInstance().disableInvisibleTiles();
    Sprites.getInstance().addTileActors(); // to-be defunct
    if (!placeExit()) return false;
    Sprites.getInstance().addTileRigidBodies();
    if (!placeBigBad()) return false;
    if (!placeKeyRooms()) return false;
    placeClutter();
    Sprites.getInstance().textureWalls();
    Sprites.getInstance().addToStage( Sprites.getInstance().getPlayer(), false );
    placeChests();
    Sprites.getInstance().addToStage( Sprites.getInstance().getBigBad(), false );
    Sprites.getInstance().addToStage( Sprites.getInstance().winMask, false );
    return true;
  }

  private void reset() {
    rooms = new Vector<Room>();
    corridors = new Vector<Room>();
    allRooms = new Vector<Room>();
    keyRooms = new Vector<Room>();
    exitRoom = null;
    nearestCentre = null;
    Physics.getInstance().reset();
    Sprites.getInstance().reset();
    GameState.getInstance().theGameScreen.reset();
    GameState.getInstance().waypoints.clear();
  }

  private void placeRooms() {
    int t = 0;
    final int minX = 1;
    final int minY = 1;
    final int maxX = Param.TILE_X - Param.MIN_ROOM_SIZE;
    final int maxY = Param.TILE_Y - Param.MIN_ROOM_SIZE;
    while (t < Param.WORLDGEN_TRIES) {
      boolean pass = true;
      long w = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      long h = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      if (w > Param.MAX_ROOM_SIZE) w = Param.MAX_ROOM_SIZE;
      if (h > Param.MAX_ROOM_SIZE) h = Param.MAX_ROOM_SIZE;
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
    // Find the room nearest the centre-bottom
    nearestCentre = null;
    float minDist = 9999f;
    final Vector2 target = new Vector2(Param.TILE_X/2, Param.TILE_Y/4);
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
    Sprites.getInstance().getBigBad().aiState = BigBad.AIState.RETURN_TO_WAYPOINT;
    return true;
  }

  private void placeChests() {
    for (Room room : rooms) {
      if (room == exitRoom) continue;
      int chests = MathUtils.clamp((int) Math.round(Math.abs(r.nextGaussian())), 0, 2);
      if (room.getConnectedRooms().size() == 1) ++chests;
      if (chests == 0) continue;
      for (int t = 0; t < Param.WORLDGEN_TRIES; ++t) {
        int rX = (int)room.getX() + r.nextInt((int)room.getWidth() - 1);
        int rY = (int)room.getY() + r.nextInt((int)room.getHeight() - 1);
        if (!Sprites.getInstance().getClear(rX,rY,1,1)) continue;
        Chest newChest = new Chest(rX, rY, false);
        Sprites.getInstance().addToStage(newChest, true);
        Sprites.getInstance().chests.add(newChest);
        if (--chests == 0) break;
      }
    }
  }

  private void placeClutter() {
    for (Room room : allRooms) {
      int clutter = MathUtils.clamp((int) Math.round(Math.abs(r.nextGaussian())), 0, 5);
      if (clutter == 0) continue;
      for (int t = 0; t < Param.WORLDGEN_TRIES; ++t) {
        int rX = (int)room.getX() + r.nextInt((int)room.getWidth() - 1);
        int rY = (int)room.getY() + r.nextInt((int)room.getHeight() - 1);
        Clutter c = new Clutter(rX,rY);
        if (!Sprites.getInstance().getClear(rX,rY,(int)c.getWidth()/Param.TILE_SIZE, (int)c.getHeight()/Param.TILE_SIZE)) continue;
        Sprites.getInstance().addToStage(c, true);
        c.setAsClutter();
        if (--clutter == 0) break;
      }
    }
  }

  private boolean placeExit() {
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
    exitRoom = entryRoomOptions.elementAt( r.nextInt(entryRoomOptions.size()) );
    Sprites.getInstance().addExitRoom(exitRoom);
    Sprites.getInstance().getPlayer().setPhysicsPosition(exitRoom.x + exitRoom.width/2f - .5f,
      exitRoom.y + exitRoom.height - 3);
    Sprites.getInstance().getPlayer().setMoveDirection(3f*Math.PI/2f);
    Sprites.getInstance().getPlayer().updatePosition();
    GameState.getInstance().theGameScreen.gameCamera.centreOnPlayer(true);
    return true;
  }

  public boolean placeKeyRooms() {
    final int exclusionDist = Math.min(Param.TILE_X, Param.TILE_Y) / 4; // Rooms must be far apart
    final Vector2 entryRoomPos = exitRoom.getPosition(new Vector2());
    int attempt = 0;
    Vector<Room> possibleRooms = new Vector<Room>();
    for (Room room : rooms) {
      if (room.getConnectedRooms().size() > 2) continue; // Room can have at most two connections
      if (room.width < 10 || room.height < 10) continue; // Room has to be large enough // TODO magic numbers
      possibleRooms.add(room);
    }
    if (possibleRooms.size() < Param.KEY_ROOMS) return false;
    while (keyRooms.size() != Param.KEY_ROOMS && ++attempt < Param.WORLDGEN_TRIES) {
      Room room = possibleRooms.get( Utility.r.nextInt(possibleRooms.size()) );
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
    Sprites.getInstance().addKeyShrine((int)keyLoc.x, (int)keyLoc.y, shrineN, room);
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


  private void mergeCorridors(Room C1, Room C2, Room newRoom) {
    // Get the 4 rooms these two corridors link
    Vector<Room> C1Rooms = C1.getConnectedRooms();
    Vector<Room> C2Rooms = C2.getConnectedRooms();
    // Unlink rooms
    for (Room C1Room : C1Rooms) {
      for (Room C2Room : C2Rooms) {
        C1Room.removeRoomLink( C2Room );
        C2Room.removeRoomLink( C1Room);
      }
    }
    // Re-link via new room
    // TODO form four new corridors and link them
//    possibleCorridors.get(id).setCorridor(Room.CorridorDirection.HORIZONTAL, left, right);
//    corridors.add(possibleCorridors.get(id));
//    allRooms.add(possibleCorridors.get(id));
  }

  private void convertCrossidorsToRooms() {
    return; // TODO
    // It's possible for corridors to cross. When this happens we can convert the crossing points to mini-rooms
//    boolean madeChange = false;
//    do {
//      madeChange = false;
//      for (Room C1 : corridors) {
//        for (Room C2 : corridors) {
//          if (C1 == C2) continue;
//          // Check for overlap
//          Room overlapRoom = new Room(0, 0, 0, 0);
//          boolean overlap = Intersector.intersectRectangles(C1, C2, overlapRoom);
//          if (overlap) {
//            mergeCorridors(C1, C2, overlapRoom);
//            madeChange = true;
//            break;
//          }
//        }
//        if (madeChange) break;
//      }
//    } while (madeChange);
  }

  private void makeCorridors() { //TODO make one loop rather than two
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
            int possibleOffset = (int) intersectionY.getWidth() - Param.CORRIDOR_SIZE;
            // Check possible offsets
            Vector<Room> possibleCorridors = new Vector<Room>();
            for (int startX = 0; startX <= possibleOffset; ++startX) {
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
              if (!overlap) possibleCorridors.add(c);
            }
            if (possibleCorridors.size() > 0) {
              int id = r.nextInt(possibleCorridors.size());
              possibleCorridors.get(id).setCorridor(Room.CorridorDirection.VERTICAL, below, above);
              corridors.add(possibleCorridors.get(id));
              allRooms.add(possibleCorridors.get(id));
            }
          }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
            int possibleOffset = (int) intersectionX.getHeight() - Param.CORRIDOR_SIZE;
            // Check possible offsets
            Vector<Room> possibleCorridors = new Vector<Room>();
            for (int startY = 0; startY <= possibleOffset; ++startY) {
              Room c = new Room(left.getX() + left.getWidth(),
                intersectionX.getY() + startY,
                corridorLength,
                Param.CORRIDOR_SIZE);
              // Check that the corridor does not intercept any other large rooms
              boolean overlap = false;
              Room fatC = new Room(c.getX(), c.getY() - 3, c.getWidth(), c.getHeight() + 6);
              for (Room overlapCheck : rooms) {
                if (overlapCheck.overlaps(fatC)) overlap = true;
              }
              if (!overlap) possibleCorridors.add(c);
            }
            if (possibleCorridors.size() > 0) {
              int id = r.nextInt(possibleCorridors.size());
              possibleCorridors.get(id).setCorridor(Room.CorridorDirection.HORIZONTAL, left, right);
              corridors.add(possibleCorridors.get(id));
              allRooms.add(possibleCorridors.get(id));
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
        }
      }
    }
  }

}

