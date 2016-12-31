package timboe.hunted.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Physics;

/**
 * Created by Tim on 30/12/2016.
 */
public class EntityBase extends Actor {
  protected Texture texture;
  protected Body body;

  public EntityBase(int x, int y) {
    setX(x * HuntedGame.TILE_SIZE);
    setY(y * HuntedGame.TILE_SIZE);
    setBounds(getX(),getY(),HuntedGame.TILE_SIZE,HuntedGame.TILE_SIZE);
    body = null;
  }

  public void setPhysicsPosition(float x, float y) {
    x *= HuntedGame.TILE_SIZE;
    y *= HuntedGame.TILE_SIZE;
    body.setTransform((x + getWidth()/2f) / (float)HuntedGame.TILE_SIZE,
      (y + getHeight()/2f) / (float)HuntedGame.TILE_SIZE,
      0f);
  }

  public void setPhysicsBody(BodyDef.BodyType bodyType, float width, float height) {
    BodyDef bodyDef = new BodyDef();
    float newWidth2 = (width * getWidth()) / 2f;
    float newHeight2 = (height * getHeight()) / 2f;
    bodyDef.type = bodyType;
    bodyDef.fixedRotation = true; // No spiny
    bodyDef.position.set((getX() + newWidth2) / (float)HuntedGame.TILE_SIZE,
      (getY() + newHeight2) / (float)HuntedGame.TILE_SIZE);

    // Create a body in the world using our definition
    body = Physics.getInstance().worldBox2D.createBody(bodyDef);
    // Now define the dimensions of the physics shape
    PolygonShape boxShape = new PolygonShape();
    // Basically set the physics polygon to a box with the same dimensions
    boxShape.setAsBox(newWidth2 / (float)HuntedGame.TILE_SIZE,newHeight2 / (float)HuntedGame.TILE_SIZE);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = boxShape;
    fixtureDef.density = 1f;
    body.createFixture(fixtureDef);

    // Shape is the only disposable of the lot, so get rid of it
    boxShape.dispose();
  }

  @Override
  public void draw(Batch batch, float alpha){
    batch.draw(texture,this.getX(),this.getY());
  }

}
