package timboe.hunted.entity;

import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.render.Sprites;


/**
 * Created by Tim on 28/12/2016.
 */
public class Player extends EntityBase {


  public Player() {
    super(0,0);
    setTexture("playerE");
    speed = Param.PLAYER_SPEED;
    setAsPlayerBody(0.5f, 0.25f);
    addTorchToEntity(true, false, true, 0f, Param.PLAYER_FLAME, 0f, 0.25f);
    torchDistanceRef = Param.PLAYER_TORCH_STRENGTH;
  }

  public void updatePhysics() {
    getRoomUnderEntity().addToScent( Param.PLAYER_SMELL );  // Add player smelliness
    flicker();
  }

  public void updateDirection(boolean keyN, boolean keyE, boolean keyS, boolean keyW) {
    if (keyN && keyE) setMoveDirection(Math.PI / 4f, true);
    else if (keyE && keyS) setMoveDirection(7f * Math.PI / 4f, true);
    else if (keyS && keyW) setMoveDirection(5f * Math.PI / 4f, true);
    else if (keyW && keyN) setMoveDirection(3f * Math.PI / 4f, true);
    else if (keyN) setMoveDirection(Math.PI / 2f, true);
    else if (keyE) setMoveDirection(0, true);
    else if (keyS) setMoveDirection(3f * Math.PI / 2f, true);
    else if (keyW) setMoveDirection(Math.PI, true);
    else setMoving(false);
  }




}
