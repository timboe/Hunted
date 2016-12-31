package timboe.hunted.entity;

import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Utility;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Room;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends EntityBase {

  enum AIState {IDLE, PATHTOROOM}
  AIState aiState = AIState.IDLE;
  ArrayList<Vector2> movementTargets;

  public BigBad() {
    super(0,0);
    texture = Textures.getInstance().dummyBigBad;
    setPlayerBody(0.5f, 0.25f);
    movementTargets = new ArrayList<Vector2>();
  }

  public void runAI() {
    switch (aiState) {
      case IDLE: chooseDestination();
    }
  }

  private void chooseDestination() {
    // First try and follow scent trail
    if ( Utility.prob(getRoomUnderEntity().getScent()) ) { // Follow scent
      HashMap.Entry<Room,Room> toGoTo = getRoomUnderEntity().getNeighborRoomWithHighestSccentTrail();
      Room targetCorridor = toGoTo.getKey();
      Room targetRoom = toGoTo.getValue();

    }
  }

}
