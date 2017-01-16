package timboe.hunted.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import timboe.hunted.Param;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;

/**
 * Created by Tim on 16/01/2017.
 */
public class Switch extends EntityBase {

  public int switchID = -1; // -1 is invalid, 0=exitDoor. 1-N are rooms

  public Switch(int x, int y, int id) {
    super(x, y);
    switchID = id;
    setTexture("switch",7);
    addSwitchSensor();
  }

  @Override
  public void act (float delta) {
    if (switchID >= 0) {
      currentFrame = (int) ((Math.min(GameState.getInstance().progress[switchID], Param.SWITCH_TIME-1) / (float) Param.SWITCH_TIME) * nFrames);
    }
  }

  public void addSwitchSensor() {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(getX()/ Param.TILE_SIZE + .5f, getY()/Param.TILE_SIZE + .5f);
    body = Physics.getInstance().world.createBody(bodyDef);
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
}
