package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sprites;
import timboe.hunted.manager.Physics;
import timboe.hunted.world.Room;
import timboe.hunted.world.WorldGen;

import java.util.*;

/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends ParticleEffectActor {

  public enum AIState {IDLE, ROTATE, PATHING, HUNTPATHING, DOASTAR, RETURN_TO_WAYPOINT, CHASE, END}
  public AIState aiState = AIState.IDLE;
  private LinkedList<Tile> movementTargets; // List of destinations for AI
  private HashSet<Room> roomsVisited;
  private Vector2 atDestinationVector = new Vector2();
  private Tile tileUnderMe = null;

  private RayCastCallback raycastCallback = null;
  private float raycastMin = 1f;
  public boolean canSeePlayer;
  public boolean sameRoomAsPlayer;
  public float distanceFromPlayer;


  public BigBad() {
    super(0,0);
    speed = Param.BIGBAD_SPEED;
    roomsVisited = new HashSet<Room>();
    setTexture("playerC");
    setAsPlayerBody(0.5f, 0.25f);
    addTorchToEntity(true, false, 45f, Param.EVIL_FLAME, true, false, null);
    torchLight[0].setDistance(Param.PLAYER_TORCH_STRENGTH);
    addTorchToEntity(true, false, 180f, Param.EVIL_FLAME, true, false, null);
    torchLight[1].setDistance(Param.SMALL_TORCH_STRENGTH);
    movementTargets = new LinkedList<Tile>();
    raycastCallback = new RayCastCallback() {
      @Override
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if ((fixture.getFilterData().categoryBits & Param.TORCH_SENSOR_ENTITY) > 0) return 1;
        if ((fixture.getFilterData().categoryBits & Param.TORCH_ENTITY) > 0) return 1;
        if (fraction < Sprites.getInstance().getBigBad().raycastMin) {
          canSeePlayer = (fixture.getFilterData().categoryBits == Param.PLAYER_ENTITY);
          raycastMin = fraction;
        }
        return 1;
      }
    };
  }

