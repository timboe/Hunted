package timboe.hunted.entity;

import com.badlogic.gdx.physics.box2d.*;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;
import timboe.hunted.manager.Sprites;
import timboe.hunted.pathfinding.Node;
import timboe.hunted.world.Room;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tim on 28/12/2016.
 */
public class Tile extends EntityBase implements Node<Tile> {

  private boolean isFloor = false;
  private boolean hasPhysics = false;
  private boolean isWeb = false;
  private Room myRoom = null;
  public int switchID = -1; // -1 is invalid, 0=exitDoor. 1-N are rooms
  public int activationID = -1; // Which switch causes me to animate when true? -1 is invalid
  private HashSet<Tile> webNeighbours = new HashSet<Tile>();

  public Tile(int x, int y) {
    super(x, y);
    setTexture("pitC");
  }

  public void setIsFloor(Room room) {
    isFloor = true;
    myRoom = room;
    int floor = Utility.r.nextInt(100);
    if (floor <= 22)  setTexture("floor" + Integer.toString(floor));
    else setTexture("floor");
  }

  public void setIsWeb() {
    // TODO don't do this on every frame.... waste
    updateNeighbours(); // Update my neighbours
    HashSet<Tile> cpy = (HashSet<Tile>) webNeighbours.clone();
    for (Tile t : cpy) {
      t.updateNeighbours(); // And theirs (they need to add me)
    }
    if (isWeb) return;
    setTexture("webA");
    isWeb = true;
  }

  public void addSwitchSensor(int ID) {
    switchID = ID;
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(getX()/Param.TILE_SIZE + .5f, getY()/Param.TILE_SIZE + .5f);
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);
    body.setUserData(this);
    bodyDef.type = BodyDef.BodyType.StaticBody;
    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(1f);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.filter.categoryBits = Param.TORCH_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY; // I collide with
    fixtureDef.isSensor = true;
    body.createFixture(fixtureDef);
    circleShape.dispose();
  }

  @Override
  public void act (float delta) {
    // TODO do we need to call on the super here? We don't animate any of these so prob not
    if (switchID >= 0) {
      currentFrame = (int) ((GameState.getInstance().progress[switchID] / (float) Param.SWITCH_TIME) * nFrames);
    } else if (activationID >= 0 && GameState.getInstance().progress[activationID] == Param.SWITCH_TIME) {
      if (GameState.getInstance().frame % Param.ANIM_SPEED == 0) ++currentFrame;
    }
  }

  public Room getTilesRoom() {
    return myRoom;
  }

  public boolean getIsFloor() {
    return isFloor;
  }

  public boolean getIsWeb() {
    return isWeb;
  }

  public boolean getHasPhysics() {
    return hasPhysics;
  }

  public void setHasPhysics(boolean p) {
    hasPhysics = p;
  }

  public double getHeuristic(Tile goal) {
    return Math.sqrt( Math.pow( getX() - goal.getX(), 2) + Math.pow( getY() - goal.getY(), 2) );
  }

  public double getTraversalCost(Tile neighbour) {
    return 1f; // Web tiles are always one apart and always accessible
  }

  private void updateNeighbours() {
    webNeighbours.clear();
    Sprites.getInstance().getNeighbourWeb((int)getX()/Param.TILE_SIZE, (int)getY()/Param.TILE_SIZE, webNeighbours);
  }

  public Set<Tile> getNeighbours() {
    return webNeighbours;
  }
}
