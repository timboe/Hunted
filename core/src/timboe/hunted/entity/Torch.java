package timboe.hunted.entity;

import box2dLight.ConeLight;
import box2dLight.PositionalLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;

/**
 * Created by Tim on 02/01/2017.
 */
public class Torch extends EntityBase {

  public boolean isOn = false;
  private boolean isPartial;
  private Vector2 lightEffectPos;
  private boolean needsSecondLight;

  public int nLight = 0;
  public PositionalLight[] torchLight = {null,null};
  public float torchDistanceRef;
  private float torchDistanceCurrent;
  private float torchDistanceTarget;


  public Torch(int x, int y) {
    super (x,y);
  }

  public Torch(float x, float y, float lX, float lY, float r, boolean partial, float angle) {
    super((int)x, (int)y);
    isPartial = partial;
    setAsTorchBody(x,y,r);
    lightEffectPos = new Vector2(lX, lY);
    // If the actual light is not in the same position as its effect - or the actual light is partial, need another
    needsSecondLight = (isPartial || body.getPosition().dst(lightEffectPos) < 1e-4);
    setMoveDirection(angle, false);
  }

  public void setAsTorchBody(float x, float y, float r) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(x, y);
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);
    body.setUserData(this);
    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(r);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.filter.categoryBits = Param.WORLD_ENTITY | Param.TORCH_SENSOR_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY; // I collide with
    fixtureDef.isSensor = true;
    body.createFixture(fixtureDef);
    circleShape.dispose();
  }

  public void addTorchToEntity(boolean ignoreSelf, boolean staticL, boolean point, float range, Color c, boolean addToBody, Vector2 loc) {
    torchDistanceRef = Param.WALL_TORCH_STRENGTH;
    if (point) {
      torchLight[nLight] = new ConeLight(Physics.getInstance().rayHandler,
        Param.RAYS,
        c,
        torchDistanceRef,
        0f, 0f, body.getAngle(), 180f);
    } else {
      torchLight[nLight] = new ConeLight(Physics.getInstance().rayHandler,
        Param.RAYS,
        c,
        torchDistanceRef,
        0f, 0f, body.getAngle(), range);
    }
    torchLight[nLight].setContactFilter(Param.SENSOR_ENTITY,
      (short)0,
      (short)(Param.PLAYER_ENTITY|Param.BIGBAD_ENTITY|Param.WORLD_ENTITY)); // I am a, 0, I collide with
    if (addToBody) {
      torchLight[nLight].attachToBody(body);
    } else {
      torchLight[nLight].setPosition(loc.x, loc.y);
      torchLight[nLight].setXray(true);
    }
    torchLight[nLight].setStaticLight(staticL);
    torchLight[nLight].setIgnoreAttachedBody(ignoreSelf);
    ++nLight;
  }

  public void flicker() {
    //if (Math.abs(torchDistanceCurrent - torchDistanceTarget) < 1e-3) {
    //  torchDistanceTarget = torchDistanceRef + ((float)Utility.r.nextGaussian() * Param.TORCH_FLICKER);
    //}
    torchDistanceCurrent = torchDistanceRef;// rchDistanceCurrent + (0.1f * (torchDistanceTarget - torchDistanceCurrent));
    torchLight[0].setDistance(torchDistanceCurrent);
  }

  public void doCollision() {
    if (isOn) return;
    isOn = true;
    float range = isPartial ? 90f : 180f;
    Gdx.app.log("Torch", "Turning on " + this);
    addTorchToEntity(true, false, false, range, Param.WALL_FLAME_CAST, true, null);
    Physics.getInstance().litTorches.add(this);
    Sprites.getInstance().addFlameEffect(lightEffectPos);
    if (needsSecondLight) {
      addTorchToEntity(true, false, true, 180f, Param.WALL_FLAME_SPOT, false, lightEffectPos);
      torchLight[1].setDistance(Param.SMALL_TORCH_STRENGTH);
    }
  }

}
