package timboe.hunted.entity;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Sprites;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Physics;

/**
 * Created by Tim on 30/12/2016.
 */
public class EntityBase extends Actor {
  protected Texture texture = null;
  protected Body body = null;
  protected Vector2 grid = null;
  protected float offsetMod = 0f;
  protected float angle = 0;
  protected boolean moving = false;
  protected PointLight torch = null;


  public EntityBase(int x, int y) {
    grid = new Vector2(x,y);
    setX(x * HuntedGame.TILE_SIZE);
    setY(y * HuntedGame.TILE_SIZE);
    setBounds(getX(),getY(),HuntedGame.TILE_SIZE,HuntedGame.TILE_SIZE);
  }

  public void setPhysicsPosition(float x, float y) {
    grid.set(x,y);
    x *= HuntedGame.TILE_SIZE;
    y *= HuntedGame.TILE_SIZE;
    body.setTransform((x + getWidth()/2f) / (float)HuntedGame.TILE_SIZE,
      (y + getHeight()/2f) / (float)HuntedGame.TILE_SIZE,
      0f);
  }

  public void setPhysicsBody(float width, float height) {
    // This may extend over many sprites - make sure we flag them all
    for (int w = 0; w < (int)width; ++ w) {
      for (int h = 0; h < (int)height; ++h) {
        Sprites.getInstance().getTile((int)(grid.x + w), (int)(grid.y + h)).setHasPhysics(true);
      }
    }

    BodyDef bodyDef = new BodyDef();
    float newWidth2 = (width * getWidth()) / 2f;
    float newHeight2 = (height * getHeight()) / 2f;
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.fixedRotation = true; // No spiny
    bodyDef.position.set((getX() + newWidth2) / (float)HuntedGame.TILE_SIZE,
      (getY() + newHeight2) / (float)HuntedGame.TILE_SIZE);
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);

    PolygonShape boxShape = new PolygonShape();
    boxShape.setAsBox(newWidth2 / (float) HuntedGame.TILE_SIZE, newHeight2 / (float) HuntedGame.TILE_SIZE);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 1f;
    body.createFixture(fixtureDef);

    boxShape.dispose();
  }

  public void setPlayerBody(float scale, float offset) {
    BodyDef bodyDef = new BodyDef();
    float newWidth2 = (scale * getWidth()) / 2f;
    float heightMod = getHeight() / 2f;
    offsetMod = getHeight() * offset;
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.fixedRotation = true; // No spiny
    bodyDef.position.set((getX() + newWidth2) / (float)HuntedGame.TILE_SIZE,
      (getY() + heightMod - offsetMod) / (float)HuntedGame.TILE_SIZE);
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);

    CircleShape circleShape = new CircleShape();
    circleShape.setRadius(newWidth2 / (float) HuntedGame.TILE_SIZE);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circleShape;
    fixtureDef.density = 1f;
    body.createFixture(fixtureDef);
    circleShape.dispose();
  }

  public void updatePhysics() {
    if (moving) {
      body.setLinearVelocity((float) (HuntedGame.PLAYER_SPEED * Math.sin(angle)),
        (float) (HuntedGame.PLAYER_SPEED * Math.cos(angle)));
    } else {
      body.setLinearVelocity(0f,0f);
    }
  }

  public void updatePosition() {
    float x = (body.getPosition().x * HuntedGame.TILE_SIZE) - getWidth()/2;
    float y = (body.getPosition().y * HuntedGame.TILE_SIZE) - getHeight()/2 + offsetMod;
    setPosition(x,y);
    //if (torch != null) torch.setPosition(x,y);
  }


  @Override
  public void draw(Batch batch, float alpha){
    batch.draw(texture,this.getX(),this.getY());
  }

}
