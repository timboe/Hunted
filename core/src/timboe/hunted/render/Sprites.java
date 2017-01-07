package timboe.hunted.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.entity.BigBad;
import timboe.hunted.entity.ParticleEffectActor;
import timboe.hunted.entity.Player;
import timboe.hunted.entity.Tile;
import timboe.hunted.world.Physics;
import timboe.hunted.world.Room;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Tim on 28/12/2016.
 */
public class Sprites {
  private static Sprites ourInstance = new Sprites();

  public static Sprites getInstance() {
    return ourInstance;
  }

  private Group tileSet;
  private HashMap<Integer, Tile> tileMap;
  private HashSet<ParticleEffectActor> particles;
  private Player player;
  private BigBad bigBad;
  public Tile entry;

  private Sprites() {
  }

  public void reset() {
    player = new Player();

    bigBad = new BigBad();
    tileSet = new Group();

    particles = new HashSet<ParticleEffectActor>();
    tileMap = new HashMap<Integer, Tile>();
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        tileMap.put(Utility.xyToID(x, y), new Tile(x, y));
      }
    }
  }

  public void addToStage(Actor a) {
    tileSet.addActor(a);
  }

  public void addTileActors() {
    tileSet.clearChildren();
    for (int x = 0; x < Param.TILE_X; ++x) {
      for (int y = 0; y < Param.TILE_Y; ++y) {
        Tile t = tileMap.get(Utility.xyToID(x, y));
        if (t.isVisible() == true) tileSet.addActor(t);
      }
    }
  }

  public void addEntryRoom(Room entryRoom) {
    final int xStart = (int)(entryRoom.x + entryRoom.width/2 - 1);
    final int yStart = (int)(entryRoom.y + entryRoom.height);
    for (int x = xStart; x < xStart + 3; ++x) {
      getTile(x, yStart).setVisible(false);
    }
    player.setPhysicsPosition(entryRoom.x + entryRoom.width/2f, entryRoom.y + entryRoom.height/2f);
    Tile t = new Tile(xStart, (int)(entryRoom.y + entryRoom.height));
    t.setTexture("entry",5);
    entry = t;
    addToStage(t);
    getTile(xStart + 0, yStart - 1).setTexture("blobRed",2);
    getTile(xStart + 1, yStart - 1).setTexture("blobGreen",2);
    getTile(xStart + 2, yStart - 1).setTexture("blobBlue",2);
    getTile(xStart + 1, yStart - 2).setTexture("switch",7);
    Tile torchA = new Tile(xStart - 1, yStart - 2);
    Tile torchB = new Tile(xStart + 3, yStart - 2);
    torchA.setTexture("torchTall");
    torchB.setTexture("torchTall");
    addToStage(torchA);
    addToStage(torchB);
    Physics.getInstance().addTorch(xStart + -.5f, yStart - .8f, .5f).doCollision();
    Physics.getInstance().addTorch(xStart + 3.5f, yStart - .8f, .5f).doCollision();
  }


    public void addKeyShrine(int x, int y, int n) {
    Tile shrine = new Tile(x + 1, y + 1);
    Tile lightA = new Tile(x, y + 1);
    Tile lightB = new Tile(x + 3, y + 1);
    String colour = new String();
    switch (n) {
      case 0: colour = "Red"; break;
      case 1: colour = "Green"; break;
      case 2: colour = "Blue"; break;
      default:Gdx.app.error("Sprites::addKeyShrine","FATAL n = " + n); Gdx.app.exit();
    }
    shrine.setTexture(Utility.prob(.5f) ? "totemA" + colour : "totemB" + colour, 2);
    getTile(x + 2, y).setTexture("blob" + colour, 2);
    lightA.setTexture("lamp" + colour,2);
    lightB.setTexture("lamp" + colour,2);
    getTile(x + 1, y).setTexture("switch", 7);
    Tile torchA = new Tile(x, y + 2);
    Tile torchB = new Tile(x + 3, y + 2);
    torchA.setTexture("torchTall");
    torchB.setTexture("torchTall");
    addToStage(torchA);
    addToStage(torchB);
    addToStage(lightA);
    addToStage(lightB);
    addToStage(shrine);
    Physics.getInstance().addTorch(x + 3.5f, y + 3.2f, .5f).doCollision();
    Physics.getInstance().addTorch(x + .5f, y + 3.2f, .5f).doCollision();

  }

  public void addFlameEffect(Vector2 position) {
    ParticleEffect effect = new ParticleEffect();
    effect.load(Gdx.files.internal("flame.p"), Textures.getInstance().getAtlas());
    effect.scaleEffect(0.2f);
    effect.start();
    ParticleEffectActor PEA = new ParticleEffectActor(effect);
    PEA.setPosition(position.x * Param.TILE_SIZE, position.y * Param.TILE_SIZE);
    tileSet.addActor(PEA);
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
      else map.put(id, Sprites.getInstance().getTile(cX, cY).getIsFloor());
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
          Physics.getInstance().addTorch(x + .25f, y + 1.3f, x + .85f, y + 1.3f, 1.75f, true, 0f);
        } else if (torch && y%Param.TORCH_SPACING==0 && f.get("W") && !f.get("N") && !f.get("S") && f.get("NW") // EAST TORCH
          && (y+3 >= Param.TILE_Y || !getTile(x,y+3).getIsFloor()) ) { //TODO horrid condition
          t.setTexture("wallETorch");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
          Physics.getInstance().addTorch(x + .75f, y + 1.3f, x + .15f, y + 1.3f, 1.75f, true, (float)Math.PI);
        } else if (torch && x%Param.TORCH_SPACING==0 && f.get("N") && !f.get("E") && !f.get("W")) { // SOUTH TORCH
          t.setTexture("wallSTorch");
          Physics.getInstance().addTorch(x + .5f, y + .25f, x + .5f, y + .85f, 1.75f, true, (float)Math.PI/2f);
        } else if (torch && x%Param.TORCH_SPACING==0 && f.get("S") && !f.get("E") && !f.get("W")) { // NORTH TORCH
          if (rnd < .5f) t.setTexture("wallNTorchA");
          else t.setTexture("wallNTorchB");
          getTile(x, y+1).setVisible(false); // DOUBLE-TILE
          Physics.getInstance().addTorch(x + .5f, y + 1.2f, x + .5f, y + 1.f, 2.2f, true, (float)Math.PI*3f/2f);
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

  public Group getTileSet() {
    return tileSet;
  }

  public Tile getTile(int x, int y) {
    return tileMap.get(Utility.xyToID(x,y));
  }

  public void dispose() {
    tileSet.clearChildren();
    tileMap.clear();
  }
}
