package timboe.hunted.world;

import timboe.hunted.HuntedGame;

import java.util.HashMap;

/**
 * Created by Tim on 28/12/2016.
 */
public class WorldGen {
  private static WorldGen ourInstance = new WorldGen();

  public static WorldGen getInstance() {
    return ourInstance;
  }


  public enum Split {NONE, VERTICAL, HORIZONTAL, BOTH};

  private HashMap<Integer, Chunk> chunkMap;

  private WorldGen() {
    chunkMap = new HashMap<Integer, Chunk>();
    for (int x = 0; x < HuntedGame.CHUNKS_X; ++x) {
      for  (int y = 0; y < HuntedGame.CHUNKS_Y; ++y) {
        final int id = HuntedGame.xyToID(x,y);
        chunkMap.put(id, new Chunk(x,y,id));
      }
    }
  }

  public void generateWorld() {
    for (HashMap.Entry<Integer, Chunk> entry : chunkMap.entrySet()) {
      entry.getValue().generate();
    }
    for (HashMap.Entry<Integer, Chunk> entry : chunkMap.entrySet()) {
      entry.getValue().addRoomsToTileMap();
    }
  }
}
