package timboe.hunted.manager;

import timboe.hunted.Param;
import timboe.hunted.entity.Tile;
import timboe.hunted.screen.GameScreen;
import timboe.hunted.world.WorldGen;

/**
 * Created by Tim on 08/01/2017.
 */
public class GameState {

  public int frame = 0;
  public boolean[] switchStatus = new boolean[Param.KEY_ROOMS + 1];
  public int[] progress = new int[Param.KEY_ROOMS + 1];
  public Tile aiDestination = null;
  public int aiCooldown = 0;
  public boolean webEffect = false;
  public GameScreen theGameScreen = null;

  private static GameState ourInstance = new GameState();

  public static GameState getInstance() {
    return ourInstance;
  }

  private GameState() {
  }

  public void reset() {
    resetInternal();
    theGameScreen.reset();
    WorldGen.getInstance().generateWorld();
    theGameScreen.addActors();

  }

  private void resetInternal() {
    for (int i = 0; i < Param.KEY_ROOMS + 1; ++i) {
      progress[i] = 0;
      switchStatus[i] = false;
    }
    aiCooldown = 0;
    aiDestination = null;
    webEffect = false;
    frame = 0;
  }

  public void updatePhysics() {

    // Update cooldown
    if (aiCooldown > 0) --aiCooldown;

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
