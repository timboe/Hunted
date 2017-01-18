package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;
import timboe.hunted.manager.Textures;

/**
 * Created by Tim on 15/01/2017.
 */
public class Chest extends EntityBase {

  public boolean chestOpened = false;
  private final int nTreasure = 5;
  protected TextureRegion treasure[] = new TextureRegion[nTreasure];
  private  TextureRegion chestMask;
  private int treasureID;
  private int treasureHeight = -1;

  public Chest(int x, int y) {
    super(x, y);
    setTexture("chest",6);
    chestMask = Textures.getInstance().getTexture("chestMask");
    for (int i = 0; i < nTreasure; ++i) {
      treasure[i] = Textures.getInstance().getTexture("treasure" + Integer.toString(i));
    }
    treasureID = Utility.r.nextInt(nTreasure);
    setAsChest();
  }

  @Override
  public void act (float delta) {
    updatePosition();
    if (chestOpened && currentFrame < nFrames-1 && GameState.getInstance().frame % Param.ANIM_SPEED/2 == 0) {
      ++currentFrame;
    } else if (currentFrame == nFrames-1 && treasureHeight < Param.TILE_SIZE) {
      ++treasureHeight;
    }
  }

  public void setAsChest() {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set((getX() / Param.TILE_SIZE) + .5f, (getY() / Param.TILE_SIZE) + .5f);
    bodyDef.fixedRotation = true; // No spiny physics
    body = Physics.getInstance().world.createBody(bodyDef);
    body.setUserData(this);
    PolygonShape boxShape = new PolygonShape();
    boxShape.setAsBox(.5f, .5f);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 0.5f;
    fixtureDef.friction = 0.8f;
    fixtureDef.restitution = 0.1f;
    fixtureDef.filter.categoryBits = Param.CLUTTER_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.WORLD_COLLIDES; // I collide with
    body.createFixture(fixtureDef);
    boxShape.dispose();
  }

  public void updatePhysics() {
    if (!body.isAwake()) return;
    Vector2 lv = body.getLinearVelocity();
    // Apply retarding force
    float deltaVX = 0 - lv.x;
    float deltaVY = 0 - lv.y;
    float mass = Param.CHEST_INERTIA_MOD * body.getMass();
    body.applyLinearImpulse(mass * deltaVX, mass * deltaVY, body.getPosition().x, body.getPosition().y, true);
  }


  @Override
  public void draw(Batch batch, float alpha) {
    super.draw(batch, alpha);
    if (treasureHeight >= 0) {
      batch.draw(chestMask,this.getX(),this.getY());
      batch.draw(treasure[treasureID] ,this.getX(),this.getY() + treasureHeight);

    }
  }

}
