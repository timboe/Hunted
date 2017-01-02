package timboe.hunted.entity;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
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
  protected TextureRegion textureRegion = null;
  protected Body body = null;
  protected Rectangle worldBox = null;
  protected float offsetMod = 0f;
  protected float angle = 0;
  protected float speed = 0;
  protected boolean moving = false;
  protected PointLight torch = null;


  public EntityBase(int x, int y) {
    worldBox = new Rectangle(x, y,1f,1f);
    setBounds(x * Param.TILE_SIZE, y * Param.TILE_SIZE, Param.TILE_SIZE, Param.TILE_SIZE);
  }

  public void setTexture(String name) {
    textureRegion = Textures.getInstance().getTexture(name);
    setWidth(textureRegion.getRegionWidth());
    setHeight(textureRegion.getRegionHeight());
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

    PolygonShape boxShape = new PolygonShape();
    boxShape.setAsBox(newWidth2, newHeight2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 1f;
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

    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(newWidth2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.density = 1f;
    body.createFixture(fixtureDef);
    circleShape.dispose();
  }

  public void updatePhysics() {
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
    moving = true;
    angle = (float)a;
    body.setTransform(body.getPosition(), angle);
  }

  @Override
  public void draw(Batch batch, float alpha){
    batch.draw(textureRegion ,this.getX(),this.getY());
  }

}
