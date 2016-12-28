package timboe.hunted.render;

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

  public Integer xyToID(int x, int y) {
    return (HuntedGame.TILE_H * x) + y;
  }

  private Sprites() {
    player = new Player();
    tileSet = new Group();
    tileMap = new HashMap<Integer, Tile>();
    for (int x = 0; x < HuntedGame.TILE_W; ++x) {
      for (int y = 0; y < HuntedGame.TILE_H; ++y) {
        Tile t = new Tile(x,y);
        tileMap.put(xyToID(x,y), t);
        tileSet.addActor(t);
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
    return tileMap.get(xyToID(x,y));
  }

  public void dispose() {
    tileSet.clearChildren();
    tileMap.clear();
  }
}
