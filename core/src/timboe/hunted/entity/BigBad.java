package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sprites;
import timboe.hunted.manager.Physics;
import timboe.hunted.pathfinding.PathFinding;
import timboe.hunted.world.Room;
import timboe.hunted.world.WorldGen;

import java.util.*;

import static timboe.hunted.entity.BigBad.AIState.*;


/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends ParticleEffectActor {

  private Body lightAttachment;
  private float torchOffset = 1.5f; // How much above the centre of the tile the bigbads torch it

  public enum AIState {IDLE, // need to choose a new destination
    ROTATE, // Slowly change angle. Leads to PATHING
    PATHING, // Regular exploration, leads to ROTATE or IDLE
    PATHING_TO_WAYPOINT, // Similar to PATHING (no rotation). Only leads to IDLE
    HUNTPATHING, // Fast version of PATHING. Leads to RETURN_TO_WAYPOINT
    DOASTAR, // Triggers pathfinding to location. Leads to HUNTPATHING
    RETURN_TO_WAYPOINT, // Clear destination list and add just the most recent waypoint. Leads to PATHING_TO_WAYPOINT
    CHASE, // Home in on player. Leads to END or RETURN_TO_WAYPOINT.
    END} // Home in faster than player's speed. No return.
  private AIState aiState = AIState.RETURN_TO_WAYPOINT;
  private LinkedList<Tile> movementTargets; // List of destinations for AI
  private HashSet<Room> roomsVisited;
  private Vector2 atDestinationVector = new Vector2();
  private Tile tileUnderMe = null;

  private RayCastCallback raycastCallback = null;
  private float raycastMin = 1f;
  public boolean canSeePlayer = false;
  public boolean lookingAtPlayer = false;
  public boolean sameRoomAsPlayer = false;
  public boolean musicSting = false;
  public float distanceFromPlayer;
  private final float yOff = 32;
  private int sixthSense;


  public BigBad() {
    super(0,0);
    speed = Param.BIGBAD_SPEED;
    roomsVisited = new HashSet<Room>();
    setTexture("bb", 8);
    setAsPlayerBody(0.5f, 0.25f);
    movementTargets = new LinkedList<Tile>();
    raycastCallback = new RayCastCallback() {
      @Override
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if ((fixture.getFilterData().categoryBits & Param.BIGBAD_CAN_SEE_THROUGH) > 0) return 1;
        if (fraction < Sprites.getInstance().getBigBad().raycastMin) {
          canSeePlayer = (fixture.getFilterData().categoryBits == Param.PLAYER_ENTITY);
          raycastMin = fraction;
        }
        return 1;
      }
    };

    //    particleEffect = Utility.getNewFlameEffect();
    // BB should not have?

    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    lightAttachment = Physics.getInstance().world.createBody(bodyDef);
    lightAttachment.setUserData(this);
    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(.05f);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.filter.categoryBits = Param.TORCH_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY|Param.WORLD_ENTITY|Param.TORCH_ENTITY; // I collide with
    lightAttachment.createFixture(fixtureDef);
    circleShape.dispose();

    addTorchToEntity(45f, Param.PLAYER_TORCH_STRENGTH, Param.EVIL_FLAME, false, null);
    addTorchToEntity(180f, Param.SMALL_TORCH_STRENGTH, Param.EVIL_FLAME, true, null);
    torchLight[0].attachToBody(lightAttachment);
    torchLight[0].setIgnoreAttachedBody(true);
    torchLight[0].setContactFilter(Param.TORCH_ENTITY,
      (short)0,
      (short)(Param.PLAYER_ENTITY|Param.WORLD_ENTITY|Param.CLUTTER_ENTITY)); // I am a, 0, I collide with
    torchLight[1].attachToBody(lightAttachment);

  }

  public boolean isChasing() {
    return aiState == AIState.CHASE || aiState == AIState.END;
  }

  public boolean isEnd() { return aiState == AIState.END; }

  private void checkStartChase() {
    if (sameRoomAsPlayer && !isChasing()) {
      aiState = AIState.CHASE;
      Gdx.app.log("AI","checkStartChase -> CHASE");
      // Give the player a hit of adrenalin
      Sprites.getInstance().getPlayer().speed = Param.PLAYER_SPEED_BOOST;
    } // TODO also chase if nearby and within vision
  }

  public void updatePhysics() {
    sixthSense = Param.BIGBAD_SIXTH_SENSE;
    // Get straight line distance from player
    distanceFromPlayer = Sprites.getInstance().getPlayer().getBody().getPosition().dst( body.getPosition() );
    // Set speed
    speed = Param.BIGBAD_SPEED;
    if (aiState == AIState.HUNTPATHING) {
      // If close to the destination then try slowing down a little so as not to overshoot
      final float mod = (float)Math.min(1f, Math.log10(distanceToDestination() * 10));
      speed = Param.BIGBAD_RUSH * mod;
    } else {
      for (int i = 1; i <= Param.KEY_ROOMS; ++i) {
        if (GameState.getInstance().progress[i] == Param.SWITCH_TIME) {
          sixthSense += Param.BIGBAD_SIXTH_SENSE_BOOST;
          speed += Param.BIGBAD_SPEED_BOOST;
        }
      }
    }
    // If far away then speed up
    if (distanceFromPlayer > Param.BIGBAD_SENSE_DISTANCE * 2) {
      speed *= 2;
    }
    // Update the things which relate to the movement of the AI over different tiles
    Tile t = getTileUnderEntity();
    if (tileUnderMe != t) {
      tileUnderMe = t;
      roomsVisited.add(t.getTilesRoom());
      if (aiState == PATHING) t.setIsWeb();
    }

    // See if the AI can see the player
    sameRoomAsPlayer = (t.myRoom != null && t.myRoom == Sprites.getInstance().getPlayer().getRoomUnderEntity());
    raycastMin = 9999f;  // Bounce a ray to the player - does it intersect anything else first?
    Physics.getInstance().world.rayCast(raycastCallback, body.getPosition(), Sprites.getInstance().getPlayer().getBody().getPosition());
    // See if we should change AI state to get player
    checkStartChase();

    lookingAtPlayer = canSeePlayer; // TODO make this based on angle

    musicSting = (isChasing() || (lookingAtPlayer && distanceFromPlayer < Param.BIGBAD_SENSE_DISTANCE));
    // Lighting call
    flicker();
    // Do all the AI stuff
    runAI();
  }

  @Override
  public void updatePosition() {
    super.updatePosition();

    // Update my light
    lightAttachment.setTransform(body.getPosition().x,
      body.getPosition().y + torchOffset, body.getAngle());
      super.updatePosition();

    setPosition(getX(), getY() + yOff);
    // Set angle
    float ang = body.getAngle();
    if (ang < Math.PI/4f) currentFrame = 0;
    else if (ang < 3*Math.PI/4f) currentFrame = 1;
    else if (ang < 5*Math.PI/4f) currentFrame = 2;
    else if (ang < 7*Math.PI/4f) currentFrame = 3;
    else currentFrame = 0;
    if (isChasing()) currentFrame += 4;
  }

    public void runAI() {
    switch (aiState) {
      case IDLE: chooseDestination(); break;
      case RETURN_TO_WAYPOINT: getNearestWaypoint(); break;
      case ROTATE: rotate(); break;
      case PATHING: case HUNTPATHING: case PATHING_TO_WAYPOINT: path(); break;
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
//      Gdx.app.log("AI","rotate -> PATHING");
    } else {
      float diff = targetAngle - body.getAngle();
      int sign = (diff >= 0 && diff <= Math.PI) || (diff <= -Math.PI && diff >= -2*Math.PI) ? 1 : -1;
      setMoveDirection(body.getAngle() + (sign * Param.BIGBAD_ANGULAR_SPEED));
      setMoving(false);
    }
  }

  private float getTargetAngle() {
    return Utility.getTargetAngle((movementTargets.get(0).getX() / Param.TILE_SIZE) + .5f,
      (movementTargets.get(0).getY() / Param.TILE_SIZE) + .5f,
      body.getPosition());
  }

  public void webHit() {
    if (tileUnderMe.getIsWeb() && (aiState == PATHING || aiState == ROTATE)) {
      aiState = BigBad.AIState.DOASTAR;
      Gdx.app.log("AI","-> DO A*");
    }
  }

  private void path() {
    if (atDestination()) {
      movementTargets.remove(0);
      if (movementTargets.size() == 0) { // Reached destination
        setMoving(false);
        if (aiState == AIState.HUNTPATHING) {
          aiState = AIState.RETURN_TO_WAYPOINT;
          Gdx.app.log("AI","path -> RETURN TO WAYPOINT");
        } else {
          Gdx.app.log("AI","path -> IDLE");
          aiState = AIState.IDLE;
        }
      } else if (aiState == AIState.PATHING) { // More steps // Only in regular path mode do we rotate
        aiState = AIState.ROTATE;
//        Gdx.app.log("AI","path -> ROTATE");
      }
    } else {
      setMoveDirection(getTargetAngle());
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
      Gdx.app.log("AI", "Got scent of " + getRoomUnderEntity().getScent() * 100 + "% following to " + toGoTo.getValue() + " with scent " + toGoTo.getValue().getScent() * 100);
    } else if (Utility.prob(sixthSense)) { // Clairvoyant!
      Gdx.app.log("AI", "Being clairvoyant");
      toGoTo = getBestRoom();
    } else { // Pick random, prefer new rooms
      toGoTo = getRoomUnderEntity().getRandomNeighbourRoom(roomsVisited);
    }
    basicPathing(toGoTo.getKey(), toGoTo.getValue());
  }

  private HashMap.Entry<Room,Room> getBestRoom() { // Pathfind to the player's room
    LinkedList<Room> pathFind = PathFinding.doAStar(getRoomUnderEntity(), Sprites.getInstance().getPlayer().getRoomUnderEntity());
    if (pathFind == null || pathFind.size() < 2) {
      Gdx.app.error("AI", "Clairvoyant pathfind to players room failed");
      getRoomUnderEntity().getRandomNeighbourRoom(roomsVisited); // Failed for some reason
    }
    // Note we go to corridor in location 1 as location 0 is the current room
    return getRoomUnderEntity().getConnectionTo(pathFind.get(1)); // Go to the first room
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
    movementTargets.clear();
    movementTargets.add(nearest);
    aiState = AIState.PATHING_TO_WAYPOINT;
    Gdx.app.log("AI","getNearestWaypoint -> PATHING_TO_WAYPOINT");
  }

  private void basicPathing(Room corridor, Room target) {
//    Gdx.app.log("AI","Starting - " + body.getPosition());
    if (corridor.getCorridorDirection() == Room.CorridorDirection.VERTICAL) {
      int commonX = (int)(corridor.x + Param.CORRIDOR_SIZE/2f);
//      Gdx.app.log("AI","Go through V corridor at common X:" + commonX);
      int finalY = (int)(target.y + Param.CORRIDOR_SIZE/2f);
      if (target.y < corridor.y) finalY = (int)(target.y + target.height - Param.CORRIDOR_SIZE/2f);
      Tile t1 = Sprites.getInstance().getTile(commonX, (int)body.getPosition().y);
      Tile t2 = Sprites.getInstance().getTile(commonX, finalY);
      movementTargets.add( t1 );//   new Vector2(commonX, body.getPosition().y) );
      movementTargets.add( t2 );//new Vector2(commonX, finalY));
    } else {
      int commonY = (int)(corridor.y + Param.CORRIDOR_SIZE/2f);
//      Gdx.app.log("AI","Go through H corridor at common Y:" + commonY);
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
    Gdx.app.log("AI","basicPathing (idle) -> ROTATE");

  }

  private void doAStar() {
    final Tile dest = GameState.getInstance().aiDestination;
    movementTargets.clear();
    LinkedList<Tile> pathFind = PathFinding.doAStar(getTileUnderEntity(), dest);
    if (pathFind != null) movementTargets.addAll( pathFind );
    if (movementTargets.size() == 0) { // Could not path for some reason
      aiState = AIState.RETURN_TO_WAYPOINT;
      Gdx.app.log("AI","doA* [FAIL] -> RETURN_TO_WAYPOINT");
      return;
    }
    // Cull the list of non-waypoint nodes
    // Note we always leave the final point
    HashSet<Tile> toRemove = new HashSet<Tile>();
    for (int i = 0; i < movementTargets.size() - 1; ++i) {
      if (!GameState.getInstance().waypoints.contains( movementTargets.get(i) )) toRemove.add( movementTargets.get(i) );
    }
    movementTargets.removeAll( toRemove );
    Gdx.app.log("AI","Do A* -> HUNTPATHING  from " + this + " to " + dest);
    aiState = AIState.HUNTPATHING;
  }

  private void doChase() {
    // Keep one entry in the list
    if (movementTargets.size() > 1) movementTargets.clear();
    if (movementTargets.size() == 0) {
      movementTargets.add( Sprites.getInstance().getPlayer().getTileUnderEntity() );
    } else {
      movementTargets.set(0, Sprites.getInstance().getPlayer().getTileUnderEntity() );
    }
    setMoveDirection(getTargetAngle());
    setMoving(true);
    speed = Param.PLAYER_SPEED * 1.1f; // 110% of player max speed (cannot be outrun after adrenalin runs out)
    if (aiState == AIState.CHASE && distanceFromPlayer < Param.BIGBAD_POUNCE_DISTANCE) { // see if it's game over
      aiState = AIState.END;
      Gdx.app.log("AI","doChase -> END");
    } else if (aiState == AIState.CHASE && !sameRoomAsPlayer && !canSeePlayer) {  // see if we should stop chasing
      aiState = RETURN_TO_WAYPOINT;
      Gdx.app.log("AI","doChase -> RETURN TO WAYPOINT");
      movementTargets.clear();
    }
  }

  private void doEnd() {
    doChase();
    speed = Sprites.getInstance().getPlayer().speed * 1.05f; // Based off of players CURRENT speed - will catch
    if (distanceFromPlayer < .5f) {
      WorldGen.getInstance().generateWorld(); // Restart
    }
  }

}
