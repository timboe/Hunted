package timboe.hunted.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;

/**
 * Created by Tim on 15/01/2017.
 */
public class Clutter extends EntityBase {

  private final int nClutters = 17;

  public Clutter(int x, int y) {
    super(x, y);
    setTexture("clutter" + Integer.toString(Utility.r.nextInt(nClutters)));
  }

  public void act (float delta) {
    updatePosition();
  }

  public void setAsClutter() {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set((getX() / Param.TILE_SIZE) + getWidth()/(2*Param.TILE_SIZE),
      (getY() / Param.TILE_SIZE) + getHeight()/(2*Param.TILE_SIZE));
    bodyDef.fixedRotation = true; // No spiny physics
    body = Physics.getInstance().world.createBody(bodyDef);
    body.setUserData(this);
    PolygonShape boxShape = new PolygonShape();
    boxShape.setAsBox(getWidth()/(2*Param.TILE_SIZE), getHeight()/(2*Param.TILE_SIZE));
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 0.5f;
    fixtureDef.friction = 0.8f;
    fixtureDef.restitution = 0.1f;
    fixtureDef.filter.categoryBits = Param.WORLD_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY | Param.WORLD_ENTITY | Param.TORCH_ENTITY; // I collide with
    body.createFixture(fixtureDef);
    boxShape.dispose();
  }

}
