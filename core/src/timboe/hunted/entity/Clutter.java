package timboe.hunted.entity;

import com.badlogic.gdx.physics.box2d.*;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;

/**
 * Created by Tim on 15/01/2017.
 */
public class Clutter extends EntityBase {

  private final int nClutters = 17;
  private int clutter;

  public Clutter(int x, int y) {
    super(x, y);
    clutter = Utility.r.nextInt(nClutters);
    setTexture("clutter" + Integer.toString(clutter));
  }

  public void act (float delta) {
    updatePosition();
  }

  public void setAsClutter() {
    float w = getWidth()/(2*Param.TILE_SIZE);
    float h = getHeight()/(2*Param.TILE_SIZE);
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set((getX() / Param.TILE_SIZE) + w, (getY() / Param.TILE_SIZE) + h);
    bodyDef.fixedRotation = true; // No spiny physics
    body = Physics.getInstance().world.createBody(bodyDef);
    body.setUserData(this);
    Shape shape;
    if (clutter < 10) {
      shape = new CircleShape();
      shape.setRadius(.9f * w);
    } else if (clutter == 9) {
      shape = new PolygonShape();
      ((PolygonShape)shape).setAsBox(w, .75f * h);
    } else if (clutter < 12) {
      shape = new PolygonShape();
      ((PolygonShape)shape).setAsBox(.8f * w, h);
    } else if (clutter == 12) {
      shape = new PolygonShape();
      ((PolygonShape)shape).setAsBox(.75f * w, .5f * h);
    } else if (clutter < 15) {
      shape = new CircleShape();
      shape.setRadius(.8f * w);
    } else {
      shape = new PolygonShape();
      ((PolygonShape)shape).setAsBox(.5f * w, .5f * h);
    }
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.filter.categoryBits = Param.WORLD_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY | Param.WORLD_ENTITY | Param.TORCH_ENTITY; // I collide with
    body.createFixture(fixtureDef);
    shape.dispose();
  }

}
