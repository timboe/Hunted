package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.Param;
import timboe.hunted.manager.Sprites;
import timboe.hunted.manager.Textures;
import timboe.hunted.manager.Physics;
import timboe.hunted.world.Room;

/**
 * Created by Tim on 30/12/2016.
 */
public class EntityBase extends Actor {
  protected TextureRegion webTexture = null;
  protected TextureRegion[] textureRegion = new TextureRegion[Param.MAX_FRAMES];
  public int currentFrame = 0;
  protected int nFrames;

  protected Body body = null;
  protected Rectangle worldBox = null; // TODO get rid of this
  protected float offsetMod = 0f;
  protected float angle = 0;
  protected float speed = 0;
  protected boolean moving = false;
  protected float webTint = 0;

  public EntityBase(int x, int y) {
    worldBox = new Rectangle(x, y,1f,1f);
    setBounds(x * Param.TILE_SIZE, y * Param.TILE_SIZE, Param.TILE_SIZE, Param.TILE_SIZE);
  }

  public Body getBody() { return body; }

  public void setWebTexture(String name) {
    webTexture = Textures.getInstance().getTexture(name);
  }


  public void setTexture(String name) {
    setTexture(name, 1, false);
  }

  public void setTexture(String name, int frames) {
    setTexture(name, frames, false);
  }

  public void setTexture(String name, int frames, boolean dupeMiddleFrame) {
    if (frames > Param.MAX_FRAMES) {
      Gdx.app.error("setTexture", "Too many frames " + frames + " on " + name);
      Gdx.app.exit();
    }
    nFrames = frames;
    currentFrame = 0;
    if (frames == 1) {
      textureRegion[0] = Textures.getInstance().getTexture(name);
    } else {
      for (int i = 0; i < frames; ++i) {
        textureRegion[i] = Textures.getInstance().getTexture(name + Integer.toString(i));
      }
    }
    setWidth(textureRegion[0].getRegionWidth());
    setHeight(textureRegion[0].getRegionHeight());
    if (dupeMiddleFrame && frames == 3) {
      textureRegion[frames] = textureRegion[1];
      ++nFrames;
    }
  }


  public void setPhysicsPosition(float x, float y) {
    worldBox.setPosition(x,y);
    body.setTransform(x + worldBox.width/2f, y + worldBox.height/2f, angle);
  }

  public void setAsPhysicsBody(float width, float height) {
    setAsPhysicsBody(worldBox.x, worldBox.y, width, height);
  }

  public void setAsPhysicsBody(float x, float y, float width, float height) {
    // This may extend over many sprites - make sure we flag them all
    for (int w = 0; w < (int)width; ++ w) {
      for (int h = 0; h < (int)height; ++h) {
        Sprites.getInstance().getTile((int)(x + w), (int)(y + h)).setHasPhysics(true);
      }
    }

    BodyDef bodyDef = new BodyDef();
    float newWidth2 = (width * worldBox.width) / 2f;
    float newHeight2 = (height * worldBox.height) / 2f;
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(x + newWidth2, y + newHeight2);
    body = Physics.getInstance().world.createBody(bodyDef);
    body.setUserData(this);

    PolygonShape boxShape = new PolygonShape();
    boxShape.setAsBox(newWidth2, newHeight2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 1f;
    fixtureDef.filter.categoryBits = Param.WORLD_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.WORLD_COLLIDES; // I collide with
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
    body = Physics.getInstance().world.createBody(bodyDef);
    body.setUserData(this);

    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(newWidth2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.density = 1f;
    fixtureDef.filter.categoryBits = Param.PLAYER_ENTITY; // I am a
    fixtureDef.filter.maskBits = Param.PLAYER_COLLIDES; // I collide with
    if (this instanceof BigBad) {
      fixtureDef.filter.categoryBits = Param.BIGBAD_ENTITY; // I am a
      fixtureDef.filter.maskBits = Param.BIGBAD_COLLIDES; // I collide with
    }
    body.createFixture(fixtureDef);
    circleShape.dispose();
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

  public void setMoveDirection(double a) {
    while (a < 0) a += 2*Math.PI;
    while (a >= 2*Math.PI) a -= 2*Math.PI;
    angle = (float)a;
    body.setTransform(body.getPosition(), angle);
  }

  @Override
  public void draw(Batch batch, float alpha) {
//    if (currentFrame > 0) Gdx.app.log("DBG","iam "+textureRegion[0]+" "+currentFrame + " mod " + nFrames + " is " + currentFrame%nFrames);
    batch.draw(textureRegion[currentFrame % nFrames] ,this.getX(),this.getY());
    if (webTexture != null) {
      batch.setColor(1f,1f - webTint,1f - webTint,1f); //red
      batch.draw(webTexture, this.getX(), this.getY());
      batch.setColor(1f,1f,1f,1f);
    }
  }

//  // TODO check for danger here
//  public int hashCode() {
//    return Utility.xyToID((int)getX(), (int)getY());
//  }


}
