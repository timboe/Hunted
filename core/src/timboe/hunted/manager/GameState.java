package timboe.hunted.manager;

import timboe.hunted.Param;

/**
 * Created by Tim on 08/01/2017.
 */
public class GameState {

  public boolean switchExit = false;
  public boolean[] switchKeyRoom = new boolean[Param.KEY_ROOMS];

  public int progressExit = 0;
  public int[] progressKeyRoom = new int[Param.KEY_ROOMS];

  private static GameState ourInstance = new GameState();

  public static GameState getInstance() {
    return ourInstance;
  }

  private GameState() {
  }

  public void updatePhysics() {

    // Update logic
    if (progressExit < Param.SWITCH_TIME) {
      if (switchExit) ++progressExit;
      else if (progressExit > 0) --progressExit;
    }
    for (int i = 0; i < Param.KEY_ROOMS; ++i) {
      if (progressKeyRoom[i] < Param.SWITCH_TIME) {
        if (switchKeyRoom[i]) ++progressKeyRoom[i];
        else if (progressKeyRoom[i]> 0) --progressKeyRoom[i];
      }
    }

  }
}
