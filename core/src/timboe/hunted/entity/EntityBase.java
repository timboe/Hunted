package timboe.hunted.entity;

import box2dLight.ConeLight;
import box2dLight.PositionalLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.Param;
import timboe.hunted.render.Sprites;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Physics;
import timboe.hunted.world.Room;

/**
 * Created by Tim on 30/12/2016.
 */
public class EntityBase extends Actor {
  protected TextureRegion[] textureRegion = new TextureRegion[10];
  protected int currentFrame;
  protected int nFrames;

  protected Body body = null;
  protected Rectangle worldBox = null;
  protected float offsetMod = 0f;
  private float angle = 0;
  protected float speed = 0;
  private boolean moving = false;
  public PositionalLight torchLight = null;



  public EntityBase(int x, int y) {
    worldBox = new Rectangle(x, y,1f,1f);
    setBounds(x * Param.TILE_SIZE, y * Param.TILE_SIZE, Param.TILE_SIZE, Param.TILE_SIZE);
  }

  public Body getBody() { return body; }

  public void setTexture(String name) {
    setTexture(name, 0);
  }

  public void setTexture(String name, int frames) {
    nFrames = frames;
    currentFrame = 0;
    if (frames == 0) {
      textureRegion[0] = Textures.getInstance().getTexture(name);
    } else {
      for (int i = 0; i < frames; ++i) {
        textureRegion[i] = Textures.getInstance().getTexture(name + Integer.toString(i));
      }
    }
    setWidth(textureRegion[0].getRegionWidth());
    setHeight(textureRegion[0].getRegionHeight());
  }


  public void setPhysicsPosition(float x, float y) {
    worldBox.setPosition(x,y);
    body.setTransform(x + worldBox.width/2f, y + worldBox.height/2f, angle);
  }

  public void setAsPhysicsBody(float width, float height) {
    // This may extend over many sprites - make sure we flag them all
    for (int w = 0; w < (int)width; ++ w) {
      for (int h = 0; h < (int)height; ++h) {
        Sprites.getInstance().getTile((int)(worldBox.x + w), (int)(worldBox.y + h)).setHasPhysics(true);
      }
    }

    BodyDef bodyDef = new BodyDef();
    float newWidth2 = (width * worldBox.width) / 2f;
    float newHeight2 = (height * worldBox.height) / 2f;
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(worldBox.x + newWidth2, worldBox.y + newHeight2);
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);
    body.setUserData(this);

    PolygonShape boxShape = new PolygonShape();
    boxShape.setAsBox(newWidth2, newHeight2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 1f;
    fixtureDef.filter.categoryBits = Param.WORLD_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_ENTITY | Param.SENSOR_ENTITY; // I collide with
    body.createFixture(fixtureDef);

    boxShape.dispose();
  }

  public void setAsPlayerBody(float scale, float offset) {
    BodyDef bodyDef = new BodyDef();
    float newWidth2 = (scale * worldBox.width) / 2f;
    float heightMod = worldBox.height / 2f;
    offsetMod = worldBox.height * offset;
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.fixedRotation = true; // No spiny physics
    bodyDef.position.set(worldBox.x + newWidth2, worldBox.y + heightMod - offsetMod);
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);
    body.setUserData(this);

    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(newWidth2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.density = 1f;
    fixtureDef.filter.categoryBits = Param.PLAYER_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.WORLD_ENTITY | Param.BIGBAD_ENTITY | Param.PLAYER_ENTITY | Param.SENSOR_ENTITY; // I collide with
    if (this instanceof BigBad) {
      fixtureDef.filter.categoryBits = Param.BIGBAD_ENTITY; // I am a
//      fixtureDef.filter.maskBits = Param.SENSOR_ENTITY; // I collide with
    }
    body.createFixture(fixtureDef);
    circleShape.dispose();
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

  public void addTorchToEntity(boolean ignoreSelf, boolean staticL, boolean point, float range, Color c, float offX, float offY) {
    if (point) {
      torchLight = new ConeLight(Physics.getInstance().rayHandler,
        Param.RAYS,
        c,
        Param.WALL_TORCH_STRENGTH,
        0f, 0f, body.getAngle(), 180f);
    } else {
      torchLight = new ConeLight(Physics.getInstance().rayHandler,
        Param.RAYS,
        c,
        Param.WALL_TORCH_STRENGTH,
        0f, 0f, body.getAngle(), range);
    }
    torchLight.setContactFilter(Param.SENSOR_ENTITY, (short)0, (short)(Param.PLAYER_ENTITY|Param.BIGBAD_ENTITY|Param.WORLD_ENTITY)); // I am a, 0, I collide with
    torchLight.attachToBody(body, offX, offY);
    torchLight.setStaticLight(staticL);
    torchLight.setIgnoreAttachedBody(ignoreSelf);
  }

  public void setMoving(boolean m) {
    moving = m;
    if (moving) {
      body.setLinearVelocity((float) (speed * Math.cos(angle)), (float) (speed * Math.sin(angle)));
    } else {
      body.setLinearVelocity(0f,0f);
    }
  }

  public void updatePosition() {
    float x = (body.getPosition().x * Param.TILE_SIZE) - getWidth()/2;
    float y = (body.getPosition().y * Param.TILE_SIZE) - getHeight()/2 + offsetMod;
    setPosition(x,y);
    worldBox.setPosition((float)Math.floor(body.getPosition().x), (float)Math.floor(body.getPosition().y));
  }

  public Tile getTileUnderEntity() {
    return Sprites.getInstance().getTile((int) body.getPosition().x, (int) body.getPosition().y);
  }

  public Room getRoomUnderEntity() {
    return getTileUnderEntity().getTilesRoom();
  }

  public void setMoveDirection(double a, boolean move) {
    while (a < 0) a += 2*Math.PI;
    while (a >= 2*Math.PI) a -= 2*Math.PI;
    angle = (float)a;
    body.setTransform(body.getPosition(), angle);
    setMoving(move);
  }

  @Override
  public void draw(Batch batch, float alpha) {
    batch.draw(textureRegion[currentFrame] ,this.getX(),this.getY());
  }

}
