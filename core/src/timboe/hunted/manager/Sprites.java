package timboe.hunted.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.entity.*;
import timboe.hunted.pathfinding.PathFinding;
import timboe.hunted.world.Room;
import timboe.hunted.world.WorldGen;

import java.util.*;

/**
 * Created by Tim on 28/12/2016.
 */
public class Sprites {
  private static Sprites ourInstance = new Sprites();

  public static Sprites getInstance() {
    return ourInstance;
  }

  private HashMap<Integer, Tile> tileMap;
  private HashSet<ParticleEffectActor> particles;
  private Player player;
  private BigBad bigBad;
  public ExitDoor exitDoor;
  public Tile[] keySwitch = new Tile[Param.KEY_ROOMS + 1];
  public Vector<Tile> toUpdateWeb;
  public HashSet<Tile> webTiles;
  private HashSet<EntityBase> clutter;
  public Stage stage;

  private Sprites() {
  }

  public void reset() {
    player = new Player();

    bigBad = new BigBad();
    webTiles = new HashSet<Tile>();
    toUpdateWeb = new Vector<Tile>();
    clutter = new HashSet<EntityBase>();

    exitDoor = null;
    for (int i = 0; i < Param.KEY_ROOMS + 1; ++i) keySwitch[i] = null;

    particles = new HashSet<ParticleEffectActor>();
    tileMap = new HashMap<Integer, Tile>();
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        tileMap.put(Utility.xyToID(x, y), new Tile(x, y));
      }
    }
  }

  public void resetWeb() {
    for (Tile t : webTiles) t.webEffect = 0;
  }

  public boolean tintWeb() {
    boolean active = false;
    for (Tile t : webTiles) active |= t.tintWeb();
    return active;
  }

  public void addPlayers() {
    stage.addActor(player);
    stage.addActor(bigBad);
  }

  public void moveWeb() {
    for (Tile t : webTiles) t.moveWeb();
  }

  public void addToStage(EntityBase a, boolean isClutter) {
    stage.addActor(a);
    if (isClutter) clutter.add(a);
  }

  public void addTileActors() {
    stage.clear();
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        Tile t = tileMap.get(Utility.xyToID(x, y));
        if (t.isVisible() == true) stage.addActor(t);
      }
    }
  }

  public void updatePhysics() {
    player.updatePhysics();
    bigBad.updatePhysics();
    for (int i = 0; i < toUpdateWeb.size(); ++i) { // Note can only use basic iteration as we modify these mid-loop
      toUpdateWeb.get(i).updateNeighbours(false);
    }
    toUpdateWeb.clear();

    if (GameState.getInstance().webEffect) {
      if (GameState.getInstance().frame % Param.ANIM_SPEED/4 == 0) moveWeb();
      if (!tintWeb()) GameState.getInstance().webEffect = false; // Stop
    }
  }

  public void updatePosition() {
    player.updatePosition();
    bigBad.updatePosition();
  }


  public void addExitRoom(Room entryRoom) {
    final int xStart = (int)(entryRoom.x + entryRoom.width/2 - 1);
    final int yStart = (int)(entryRoom.y + entryRoom.height);
    for (int x = xStart; x < xStart + 3; ++x) {
      getTile(x, yStart).setVisible(false);
    }
    ExitDoor t = new ExitDoor(xStart, (int)(entryRoom.y + entryRoom.height));
    t.setTexture("entry",5);
    exitDoor = t;
    addToStage(t, false);
    getTile(xStart + 0, yStart - 1).setTexture("blobRed",2);
    getTile(xStart + 1, yStart - 1).setTexture("blobGreen",2);
    getTile(xStart + 2, yStart - 1).setTexture("blobBlue",2);
    clutter.add(getTile(xStart + 0, yStart - 1));
    clutter.add(getTile(xStart + 1, yStart - 1));
    clutter.add(getTile(xStart + 2, yStart - 1));
    getTile(xStart + 0, yStart - 1).activationID = 1;
    getTile(xStart + 1, yStart - 1).activationID = 2;
    getTile(xStart + 2, yStart - 1).activationID = 3;
    keySwitch[0] = getTile(xStart + 1, yStart - 2);
    keySwitch[0].setTexture("switch",7);
    keySwitch[0].addSwitchSensor(0);
    clutter.add(keySwitch[0]);
    Tile torchA = new Tile(xStart - 1, yStart - 2);
    Tile torchB = new Tile(xStart + 3, yStart - 2);
    torchA.setAsPhysicsBody(xStart - 1 + .35f, yStart - 2, .3f, 1.2f);
    torchB.setAsPhysicsBody(xStart + 3 + .35f, yStart - 2, .3f, 1.2f);;
    torchA.setTexture("torchTall");
    torchB.setTexture("torchTall");
    addToStage(torchA, true);
    addToStage(torchB, true);
    Physics.getInstance().addTorch(xStart + -.5f, yStart - .8f).doCollision();
    Physics.getInstance().addTorch(xStart + 3.5f, yStart - .8f).doCollision();
  }


    public void addKeyShrine(int x, int y, int n, Room r) {
    Tile shrine = new Tile(x + 1, y + 1);
    Tile lightA = new Tile(x, y + 1);
    Tile lightB = new Tile(x + 3, y + 1);
    Tile torchA = new Tile(x, y + 2);
    Tile torchB = new Tile(x + 3, y + 2);
    shrine.setAsPhysicsBody(x + 1.5f, y + 1.25f, 1f, 2.5f);
    lightA.setAsPhysicsBody(x + 0.2f, y + 1.1f, .6f, .8f);
    lightB.setAsPhysicsBody(x + 3.2f, y + 1.1f, .6f, .8f);;
    String colour = new String();
    switch (n) {
      case 0: colour = "Red"; break;
      case 1: colour = "Green"; break;
      case 2: colour = "Blue"; break;
      default:Gdx.app.error("Sprites::addKeyShrine","FATAL n = " + n); Gdx.app.exit();
    }
    shrine.setTexture(Utility.prob(.5f) ? "totemA" + colour : "totemB" + colour, 3, true);
    getTile(x + 2, y).setTexture("blob" + colour, 2);
    getTile(x + 2, y).activationID = n+1;
    clutter.add( getTile(x+2,y));
    shrine.activationID = n+1;
    lightA.activationID = n+1;
    lightB.activationID = n+1;
    keySwitch[n+1] = getTile(x + 1, y);
    keySwitch[n+1].setTexture("switch", 7);
    keySwitch[n+1].addSwitchSensor(n+1);
    clutter.add(keySwitch[n+1]);
    torchA.setTexture("torchTall");
    torchB.setTexture("torchTall");
    torchA.setAsPhysicsBody(x + .35f, y + 2, .3f, 1.2f);
    torchB.setAsPhysicsBody(x + 3 + .35f, y + 2, .3f, 1.2f);
    lightA.setTexture("lamp" + colour,3, true);
    lightB.setTexture("lamp" + colour,3, true);
    addToStage(torchA, true);
    addToStage(torchB, true);
    addToStage(lightA, true);
    addToStage(lightB, true);
    addToStage(shrine, true);
    Physics.getInstance().addTorch(x + 3.5f, y + 3.2f).doCollision();
    Physics.getInstance().addTorch(x + .5f, y + 3.2f).doCollision();
    for (int i = 0; i < Utility.r.nextInt(Param.MAX_MINI_LIGHT); ++i) {
      int rX = (int)r.getX() + Utility.r.nextInt((int)r.getWidth()-1);
      int rY = (int)r.getY() + Utility.r.nextInt((int)r.getHeight()-1);
      if (getClear(rX,rY,1,1)) {
        Tile miniTorch = new Tile(rX, rY);
        miniTorch.setTexture("lampS" + colour, 3, true);
        miniTorch.activationID = n+1;
        addToStage(miniTorch, true);
      }
    }
  }

  public boolean getClear(int x, int y, int w, int h) {
    Tile t = getTile(x,y);
    if (!t.getIsFloor()) return false;
    Room r = t.getTilesRoom();
    Vector<Room> connectedCorridors = r.getCorridors();
    Rectangle rect = new Rectangle(x, y, w, h);
    for (Room corridor : connectedCorridors ) { // Check enemy pathing overlap
      if (rect.overlaps(corridor.corridorProjection)) return false;
    }
    Rectangle cluttertangle = new Rectangle();
    for (EntityBase c : clutter) { // Check existing clutter overlap
      cluttertangle.set(c.getX() / Param.TILE_SIZE,
        c.getY() / Param.TILE_SIZE,
        c.getWidth() / Param.TILE_SIZE,
        c.getHeight() / Param.TILE_SIZE);
      if (rect.overlaps(cluttertangle)) return false;
    }
    return true;
  }


  public void addFlameEffect(Vector2 position) {
    ParticleEffectActor PEA = new ParticleEffectActor(Utility.getNewFlameEffect());
    PEA.setPosition((position.x - .5f) * Param.TILE_SIZE, (position.y - .5f) * Param.TILE_SIZE);
    stage.addActor(PEA);
    particles.add(PEA);
  }

  // TODO don't need isVisible here but it helps with the lighting
  private boolean canIncludeInRigidBody(Tile t, boolean incInvisible) {
    if (incInvisible) return (!t.getIsFloor() && !t.getHasPhysics());
    else return (t.isVisible() && !t.getIsFloor() && !t.getHasPhysics());
  }

  private Vector2 expandRigidBody(final int x, final int y, boolean incInvisible) {
    Vector2 size = new Vector2(1,1);
    int xNew = x + 1;
    while (xNew < Param.TILE_X) {
      Tile t = getTile(xNew, y);
      if (canIncludeInRigidBody(t, incInvisible)) {
        size.x += 1;
        ++xNew;
      } else {
        break;
      }
    }
    int yNew = y + 1;
    while (yNew < Param.TILE_Y) {
      boolean canExpand = true;
      for (int cX = x; cX < x + size.x; ++cX) {
        Tile t = getTile(cX, yNew);
        if (!canIncludeInRigidBody(t, incInvisible)) canExpand = false;
      }
      if (canExpand) {
        size.y += 1;
        ++yNew;
      } else {
        break;
      }
    }
    return size;
  }

    public void addTileRigidBodies() {
      //addTileRigidBodies(false);
      addTileRigidBodies(true);
    }

    public void addTileRigidBodies(boolean incInvisible) {
    int count = 0;
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        Tile t = getTile(x, y); // Find a solid tile
        if (canIncludeInRigidBody(t, incInvisible)) {
          Vector2 size = expandRigidBody(x, y, incInvisible);
          t.setAsPhysicsBody(size.x, size.y);
          ++count;
        }
      }
    }
    Gdx.app.log("Sprites", "Invisible="+incInvisible+" required " + count + " rigid bodies");
  }

  public HashSet<Tile> getNeighbourWeb(final int x, final int y, HashSet<Tile> returnSet, boolean recurse) {
//    Gdx.app.log("getN", "x "+x+" y " + y );
    for (int cX = x - 1; cX < x + 2; ++cX) {
      Tile t = getTile(cX, y);
      if (t.getIsWeb()) {
        returnSet.add(t);
        if (recurse) toUpdateWeb.add(t);
      }
    }
    for (int cY = y - 1; cY < y + 2; ++cY) {
      Tile t = getTile(x, cY);
      if (t.getIsWeb()) {
        returnSet.add(t);
        if (recurse) toUpdateWeb.add(t);
      }
    }
    return returnSet;
  }

  public void crinkleEdges(Vector<Room> rooms, Vector<Room> corridors) {
    // remove some corner blocks
    for (Room r : rooms) {
      for (int corner = 0; corner < 4; ++corner) {
        for (int dirn = 0; dirn < 2; ++dirn) {
          int x = (int) r.getX();
          int y = (int) r.getY();
          int extent = MathUtils.clamp((int) Math.round(Math.abs(Utility.r.nextGaussian())), 0, Param.MAX_CRINKLE);
          switch (corner) {
            case 0: // bot left
              break;
            case 1: //bot right
              x += r.getWidth() - 1;
              break;
            case 2: // top right
              x += r.getWidth() - 1;
              y += r.getHeight() - 1;
              break;
            case 3: // bot right
              y += r.getHeight() - 1;
              break;
          }
          // Check good corner
//          Gdx.app.log("dbg","Room="+r+" x=" + x + " y=" + y + " e=" + extent);
          if      (corner == 0 && (getTile(x - 1, y).getIsFloor() || getTile(x, y - 1).getIsFloor())) break;
          else if (corner == 1 && (getTile(x + 1, y).getIsFloor() || getTile(x, y - 1).getIsFloor())) break;
          else if (corner == 2 && (getTile(x + 1, y).getIsFloor() || getTile(x, y + 1).getIsFloor())) break;
          else if (corner == 3 && (getTile(x - 1, y).getIsFloor() || getTile(x, y + 1).getIsFloor())) break;
          // Try and extend
          for (int e = 0; e < extent; ++e) {
            if        (corner == 0 && dirn == 0 && !getTile(x + e, y - 1).getIsFloor()) { // Bot left - Check below
              getTile(x + e, y).setIsDirt();
            } else if (corner == 0 && dirn == 1 && !getTile(x - 1, y + e).getIsFloor()) { // bot left - check left
              getTile(x, y + e).setIsDirt();
            } else if (corner == 1 && dirn == 0 && !getTile(x - e, y - 1).getIsFloor()) { // bot right, check below
              getTile(x - e, y).setIsDirt();
            } else if (corner == 1 && dirn == 1 && !getTile(x + 1, y + e).getIsFloor()) { // bot right, check right
              getTile(x, y + e).setIsDirt();
            } else if (corner == 2 && dirn == 0 && !getTile(x - e, y + 1).getIsFloor()) { // top right, check up
              getTile(x-e, y).setIsDirt();
            } else if (corner == 2 && dirn == 1 && !getTile(x + 1, y - e).getIsFloor()) { // top right, check right
              getTile(x, y - e).setIsDirt();
            } else if (corner == 3 && dirn == 0 && !getTile(x + e, y + 1).getIsFloor()) { // top left, check up
              getTile(x + e, y).setIsDirt();
            } else if (corner == 3 && dirn == 1 && !getTile(x - 1, y - e).getIsFloor()) { // top left, check left
              getTile(x, y - e).setIsDirt();
            } else {
              break;
            }
          }
        }
      }
    }
    // remove some corridor blocks
    //TODO de-ugly
    final int hGap = 3;
    final int vGap = 2;
    for (Room c : corridors) {
      int extent = MathUtils.clamp((int) Math.round(Math.abs(Utility.r.nextGaussian())), 1, Param.MAX_CRINKLE);
      int x = (int)c.getX();
      int y = (int)c.getY();
      int w = (int)c.getWidth() - 1;
      int h = (int)c.getHeight() - 1;
      if (c.getX() < Param.MAX_CRINKLE + 1 || c.getY() < Param.MAX_CRINKLE + 1) continue;
      if (c.getCorridorDirection() == Room.CorridorDirection.VERTICAL) {
        if (c.getHeight() < Param.CORRIDOR_SIZE + vGap) continue;
        for (int corner = 0; corner < 4; ++ corner) {
          for (int e = 1; e <= extent; ++e) {
//            Gdx.app.log("dbg","Room="+c+" x=" + x + " y=" + y + " e=" + e);
            if        (corner == 0 && getTile(x - e - vGap + 1, y).getIsDirt()
              && getTile(x - e - vGap, y).getIsDirt()
              && getTile(x - e - vGap, y - 1).getIsFloorNC() ) {
              getTile(x - e, y).setIsFloor(c); // Bot left, going left
            } else if (corner == 1 && getTile( x + w + e + vGap - 1, y).getIsDirt()
              && getTile( x + w + e + vGap, y).getIsDirt()
              && getTile(x + w + e + vGap, y - 1).getIsFloorNC()) {
              getTile(x + w + e, y).setIsFloor(c); // Bot right, going right
            } else if (corner == 2 && getTile(x + w + e + vGap - 1, y + h).getIsDirt()
              && getTile(x + w + e + vGap, y + h).getIsDirt()
              && getTile(x + w + e + vGap, y + h + 1).getIsFloorNC()) {
              getTile(x + w + e, y + h).setIsFloor(c); // Top right going right
            } else if (corner == 3 && getTile(x - e - vGap + 1, y + h).getIsDirt()
              && getTile(x - e - vGap, y + h).getIsDirt()
              && getTile(x - e - vGap, y + h + 1).getIsFloorNC()) {
              getTile(x - e, y + h).setIsFloor(c); // Top left, going left
            } else {
              break;
            }
          }
        }
      } else if (c.getCorridorDirection() == Room.CorridorDirection.HORIZONTAL) {
        if (c.getWidth() < Param.CORRIDOR_SIZE) continue;
        for (int corner = 0; corner < 4; ++ corner) {
          for (int e = 1; e <= extent; ++e) {
//            Gdx.app.log("dbg","Room="+c+" x=" + x + " y=" + y + " e=" + e);
            if        (corner == 0 && getTile(x, y - e - hGap + 1).getIsDirt() && getTile(x, y - e - hGap).getIsDirt() && getTile(x - 1, y - e - hGap).getIsFloorNC() ) {
              getTile(x, y - e).setIsFloor(c); // Bot left, going down
            } else if (corner == 1 && getTile( x + w, y - e - hGap + 1).getIsDirt() && getTile( x + w, y - e - hGap).getIsDirt() && getTile(x + w + 1, y - e - hGap).getIsFloorNC()) {
              getTile(x + w, y - e).setIsFloor(c); // Bot right, going dowm
            } else if (corner == 2 && getTile(x + w, y + h + e + hGap - 1).getIsDirt() && getTile(x + w, y + h + e + hGap).getIsDirt() && getTile(x + w + 1, y + h + e + hGap).getIsFloorNC()) {
              getTile(x + w, y + h + e).setIsFloor(c); // Top right going up
            } else if (corner == 3 && getTile(x, y + h + e + hGap - 1).getIsDirt() && getTile(x, y + h + e + hGap).getIsDirt() && getTile(x - 1, y + h + e + hGap).getIsFloorNC()) {
              getTile(x, y + h + e).setIsFloor(c); // Top left, going up
            } else {
              break;
            }
          }
        }
      }
    }
  }


  public LinkedList<Tile> findPath(Tile start, Tile end) {
    return PathFinding.doAStar(start, end);
  }

  private void getNeighbourFloor(final int x, final int y, HashMap<String, Boolean> map) {
    map.clear(); // Should not be needed
    for (int d = 0; d < 8; ++d) {
      int cX = x;
      int cY = y;
      String id = "";
      switch (d) {
        case 0:
          ++cY;
          id = "N";
          break;
        case 1:
          ++cX;
          id = "E";
          break;
        case 2:
          --cY;
          id = "S";
          break;
        case 3:
          --cX;
          id = "W";
          break;
        case 4:
          ++cX;
          ++cY;
          id = "NE";
          break;
        case 5:
          ++cX;
          --cY;
          id = "SE";
          break;
        case 6:
          --cX;
          ++cY;
          id = "NW";
          break;
        case 7:
          --cX;
          --cY;
          id = "SW";
          break;
      }
      if (cX >= Param.TILE_X || cY >= Param.TILE_Y || cX < 0 || cY < 0) map.put(id, Boolean.FALSE);
      else map.put(id, getTile(cX, cY).getIsFloor());
    }
  }

  public void disableInvisibleTiles() {
    HashMap<String, Boolean> floorMap = new HashMap<String, Boolean>();
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        getNeighbourFloor(x, y, floorMap);
        if (!floorMap.containsValue(Boolean.TRUE)) {
          getTile(x, y).setVisible(false);
        }
      }
    }
  }

  public void textureWalls() {
    HashMap<String, Boolean> f = new HashMap<String, Boolean>();
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        final float rnd = Utility.r.nextFloat();
        boolean torch = Utility.prob(Param.TORCH_CHANCE);
        Tile t = getTile(x, y);
        if (t.getIsFloor() || !t.isVisible()) continue;
        getNeighbourFloor(x, y, f);
        // Assign Tiles
        if (f.get("NE") && !f.get("N") && !f.get("E") && !f.get("S") && !f.get("W")) { // SW OUTER CORNER
          t.setTexture("wallSW");
        } else if (f.get("NW") && !f.get("N") && !f.get("E") && !f.get("S") && !f.get("W")) { // SE OUTER CORNER
          t.setTexture("wallSE");
        } else if (f.get("SW") && !f.get("N") && !f.get("E") && !f.get("S") && !f.get("W")) { // NW OUTER CORNER
          t.setTexture("wallNW");
          getTile(x, y + 1).setVisible(false); // DOUBLE-TILE
          ///////////////////
          ///////////////////
        } else if (f.get("N") && f.get("NW") && f.get("W") && !f.get("S") && !f.get("E") && !f.get("SW")) { // NW INNER CORNER TO W WALL
          // Note - we actually set the tile BELOW us
          getTile(x, y-1).setTexture("wallInnerNWConnectW"); // DOUBLE-TILE
          t.setVisible(false); // set ME invisible
        } else if (f.get("N") && f.get("NW") && f.get("W") && !f.get("S") && !f.get("E")) { // NW INNER CORNER
          // Note - we actually set the tile BELOW us
          getTile(x, y-1).setTexture("wallInnerNW"); // DOUBLE-TILE
          t.setVisible(false); // set ME invisible
        } else if (f.get("N") && f.get("NE") && f.get("E") && !f.get("S") && !f.get("W") && !f.get("SE")) { // NE INNER CORNER TO E WALL
          // Note - we actually set the tile BELOW us
          getTile(x, y-1).setTexture("wallInnerNEConnectE"); // DOUBLE-TILE
          t.setVisible(false); // set ME invisible
        } else if (f.get("N") && f.get("NE") && f.get("E") && !f.get("S") && !f.get("W")) { // NE INNER CORNER
          // Note - we actually set the tile BELOW us
          getTile(x, y-1).setTexture("wallInnerNE"); // DOUBLE-TILE
          t.setVisible(false); // set ME invisible
        } else if (f.get("W") && f.get("SW") && f.get("S") && !f.get("N") && !f.get("E") && !f.get("NW")) { // SW INNER CORNER TO W WALL
          t.setTexture("wallInnerSWConnectW");
          getTile(x, y + 1).setVisible(false); // TRIPLE-TILE
          getTile(x, y + 2).setVisible(false); // TRIPLE-TILE
        } else if (f.get("W") && f.get("SW") && f.get("S") && !f.get("N") && !f.get("E")) { // SW INNER CORNER
          t.setTexture("wallInnerSW");
          getTile(x, y + 1).setVisible(false); // DOUBLE-TILE
        } else if (f.get("E") && f.get("SE") && f.get("S") && !f.get("N") && !f.get("W") && !f.get("NE")) { // SE INNER CORNER TO E WALL
          t.setTexture("wallInnerSEConnectE");
          getTile(x, y + 1).setVisible(false); // TRIPLE-TILE
          getTile(x, y + 2).setVisible(false); // TRIPLE-TILE
        } else if (f.get("E") && f.get("SE") && f.get("S") && !f.get("N") && !f.get("W")) { // SE INNER CORNER
          t.setTexture("wallInnerSE");
          getTile(x, y + 1).setVisible(false); // DOUBLE-TILE
        } else if (f.get("SE") && !f.get("N") && !f.get("E") && !f.get("S") && !f.get("W")) { // NW OUTER CORNER
          t.setTexture("wallNE");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
          ///////////////////
          ///////////////////
        } else if (torch && y%Param.TORCH_SPACING==0 && f.get("E") && !f.get("N") && !f.get("S") && f.get("NE") // WEST TORCH
          && (y+3 >= Param.TILE_Y || !getTile(x,y+3).getIsFloor()) ) { //TODO horrid condition
          t.setTexture("wallWTorch");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
          Physics.getInstance().addTorch(x + .25f, y + 1.3f, x + .85f, y + 1.3f, x + 1f, y + 1.3f, true, 0f, Param.WALL_FLAME_CAST_ESW);
        } else if (torch && y%Param.TORCH_SPACING==0 && f.get("W") && !f.get("N") && !f.get("S") && f.get("NW") // EAST TORCH
          && (y+3 >= Param.TILE_Y || !getTile(x,y+3).getIsFloor()) ) { //TODO horrid condition
          t.setTexture("wallETorch");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
          Physics.getInstance().addTorch(x + .75f, y + 1.3f, x + .15f, y + 1.3f, x, y + 1.3f, true, (float)Math.PI, Param.WALL_FLAME_CAST_ESW);
        } else if (torch && x%Param.TORCH_SPACING==0 && f.get("N") && !f.get("E") && !f.get("W")) { // SOUTH TORCH
          t.setTexture("wallSTorch");
          Physics.getInstance().addTorch(x + .5f, y + .25f, x + .5f, y + .85f, x + .5f, y + 1f, true, (float)Math.PI/2f, Param.WALL_FLAME_CAST_ESW);
        } else if (torch && x%Param.TORCH_SPACING==0 && f.get("S") && !f.get("E") && !f.get("W")) { // NORTH TORCH
          if (rnd < .5f) t.setTexture("wallNTorchA");
          else t.setTexture("wallNTorchB");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
          Physics.getInstance().addTorch(x + .5f, y + 1.2f, x + .5f, y + 1.f, x + .5f, y, true, (float)Math.PI*3f/2f, Param.WALL_FLAME_CAST_N);
          ///////////////////
          ///////////////////
        } else if (f.get("E") && !f.get("N") && !f.get("S")) { // WEST WALL
          if (rnd < .8f) t.setTexture("wallWA");
          else if (rnd < .9f) t.setTexture("wallWB");
          else t.setTexture("wallWC");
        } else if (f.get("W") && !f.get("N") && !f.get("S")) { // EAST WALL
          if (rnd < .8f) t.setTexture("wallEA");
          else if (rnd < .9f) t.setTexture("wallEB");
          else t.setTexture("wallEC");
        } else if (f.get("N") && !f.get("E") && !f.get("W")) { // SOUTH WALL
          t.setTexture("wallS");
        } else if (f.get("S") && !f.get("E") && !f.get("W")) { // NORTH WALL
          if (rnd < .8f) t.setTexture("wallNA");
          else t.setTexture("wallNB");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
        } else {
          Gdx.app.error("Sprites","Painting error at " + x + "," + y);
        }
      }
    }
  }

  public Player getPlayer() {
    return player;
  }

  public BigBad getBigBad() { return  bigBad; }

  public Tile getTile(int x, int y) {
    return tileMap.get(Utility.xyToID(x,y));
  }

  public void dispose() {
    tileMap.clear();
  }
}
