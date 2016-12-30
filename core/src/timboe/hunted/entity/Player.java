package timboe.hunted.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Textures;
import timboe.hunted.screen.GameScreen;

/**
 * Created by Tim on 28/12/2016.
 */
public class Player extends Actor{

  Texture texture = Textures.getInstance().dummyPlayer;

  final double speed = 10;
  private double angle = 0;
  private Body body;

  public Player() {
    angle = -1;
    setBounds(getX(),getY(),HuntedGame.TILE_SIZE,HuntedGame.TILE_SIZE);
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.fixedRotation = true; // No spiny
    bodyDef.position.set(getX(),getY());
    bodyDef.position.set((getX() + getWidth()/2) / (float)HuntedGame.TILE_SIZE,
      (getY() + getHeight()/2) / (float)HuntedGame.TILE_SIZE);

    // Create a body in the world using our definition
    body = HuntedGame.worldBox2D.createBody(bodyDef);
    // Now define the dimensions of the physics shape
    PolygonShape shape = new PolygonShape();
    // Basically set the physics polygon to a box with the same dimensions
    shape.setAsBox(getWidth()/2 / (float)HuntedGame.TILE_SIZE, getHeight()/2 / (float)HuntedGame.TILE_SIZE);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.density = 1f;
    Fixture fixture = body.createFixture(fixtureDef);

    // Shape is the only disposable of the lot, so get rid of it
    shape.dispose();


  }

  public void updateDirection(boolean keyN, boolean keyE, boolean keyS, boolean keyW) {
    if (keyN && keyE) setMoveDirection(Math.PI / 4.);
    else if (keyE && keyS) setMoveDirection(3. * Math.PI / 4);
    else if (keyS && keyW) setMoveDirection(5. * Math.PI / 4);
    else if (keyW && keyN) setMoveDirection(7. * Math.PI / 4);
    else if (keyN) setMoveDirection(0);
    else if (keyE) setMoveDirection(Math.PI / 2);
    else if (keyS) setMoveDirection(Math.PI);
    else if (keyW) setMoveDirection(3. * Math.PI / 2);
    else setMoveDirection(-1);
  }


  public void setMoveDirection(double an) {
    angle = an;
  }

  public void updatePhysics() {
//      setPosition(body.getPosition().x, body.getPosition().y);

    if (angle >= 0) {
      body.setLinearVelocity((float) (speed * Math.sin(angle)), (float) (speed * Math.cos(angle)));
//      moveBy((float) (speed * Math.sin(angle)), (float) (speed * Math.cos(angle)));
    } else {
      body.setLinearVelocity(0f,0f);
    }

    setPosition((body.getPosition().x * HuntedGame.TILE_SIZE) - getWidth()/2,
      (body.getPosition().y * HuntedGame.TILE_SIZE) - getHeight()/2 );
  }

  @Override
  public void draw(Batch batch, float alpha) {
    batch.draw(texture,this.getX(),this.getY());
  }
}
