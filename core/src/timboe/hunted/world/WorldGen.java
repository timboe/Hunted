package timboe.hunted.world;

import timboe.hunted.HuntedGame;

/**
 * Created by Tim on 28/12/2016.
 */
public class WorldGen {
  private static WorldGen ourInstance = new WorldGen();

  public static WorldGen getInstance() {
    return ourInstance;
  }

  public final int CHUNK_SIZE = 16;
  public final int CHUNKS_X = HuntedGame.TILE_X / CHUNK_SIZE;
  public final int CHUNKS_Y = HuntedGame.TILE_X / CHUNK_SIZE;

  private WorldGen() {
  }
}
