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



  public Player() {
    super(0,0);
    texture = Textures.getInstance().dummyPlayer;
    angle = 0;
    setPlayerBody(0.5f, 0.25f);
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
    else moving = false;
  }


  public void setMoveDirection(double a) {
    moving = true;
    angle = (float)a;
  }


}
