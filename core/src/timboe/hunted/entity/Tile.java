package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Physics;
import timboe.hunted.world.Room;

import java.util.Random;

/**
 * Created by Tim on 28/12/2016.
 */
public class Tile extends EntityBase {

  private boolean isFloor = false;
  private boolean hasPhysics = false;
  private boolean isWeb = false;
  private Room myRoom = null;
  public int switchID = -2; // -1 is entry/exit. 0-N are rooms

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
    fixtureDef.filter.categoryBits = Param.SENSOR_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY; // I collide with
    fixtureDef.isSensor = true;
    body.createFixture(fixtureDef);
    circleShape.dispose();
  }

  @Override
  public void act (float delta) {
    // TODO do we need to call on the super here? We don't animate any of these so prob not
    if (switchID == -2) return;
    if (switchID == -1 && Physics.getInstance().switchEntry) ++currentFrame;
    else if (switchID >= 0 && Physics.getInstance().switchKeyRoom[switchID]) ++currentFrame;
  }

  public Room getTilesRoom() {
    return myRoom;
  }

  public boolean getIsFloor() {
    return isFloor;
  }

  public boolean getHasPhysics() {
    return hasPhysics;
  }

  public void setHasPhysics(boolean p) {
    hasPhysics = p;
  }

//  public void setIsCorridor() {
//    texture =  Textures.getInstance().dummyCorridor;
//  }
}
