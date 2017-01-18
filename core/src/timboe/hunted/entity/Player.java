package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.Physics;


/**
 * Created by Tim on 28/12/2016.
 */
public class Player extends ParticleEffectActor {

  Body lightAttachment;
  private final float torchOff = .5f;

  public Player() {
    super(0,0);
    setTexture("playerE");
    speed = Param.PLAYER_SPEED;
    setAsPlayerBody(0.5f, 0.25f);

    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    lightAttachment = Physics.getInstance().world.createBody(bodyDef);
    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(.05f);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    lightAttachment.createFixture(fixtureDef);
    circleShape.dispose();

    addTorchToEntity(180f, Param.PLAYER_TORCH_STRENGTH, Param.PLAYER_FLAME,  false, null);
    torchLight[0].setContactFilter(Param.TORCH_ENTITY,
      (short)0,
      (short)(Param.BIGBAD_ENTITY|Param.WORLD_ENTITY|Param.CLUTTER_ENTITY)); // I am a, 0, I collide with
    torchLight[0].attachToBody(lightAttachment);
    torchLight[0].setIgnoreAttachedBody(true);

    particleEffect = Utility.getNewFlameEffect();
    PAOffsetY = Param.TILE_SIZE/2;
    PAOffsetX = Param.TILE_SIZE/4;
  }

  public void updatePhysics() {
    getRoomUnderEntity().addToScent( Param.PLAYER_SMELL );  // Add player smelliness
    // Do force based movement
    Vector2 lv = body.getLinearVelocity();
    float targetX = moving ? (float)(speed * Math.cos(angle)) : 0f;
    float targetY = moving ? (float)(speed * Math.sin(angle)) : 0f;
    float deltaVX = targetX - lv.x;
    float deltaVY = targetY - lv.y;
    float mass = Param.PLAYER_INERTIA_MOD * body.getMass();
    body.applyLinearImpulse(mass * deltaVX, mass * deltaVY, body.getPosition().x, body.getPosition().y, true);
    flicker();
  }

  @Override
  public void updatePosition() {
    lightAttachment.setTransform(body.getPosition().x,
      body.getPosition().y + torchOff, body.getAngle());
    super.updatePosition();
  }

  public void updateDirection(boolean keyN, boolean keyE, boolean keyS, boolean keyW) {
    moving = true;
    if (keyN && keyE) setMoveDirection(Math.PI / 4f);
    else if (keyE && keyS) setMoveDirection(7f * Math.PI / 4f);
    else if (keyS && keyW) setMoveDirection(5f * Math.PI / 4f);
    else if (keyW && keyN) setMoveDirection(3f * Math.PI / 4f);
    else if (keyN) setMoveDirection(Math.PI / 2f);
    else if (keyE) setMoveDirection(0);
    else if (keyS) setMoveDirection(3f * Math.PI / 2f);
    else if (keyW) setMoveDirection(Math.PI);
    else moving = false;
  }




}
