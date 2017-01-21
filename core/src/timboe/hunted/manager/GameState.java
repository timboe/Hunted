package timboe.hunted.manager;

import com.badlogic.gdx.audio.Music;
import timboe.hunted.Param;
import timboe.hunted.entity.BigBad;
import timboe.hunted.entity.Tile;
import timboe.hunted.screen.GameScreen;
import timboe.hunted.world.WorldGen;

import java.util.HashSet;

/**
 * Created by Tim on 08/01/2017.
 */
public class GameState {

  public int frame = 0;
  public boolean[] switchStatus = new boolean[Param.KEY_ROOMS + 1];
  public int[] progress = new int[Param.KEY_ROOMS + 1];
//  public Tile aiDestination = null;
  public int aiCooldown = 0;
  public boolean webEffect = false;
  public GameScreen theGameScreen = null;
  public HashSet<Tile> waypoints; // Known good AI destinations
  public boolean userControl;

  private boolean chaseOn = false;
  private boolean chaseScream = false;
  private int chaseVolume = 100;

  private boolean unlockSound = false;

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
    Sounds.getInstance().startAmbiance();
    userControl = true;
  }

  private void resetInternal() {
    for (int i = 0; i < Param.KEY_ROOMS + 1; ++i) {
      progress[i] = 0;
      switchStatus[i] = false;
    }
    aiCooldown = 0;
    webEffect = false;
    frame = 0;
    userControl = false;
    waypoints = new HashSet<Tile>();
  }

  private void doSwitchLogic(int i) {
    if (switchStatus[i]) {
      if (++progress[i] == Param.SWITCH_TIME) { // finished unlocking
        unlockSound = false;
        Sounds.getInstance().unlockStop();
      } else if (!unlockSound) {
        unlockSound = true;
        Sounds.getInstance().unlock();
      }
    } else if (progress[i]> 0) {
      --progress[i];
      if (unlockSound) {
        unlockSound = false;
        Sounds.getInstance().unlockStop();
      }
    }
  }

  public void updatePhysics() {

    // Update cooldown
    if (aiCooldown > 0) --aiCooldown;

    // Update logic for key switches
    boolean allKeys = true;
    float minDist = 999f;
    for (int i = 1; i < Param.KEY_ROOMS + 1; ++i) {
      if (progress[i] < Param.SWITCH_TIME) {
        allKeys = false;
        doSwitchLogic(i);
      } else { // Machine is on
        minDist = Math.min(minDist, Sprites.getInstance().getPlayer().getBody().getPosition().dst( Sprites.getInstance().keySwitch[i].getBody().getPosition() ) );
      }
    }
    if (minDist <= Param.BIGBAD_SENSE_DISTANCE) {
      Sounds.getInstance().machineNoise((Param.BIGBAD_SENSE_DISTANCE - minDist) / Param.BIGBAD_SENSE_DISTANCE);
    } else {
      Sounds.getInstance().machineNoise(0);
    }

    // Update logic for exit switch
    if (allKeys && progress[0] < Param.SWITCH_TIME) {
      doSwitchLogic(0);
    }

    musicLogic();
  }

  private void musicLogic() {

    if (!chaseOn && Sprites.getInstance().getBigBad().musicSting) {
      chaseOn = true;
      chaseScream = false;
      Sounds.getInstance().startChase();
    }
    if (chaseOn) {
      if (!chaseScream && Sprites.getInstance().getBigBad().isChasing()) {
        chaseScream = true;
        Sounds.getInstance().scream(1f);
      }
      if (!Sprites.getInstance().getBigBad().musicSting) --chaseVolume;
      else chaseVolume = 100;
      Sounds.getInstance().chaseVolume(chaseVolume/100f);
      if (chaseVolume == 0) {
        Sounds.getInstance().endChase();
        chaseOn = false;
      }
    }
  }
}
