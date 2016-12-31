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
public class Player extends EntityBase {


  final double speed = 10;
  private double angle = 0;

  public Player() {
    super(0,0);
    texture = Textures.getInstance().dummyPlayer;
    angle = -1;
    setPhysicsBody(BodyDef.BodyType.DynamicBody, 1, 1);
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

    if (angle >= 0) {
      body.setLinearVelocity((float) (speed * Math.sin(angle)), (float) (speed * Math.cos(angle)));
    } else {
      body.setLinearVelocity(0f,0f);
    }


  }

  public void updatePosition() {
    setPosition((body.getPosition().x * HuntedGame.TILE_SIZE) - getWidth()/2,
      (body.getPosition().y * HuntedGame.TILE_SIZE) - getHeight()/2 );
  }

}
