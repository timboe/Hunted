package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Room;

import java.util.*;

/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends EntityBase {

  enum AIState {IDLE, SETANGLE, PATHING}
  AIState aiState = AIState.IDLE;
  Vector<Vector2> movementTargets;
  HashSet<Room> roomsVisited;
  Vector2 pathingVector; //TODO this is debug


  public BigBad() {
    super(0,0);
    speed = Param.BIGBAD_SPEED;
    pathingVector = new Vector2();
    roomsVisited = new HashSet<Room>();
    texture = Textures.getInstance().dummyBigBad;
    setAsPlayerBody(0.5f, 0.25f);
    movementTargets = new Vector<Vector2>();
  }

//  @Override
//  public void setPhysicsPosition(float x, float y) {
//    super.setPhysicsPosition(x,y);
//    roomsVisited.add(getRoomUnderEntity());
//  }

  @Override
  public void updatePhysics() {
    runAI();
    Tile t = getTileUnderEntity();
    t.setIsWeb();
    roomsVisited.add(t.getTilesRoom());
    super.updatePhysics();
  }

  public void runAI() {

    if (pathingVector.epsilonEquals(worldBox.x,worldBox.y,1e-2f) == false) {
      pathingVector.set(worldBox.x,worldBox.y);
      float length = 0;
      if (movementTargets.size() > 0) {
        Vector2 newV = movementTargets.get(0).cpy();
        newV.sub(pathingVector);
        length = newV.len();
      }
//      Gdx.app.log("AI","BigBad moved to " + pathingVector + " distance from target " + length);
    }

    switch (aiState) {
      case IDLE: chooseDestination(); break;
//      case SETANGLE: setAngle(); break;
      case PATHING: path(); break;
    }
  }

//  private void setAngle() {
//    Vector2 target = movementTargets.get(0);
//    float angle = (float)Math.atan2(Math.round(target.y - worldBox.y), Math.round(target.x - worldBox.x));
//    Gdx.app.log("AI","Path from " + worldBox.x + "," + worldBox.y + " to " + target.x + "," + target.y + " which is angle " + Math.toDegrees(angle));
//    aiState = AIState.PATHING;
//    setMoveDirection(angle);
//    // we path right away as we may already be at the destination
//    path();
//
//
//  }

  private void path() {
    Vector2 target = movementTargets.get(0); // Are we there yet?
    float angle = (float)Math.atan2(Math.round(target.y - worldBox.y), Math.round(target.x - worldBox.x));
    setMoveDirection(angle);

    if (target.epsilonEquals(worldBox.x,worldBox.y,1e-4f)) {
      setPhysicsPosition(worldBox.x, worldBox.y); // We do this to snap the physics object to the grid. Otherwise could drift
      movementTargets.remove(0);
      Gdx.app.log("AI","Reached target - " + movementTargets.size() + " more targets");
      if (movementTargets.size() == 0) {
        moving = false;
        aiState = AIState.IDLE;
//      } else {
//        aiState = AIState.SETANGLE;
      }
    }
  }

  private void chooseDestination() {
    // First try and follow scent trail
    HashMap.Entry<Room,Room> toGoTo;
    if ( true || Utility.prob(getRoomUnderEntity().getScent()) ) { // Follow scent
      toGoTo = getRoomUnderEntity().getNeighborRoomWithHighestScentTrail();
      Gdx.app.log("AI","Got scent of " + getRoomUnderEntity().getScent()*100 + "% following to " + toGoTo.getValue() + " with scent " + toGoTo.getValue().getScent()*100);
    } else { // Pick random, prefer new rooms
      toGoTo = getRoomUnderEntity().getRandomNeighbourRoom(roomsVisited);
    }
    basicPathing(toGoTo.getKey(), toGoTo.getValue());
  }

  private void basicPathing(Room corridor, Room target) {
    Gdx.app.log("AI","Starting - " + worldBox.x + "," + worldBox.y);
    if (corridor.getCorridorDirection() == Room.CorridorDirection.VERTICAL) {
      int commonX = (int) (corridor.x + Math.floor(Param.CORRIDOR_SIZE/2f));
      Gdx.app.log("AI","Go through V corridor at common X:" + commonX);
      float finalY = target.y + (float)Math.floor(Param.CORRIDOR_SIZE/2f);
      if (target.y < corridor.y) finalY = target.y + target.height - (float)Math.ceil(Param.CORRIDOR_SIZE/2f);
      movementTargets.add( new Vector2(commonX, worldBox.y) );
      movementTargets.add( new Vector2(commonX, finalY));
    } else {
      int commonY = (int) (corridor.y + Math.floor(Param.CORRIDOR_SIZE/2f));
      Gdx.app.log("AI","Go through H corridor at common Y:" + commonY);
      float finalX = target.x + (float)Math.floor(Param.CORRIDOR_SIZE/2f);
      if (target.x < corridor.x) finalX = target.x + target.width - (float)Math.ceil(Param.CORRIDOR_SIZE/2f);
      movementTargets.add( new Vector2(worldBox.x, commonY) );
      movementTargets.add( new Vector2(finalX, commonY) );
    }
//    for (Vector2 t : movementTargets) {
//      Gdx.app.log("AI","Movement target - " + t);
//    }
    aiState = AIState.PATHING;
  }

}
