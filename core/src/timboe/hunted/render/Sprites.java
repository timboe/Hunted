package timboe.hunted.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Group;
import timboe.hunted.HuntedGame;
import timboe.hunted.entity.BigBad;
import timboe.hunted.entity.Player;
import timboe.hunted.entity.Tile;

import java.util.HashMap;

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
  private Player player;
  private BigBad bigBad;

  private Sprites() {
    reset();
  }

  public void reset() {
    player = new Player();
    bigBad = new BigBad();
    tileSet = new Group();

    tileMap = new HashMap<Integer, Tile>();
    for (int x = 0; x < HuntedGame.TILE_X; ++x) {
      for (int y = 0; y < HuntedGame.TILE_Y; ++y) {
        tileMap.put(HuntedGame.xyToID(x, y), new Tile(x, y));
      }
    }
  }

  public void addTileActors() {
    tileSet.clearChildren();
    for (int x = 0; x < HuntedGame.TILE_X; ++x) {
      for (int y = 0; y < HuntedGame.TILE_Y; ++y) {
        Tile t = tileMap.get(HuntedGame.xyToID(x, y));
        if (t.isVisible() == true) tileSet.addActor(t);
      }
    }
  }

  private boolean canIncludeInRigidBody(Tile t) {
    return (t.getIsFloor() == false && t.getHasPhysics() == false);
  }

  private Vector2 expandRigidBody(final int x, final int y) {
    Vector2 size = new Vector2(1,1);
    int xNew = x + 1;
    while (xNew < HuntedGame.TILE_X) {
      Tile t = getTile(xNew, y);
      if (canIncludeInRigidBody(t)) {
        size.x += 1;
        ++xNew;
      } else {
        break;
      }
    }
    int yNew = y + 1;
    while (yNew < HuntedGame.TILE_Y) {
      boolean canExpand = true;
      for (int cX = x; cX < x + size.x; ++cX) {
        Tile t = getTile(cX, yNew);
        if (canIncludeInRigidBody(t) == false) canExpand = false;
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
    int count = 0;
    for (int x = 0; x < HuntedGame.TILE_X; ++x) {
      for (int y = 0; y < HuntedGame.TILE_Y; ++y) {
        Tile t = getTile(x, y); // Find a solid tile
        if (canIncludeInRigidBody(t)) {
          Vector2 size = expandRigidBody(x, y);
          t.setPhysicsBody(size.x, size.y);
          ++count;
        }
      }
    }
    Gdx.app.log("WorldGen", "required " + count + " rigid bodies");
  }

  public Player getPlayer() {
    return player;
  }

  public BigBad getBigBad() { return  bigBad; }

  public Group getTileSet() {
    return tileSet;
  }

  public Tile getTile(int x, int y) {
    return tileMap.get(HuntedGame.xyToID(x,y));
  }

  public void dispose() {
    tileSet.clearChildren();
    tileMap.clear();
  }
}
