package timboe.hunted.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Textures;

/**
 * Created by Tim on 28/12/2016.
 */
public class Player extends Actor{

  Texture texture = Textures.getInstance().dummyPlayer;

  final double speed = 10;
  private double angle = 0;

  public Player() {
    angle = -1;
    setBounds(getX(),getY(),HuntedGame.TILE_SIZE,HuntedGame.TILE_SIZE);
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
      moveBy((float) (speed * Math.sin(angle)), (float) (speed * Math.cos(angle)));
    }
  }

  @Override
  public void draw(Batch batch, float alpha){
    batch.draw(texture,this.getX(),this.getY());
  }
}
