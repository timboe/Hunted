package timboe.hunted.entity;

import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends EntityBase {

  enum AIState {IDLE, PATHING}
  AIState aiState = AIState.IDLE;
  ArrayList<Vector2> movementTargets;
  HashSet<Room> roomsVisited;
  Vector2 pathingVector;

  public BigBad() {
    super(0,0);
    speed = Param.BIGBAD_SPEED;
    pathingVector = new Vector2();
    roomsVisited = new HashSet<Room>();
    texture = Textures.getInstance().dummyBigBad;
    setAsPlayerBody(0.5f, 0.25f);
    movementTargets = new ArrayList<Vector2>();
  }

  @Override
  public void setPhysicsPosition(float x, float y) {
    super.setPhysicsPosition(x,y);
    roomsVisited.add(getRoomUnderEntity());
  }

  @Override
  public void updatePhysics() {
    runAI();
    super.updatePhysics();
  }

  public void runAI() {
    switch (aiState) {
      case IDLE: chooseDestination(); break;
      case PATHING: path(); break;
    }
  }

  private void path() {
    pathingVector = movementTargets.get(0);
    pathingVector.sub( body.getPosition() );
    setMoveDirection( pathingVector.angleRad() );
    if (pathingVector.len() < 0.01f) movementTargets.remove(0);
    if (movementTargets.size() == 0) {
      moving = false;
      aiState = AIState.IDLE;
    }

  }

  private void chooseDestination() {
    // First try and follow scent trail
    HashMap.Entry<Room,Room> toGoTo;
    if ( Utility.prob(getRoomUnderEntity().getScent()) ) { // Follow scent
      toGoTo = getRoomUnderEntity().getNeighborRoomWithHighestScentTrail();
    } else { // Pick random, prefer new rooms
      toGoTo = getRoomUnderEntity().getRandomNeighbourRoom(roomsVisited);
    }
    basicPathing(toGoTo.getKey(), toGoTo.getValue());
  }

  private void basicPathing(Room corridor, Room target) {
    if (corridor.getCorridorDirection() == Room.CorridorDirection.VERTICAL) {
      int commonX = (int) (corridor.x + Math.floor(Param.CORRIDOR_SIZE/2f));
      movementTargets.add( new Vector2(commonX, worldBox.y) );
      movementTargets.add( new Vector2(commonX, target.y + Math.round(target.height/2f)));
    } else {
      int commonY = (int) (corridor.y + Math.floor(Param.CORRIDOR_SIZE/2f));
      movementTargets.add( new Vector2(worldBox.x, commonY) );
      movementTargets.add( new Vector2(target.x + Math.round(target.width/2f), commonY) );
    }
    aiState = AIState.PATHING;
  }

}
