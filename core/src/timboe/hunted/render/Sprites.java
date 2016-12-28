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

  private Sprites() {
    player = new Player();
    tileSet = new Group();

//    tileMap = new HashMap<Integer, Tile>();
//    for (int cX = 0; cX < HuntedGame.CHUNKS_X; ++cX) {
//      for (int cY = 0; cY < HuntedGame.CHUNKS_Y; ++cY) {
//        final int xOff = cX * HuntedGame.CHUNK_SIZE;
//        final int yOff = cY * HuntedGame.CHUNK_SIZE;
//        Group chunkGroup = new Group();
//        for (int x = xOff; x < xOff + HuntedGame.CHUNK_SIZE; ++x) {
//          for (int y = 0; y < yOff + HuntedGame.CHUNK_SIZE; ++y) {
//            Tile t = new Tile(x, y);
//            tileMap.put(HuntedGame.xyToID(x, y), t);
//            chunkGroup.addActor(t);
//          }
//        }
//        tileSet.addActor(chunkGroup);
//      }
//    }

    tileMap = new HashMap<Integer, Tile>();
    for (int x = 0; x < HuntedGame.TILE_X; ++x) {
      for (int y = 0; y < HuntedGame.TILE_Y; ++y) {
        Tile t = new Tile(x, y);
        tileMap.put(HuntedGame.xyToID(x, y), t);
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
    return tileMap.get(HuntedGame.xyToID(x,y));
  }

  public void dispose() {
    tileSet.clearChildren();
    tileMap.clear();
  }
}
