package timboe.hunted.world;

/**
 * Created by Tim on 28/12/2016.
 */
public class WorldGen {
  private static WorldGen ourInstance = new WorldGen();

  public static WorldGen getInstance() {
    return ourInstance;
  }

  private WorldGen() {
  }
}
