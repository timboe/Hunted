package timboe.hunted.entity;

import box2dLight.PointLight;
import timboe.hunted.Param;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Physics;



/**
 * Created by Tim on 28/12/2016.
 */
public class Player extends EntityBase {


  public Player() {
    super(0,0);
    setTexture("playerE");
    speed = Param.PLAYER_SPEED;
    setAsPlayerBody(0.5f, 0.25f);
    addTorchToEntity(true,0f, 0.25f);
  }

  public void updatePhysics() {
//    super.updatePhysics();
    // Add player smelliness
    getRoomUnderEntity().addToScent( Param.PLAYER_SMELL );
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
