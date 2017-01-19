package timboe.hunted.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import timboe.hunted.Param;
import timboe.hunted.manager.Physics;

/**
 * Created by Tim on 19/01/2017.
 */
public class WinMask extends EntityBase {
  public WinMask(int x, int y) {
    super(x, y);
    setTexture("exitMask");
    addSensor();
  }

  public void addSensor() {
    float w = getWidth()/(2*Param.TILE_SIZE);
    float h = getHeight()/(2*Param.TILE_SIZE);
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set((getX() / Param.TILE_SIZE) + w, (getY() / Param.TILE_SIZE) + h);
    body = Physics.getInstance().world.createBody(bodyDef);
    body.setUserData(this);
    bodyDef.type = BodyDef.BodyType.StaticBody;
    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(1.5f);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.filter.categoryBits = Param.SENSOR_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY; // I collide with
    fixtureDef.isSensor = true;
    body.createFixture(fixtureDef);
    circleShape.dispose();
  }
}
