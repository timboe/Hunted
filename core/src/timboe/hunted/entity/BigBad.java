package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;
import timboe.hunted.world.Room;

import java.util.*;

/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends EntityBase {

  enum AIState {IDLE, ROTATE, PATHING}
  AIState aiState = AIState.IDLE;
  Vector<Vector2> movementTargets;
  HashSet<Room> roomsVisited;
  Vector2 pathingVector; //TODO this is debug

  RayCastCallback raycastCallback = null;
  private float raycastMin = 1f;
  public boolean canSeePlayer;
  public float distanceFromPlayer;

  public BigBad() {
    super(0,0);
    speed = Param.BIGBAD_SPEED;
    pathingVector = new Vector2();
    roomsVisited = new HashSet<Room>();
    setTexture("playerC");
    setAsPlayerBody(0.5f, 0.25f);
    addTorchToEntity(true, false, false, 45f, Param.EVIL_FLAME, 0f, 0.25f);
    torchLight.setDistance(Param.PLAYER_TORCH_STRENGTH);
    addTorchToEntity(true, false, true, 0f, Param.EVIL_FLAME, 0f, 0.25f);
    torchLight.setDistance(1f);
    movementTargets = new Vector<Vector2>();
    raycastCallback = new RayCastCallback() {
      @Override
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if ((fixture.getFilterData().categoryBits & Param.TORCH_SENSOR_ENTITY) > 0) return 1;
        if (fraction < Sprites.getInstance().getBigBad().raycastMin) {
          Sprites.getInstance().getBigBad().canSeePlayer = (fixture.getFilterData().categoryBits == Param.PLAYER_ENTITY);
          raycastMin = fraction;
        }
        return 1;
      }
    };
  }

//  @Override
  public void updatePhysics() {
    distanceFromPlayer = Sprites.getInstance().getPlayer().getBody().getPosition().dst( body.getPosition() );
    runAI();
    Tile t = getTileUnderEntity();
    t.setIsWeb();
    roomsVisited.add(t.getTilesRoom());
    raycastMin = 9999f;     // Bounce a ray to the player - does it intersect anything else first?
    Physics.getInstance().worldBox2D.rayCast(raycastCallback, body.getPosition(), Sprites.getInstance().getPlayer().getBody().getPosition());
    flicker();
  }

  public void runAI() {

//    if (pathingVector.epsilonEquals(worldBox.x,worldBox.y,1e-2f) == false) {
//      pathingVector.set(worldBox.x,worldBox.y);
//      float length = 0;
//      if (movementTargets.size() > 0) {
//        Vector2 newV = movementTargets.get(0).cpy();
//        newV.sub(pathingVector);
//        length = newV.len();
//      }
//      Gdx.app.log("AI","BigBad moved to " + pathingVector + " distance from target " + length);
//    }

    switch (aiState) {
      case IDLE: chooseDestination(); break;
      case ROTATE: rotate(); break;
      case PATHING: path(); break;
    }
  }

  private float getTargetAngle() {
    Vector2 target = movementTargets.get(0);
    float targetAngle = (float) Math.atan2(target.y - body.getPosition().y, target.x - body.getPosition().x);
    if (targetAngle < 0) targetAngle += (float)2*Math.PI;
    return targetAngle;
  }

  private boolean atDestination() {
    return movementTargets.get(0).epsilonEquals(body.getPosition(),.1f);
  }

  private void rotate() {
    float targetAngle = getTargetAngle();
    if (Math.abs(body.getAngle() - targetAngle) < Math.toRadians(10)) {
      aiState = AIState.PATHING;
    } else {
      float diff = targetAngle - body.getAngle();
//      int sign = (diff >= Math.PI && diff <= 2*Math.PI) || (diff <= 0 && diff >= -Math.PI) ? -1 : 1;
      int sign = (diff >= 0 && diff <= Math.PI) || (diff <= -Math.PI && diff >= -2*Math.PI) ? 1 : -1;
//      Gdx.app.log("AI","Target: " + Math.toDegrees(targetAngle) + ". Rotate from  " + Math.toDegrees(body.getAngle()) + " to " + Math.toDegrees(body.getAngle() + (sign * Param.BIGBAD_ANGULAR_SPEED)));
      setMoveDirection(body.getAngle() + (sign * Param.BIGBAD_ANGULAR_SPEED), false);
    }
  }

  private void path() {
    if (atDestination()) {
      movementTargets.remove(0);
      Gdx.app.log("AI","Reached target - " + movementTargets.size() + " more targets");
      if (movementTargets.size() == 0) {
        setMoving(false);
        aiState = AIState.IDLE;
      } else {
        aiState = AIState.ROTATE;
      }
    } else {
      float targetAngle = getTargetAngle();
      setMoveDirection(targetAngle, true);
    }
  }

  private void chooseDestination() {
    // First try and follow scent trail
    Room playerRoom = Sprites.getInstance().getPlayer().getRoomUnderEntity();
    HashMap.Entry<Room, Room> toGoTo = getRoomUnderEntity().getConnectionTo(playerRoom);
    // TODO what if same room as player
    if (canSeePlayer && distanceFromPlayer < Param.BIGBAD_SENSE_DISTANCE && toGoTo != null) {
      Gdx.app.log("AI","Got visual on player in neighbouring room/corridor");
    } else if (Utility.prob(getRoomUnderEntity().getScent()) ) { // Follow scent
      toGoTo = getRoomUnderEntity().getNeighborRoomWithHighestScentTrail();
      Gdx.app.log("AI","Got scent of " + getRoomUnderEntity().getScent()*100 + "% following to " + toGoTo.getValue() + " with scent " + toGoTo.getValue().getScent()*100);
    } else { // Pick random, prefer new rooms
      toGoTo = getRoomUnderEntity().getRandomNeighbourRoom(roomsVisited);
    }
    basicPathing(toGoTo.getKey(), toGoTo.getValue());
  }

  private void basicPathing(Room corridor, Room target) {
    Gdx.app.log("AI","Starting - " + body.getPosition());
    if (corridor.getCorridorDirection() == Room.CorridorDirection.VERTICAL) {
      float commonX = corridor.x + Param.CORRIDOR_SIZE/2f;
      Gdx.app.log("AI","Go through V corridor at common X:" + commonX);
      float finalY = target.y + Param.CORRIDOR_SIZE/2f;
      if (target.y < corridor.y) finalY = target.y + target.height - 1 - Param.CORRIDOR_SIZE/2f;
      movementTargets.add( new Vector2(commonX, body.getPosition().y) );
      movementTargets.add( new Vector2(commonX, finalY));
    } else {
      float commonY = corridor.y + Param.CORRIDOR_SIZE/2f;
      Gdx.app.log("AI","Go through H corridor at common Y:" + commonY);
      float finalX = target.x + Param.CORRIDOR_SIZE/2f;
      if (target.x < corridor.x) finalX = target.x + target.width - 1 - Param.CORRIDOR_SIZE/2f;
      movementTargets.add( new Vector2(body.getPosition().x, commonY) );
      movementTargets.add( new Vector2(finalX, commonY) );
    }
    // Check we are not already at our first target
//    for (Vector2 t : movementTargets) {
//      Gdx.app.log("AI","Movement target - " + t);
//    }
    aiState = AIState.ROTATE;
  }

}
