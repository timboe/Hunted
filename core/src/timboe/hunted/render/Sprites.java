package timboe.hunted.render;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Group;
import timboe.hunted.HuntedGame;
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

  private Sprites() {
    reset();
  }

  public void reset() {
    player = new Player();
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

  public void addTileRigidBodies() {
    for (int x = 0; x < HuntedGame.TILE_X; ++x) { // Vertical
      int runSize = 0;
      int runY = 0;
      Tile runStart = null;
      for (int y = 0; y < HuntedGame.TILE_Y; ++y) {
        Tile t = tileMap.get(HuntedGame.xyToID(x, y));
        if (t.isVisible() == true && t.getIsFloor() == false && t.getHasPhysics() == false) {
          if (++runSize == 1) {
            runStart = t;
            runY = y;
          }
        } else if (runSize > 1) { // > 1 in the first pass
          runStart.setPhysicsBody(BodyDef.BodyType.StaticBody, 1, runSize);
          for (int done = 0; done < runSize; ++done) tileMap.get(HuntedGame.xyToID(x, runY+done)).setHasPhysics(true);
          runSize = 0;
        } else {
          runSize = 0;
        }
      }
      if (runSize > 0) {
        runStart.setPhysicsBody(BodyDef.BodyType.StaticBody, 1, runSize);
        for (int done = 0; done < runSize; ++done) tileMap.get(HuntedGame.xyToID(x, runY+done)).setHasPhysics(true);
      }
    }
    for (int y = 0; y < HuntedGame.TILE_Y; ++y) { // Horizontal
      int runSize = 0;
      int runX = 0;
      Tile runStart = null;
      for (int x = 0; x < HuntedGame.TILE_X; ++x) {
        Tile t = tileMap.get(HuntedGame.xyToID(x, y));
        if (t.isVisible() == true && t.getIsFloor() == false && t.getHasPhysics() == false) {
          if (++runSize == 1) {
            runStart = t;
            runX = x;
          }
        } else if (runSize > 0) { // > 0 in the second pass
          runStart.setPhysicsBody(BodyDef.BodyType.StaticBody, runSize, 1);
          for (int done = 0; done < runSize; ++done) tileMap.get(HuntedGame.xyToID(runX + done, y)).setHasPhysics(true);
          runSize = 0;
        }
      }
      if (runSize > 0) {
        runStart.setPhysicsBody(BodyDef.BodyType.StaticBody, runSize, 1);
        for (int done = 0; done < runSize; ++done) tileMap.get(HuntedGame.xyToID(runX + done, y)).setHasPhysics(true);
      }
    }
  }

  public Player getPlayer() {
    return player;
  }

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
