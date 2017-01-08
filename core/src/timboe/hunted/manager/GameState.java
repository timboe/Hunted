package timboe.hunted.manager;

import timboe.hunted.Param;

/**
 * Created by Tim on 08/01/2017.
 */
public class GameState {

  public int frame = 0;
  public boolean[] switchStatus = new boolean[Param.KEY_ROOMS + 1];
  public int[] progress = new int[Param.KEY_ROOMS + 1];

  private static GameState ourInstance = new GameState();

  public static GameState getInstance() {
    return ourInstance;
  }

  private GameState() {
  }

  public void updatePhysics() {

    // Update logic for key switches
    boolean allKeys = true;
    for (int i = 1; i < Param.KEY_ROOMS + 1; ++i) {
      if (progress[i] < Param.SWITCH_TIME) {
        allKeys = false;
        if (switchStatus[i]) ++progress[i];
        else if (progress[i]> 0) --progress[i];
      }
    }

    // Update logic for exit switch
    if (allKeys) {
      if (progress[0] < Param.SWITCH_TIME) {
        if (switchStatus[0]) ++progress[0];
        else if (progress[0] > 0) --progress[0];
      }
    }
  }
}