//  @Override
  public void updatePhysics() {
    // Set speed
    speed = Param.BIGBAD_SPEED;
    for (int i = 1; i <= Param.KEY_ROOMS; ++i) {
      if (GameState.getInstance().progress[i] == Param.SWITCH_TIME) speed += Param.BIGBAD_SPEED_BOOST;
    }
    if (aiState == AIState.HUNTPATHING) {
      // If close to the destination then try slowing down a little so as not to overshoot
      final float mod = (float)Math.min(1f, Math.log10(distanceToDestination() * 10));
      speed = Param.BIGBAD_RUSH * mod;
    }
    // Update the things which relate to the movement of the AI over different tiles
    Tile t = getTileUnderEntity();
    if (tileUnderMe != t) {
      tileUnderMe = t;
      t.setIsWeb();
      roomsVisited.add(t.getTilesRoom());
    }
    // Get straight line distance from player
    distanceFromPlayer = Sprites.getInstance().getPlayer().getBody().getPosition().dst( body.getPosition() );
    // See if the AI can see the player
    sameRoomAsPlayer = (t.myRoom != null && t.myRoom == Sprites.getInstance().getPlayer().getRoomUnderEntity());
    // See if we should change AI state to get player
    if (sameRoomAsPlayer && aiState != AIState.END) aiState = AIState.CHASE;
    raycastMin = 9999f;  // Bounce a ray to the player - does it intersect anything else first?
    Physics.getInstance().world.rayCast(raycastCallback, body.getPosition(), Sprites.getInstance().getPlayer().getBody().getPosition());
    if (canSeePlayer) Gdx.app.log("AI","CAN SEE");
    // Lighting call
    flicker();
    // Do all the AI stuff
    runAI();
  }

  public void runAI() {
    switch (aiState) {
      case IDLE: chooseDestination(); break;
      case RETURN_TO_WAYPOINT: getNearestWaypoint(); break;
      case ROTATE: rotate(); break;
      case PATHING: case HUNTPATHING: path(); break;
      case DOASTAR: doAStar(); break;
      case CHASE: doChase(); break;
      case END: doEnd(); break;
    }
  }

  private float distanceToDestination() {
    atDestinationVector.set( (movementTargets.get(0).getX() / Param.TILE_SIZE) + .5f,
      (movementTargets.get(0).getY() / Param.TILE_SIZE) + .5f);
    return atDestinationVector.dst( body.getPosition());
  }

  private boolean atDestination() {
    return Math.abs(distanceToDestination()) < 0.1f;
  }

  private void rotate() {
    float targetAngle = getTargetAngle();
    if (Math.abs(body.getAngle() - targetAngle) < Math.toRadians(10)) {
      aiState = AIState.PATHING;
    } else {
      float diff = targetAngle - body.getAngle();
      int sign = (diff >= 0 && diff <= Math.PI) || (diff <= -Math.PI && diff >= -2*Math.PI) ? 1 : -1;
      setMoveDirection(body.getAngle() + (sign * Param.BIGBAD_ANGULAR_SPEED), false);
    }
  }

  private float getTargetAngle() {
    return Utility.getTargetAngle((movementTargets.get(0).getX() / Param.TILE_SIZE) + .5f,
      (movementTargets.get(0).getY() / Param.TILE_SIZE) + .5f,
      body.getPosition());
  }

  public void webHit() {
    if (aiState != AIState.CHASE && aiState != AIState.END) aiState = BigBad.AIState.DOASTAR;
  }

  private void path() {
    if (atDestination()) {
      movementTargets.remove(0);
      Gdx.app.log("AI","Reached target - " + movementTargets.size() + " more targets");
      if (movementTargets.size() == 0) { // Reached destination
        setMoving(false);
        if (aiState == AIState.HUNTPATHING) aiState = AIState.RETURN_TO_WAYPOINT;
        else aiState = AIState.IDLE;
      } else if (aiState == AIState.PATHING) { // More steps // Only in regular path mode do we rotate
        aiState = AIState.ROTATE;
      }
    } else {
      setMoveDirection(getTargetAngle(), true);
      setMoving(true);
    }
  }

  private void chooseDestination() {
    // First try and follow scent trail
    Room playerRoom = Sprites.getInstance().getPlayer().getRoomUnderEntity();
    HashMap.Entry<Room, Room> toGoTo = getRoomUnderEntity().getConnectionTo(playerRoom);
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

  private void getNearestWaypoint() {
    Tile nearest = null;
    float dist = 999f;
    Vector2 tempVectorA = new Vector2();
    Vector2 tempVectorB = new Vector2( getTileUnderEntity().getX(), getTileUnderEntity().getY() );
    for (Tile t : GameState.getInstance().waypoints) {
      tempVectorA.set( t.getX(), t.getY() );
      if (tempVectorA.dst( tempVectorB ) < dist ) {
        dist = tempVectorA.dst( tempVectorB );
        nearest = t;
      }
    }
    if (nearest == null) {
      Gdx.app.error("AI","Nearest waypoint fail");
      Gdx.app.exit();
    }
    movementTargets.add(nearest);
    aiState = AIState.PATHING;
  }

  private void basicPathing(Room corridor, Room target) {
    Gdx.app.log("AI","Starting - " + body.getPosition());
    if (corridor.getCorridorDirection() == Room.CorridorDirection.VERTICAL) {
      int commonX = (int)(corridor.x + Param.CORRIDOR_SIZE/2f);
      Gdx.app.log("AI","Go through V corridor at common X:" + commonX);
      int finalY = (int)(target.y + Param.CORRIDOR_SIZE/2f);
      if (target.y < corridor.y) finalY = (int)(target.y + target.height - Param.CORRIDOR_SIZE/2f);
      Tile t1 = Sprites.getInstance().getTile(commonX, (int)body.getPosition().y);
      Tile t2 = Sprites.getInstance().getTile(commonX, finalY);
      movementTargets.add( t1 );//   new Vector2(commonX, body.getPosition().y) );
      movementTargets.add( t2 );//new Vector2(commonX, finalY));
    } else {
      int commonY = (int)(corridor.y + Param.CORRIDOR_SIZE/2f);
      Gdx.app.log("AI","Go through H corridor at common Y:" + commonY);
      int finalX = (int)(target.x + Param.CORRIDOR_SIZE/2f);
      if (target.x < corridor.x) finalX = (int)(target.x + target.width - Param.CORRIDOR_SIZE/2f);
      Tile t1 = Sprites.getInstance().getTile((int)body.getPosition().x, commonY);
      Tile t2 = Sprites.getInstance().getTile(finalX, commonY);
      movementTargets.add( t1 );
      movementTargets.add( t2 );
    }
    // Check we are not already at our first target
//    for (Vector2 t : movementTargets) {
//      Gdx.app.log("AI","Movement target - " + t);
//    }
    aiState = AIState.ROTATE;
  }

  private void doAStar() {
    final Tile dest = GameState.getInstance().aiDestination;
    movementTargets = Sprites.getInstance().findPath(getTileUnderEntity(), dest);
    // Cull the list of non-waypoint nodes
    // Note we always leave the final point
    HashSet<Tile> toRemove = new HashSet<Tile>();
    for (int i = 0; i < movementTargets.size() - 1; ++i) {
      if (!GameState.getInstance().waypoints.contains( movementTargets.get(i) )) toRemove.add( movementTargets.get(i) );
    }
    movementTargets.removeAll( toRemove );
    Gdx.app.log("AI","Pathing from " + this + " to " + dest);
    aiState = AIState.HUNTPATHING;
  }

  private void doChase() {
    if (movementTargets.size() == 0) {
      movementTargets.add( Sprites.getInstance().getPlayer().getTileUnderEntity() );
    } else {
      movementTargets.set(0, Sprites.getInstance().getPlayer().getTileUnderEntity() );
    }
    setMoveDirection(getTargetAngle(), true);
    setMoving(true);
    if (aiState == AIState.CHASE && distanceFromPlayer < Param.BIGBAD_POUNCE_DISTANCE) { // see if it's game over
      aiState = AIState.END;
    } else if (aiState == AIState.CHASE && !sameRoomAsPlayer && !canSeePlayer) {  // see if we should stop chasing
      //TODO the !canSeePlayer seems broken
      aiState = AIState.RETURN_TO_WAYPOINT;
      movementTargets.clear();
    }
  }

  private void doEnd() {
    speed = Param.PLAYER_SPEED * 1.1f;
    // TODO animation step
    doChase();
    if (distanceFromPlayer < .5f) {
      WorldGen.getInstance().generateWorld(); // Restart
    }
  }

}
