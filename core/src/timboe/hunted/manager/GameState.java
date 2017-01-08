package timboe.hunted.manager;

/**
 * Created by Tim on 08/01/2017.
 */
public class GameState {
  private static GameState ourInstance = new GameState();

  public static GameState getInstance() {
    return ourInstance;
  }

  private GameState() {
  }
}
