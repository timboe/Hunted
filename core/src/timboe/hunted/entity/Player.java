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
    texture = Textures.getInstance().dummyPlayer;
    angle = 0;
    setPlayerBody(0.5f, 0.25f);
    torch = new PointLight(Physics.getInstance().rayHandler,
      Param.RAYS,
      Param.FLAME,
      Param.PLAYER_TORCH_STRENGTH,
      0f, 0f);
    torch.attachToBody(body, 0f, 0.25f);
    torch.setIgnoreAttachedBody(true);
  }

  @Override
  public void updatePhysics() {
    super.updatePhysics();
    // Add player smelliness
    getRoomUnderEntity().addToScent( Param.PLAYER_SMELL );
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
    body.setTransform(body.getPosition(), (float)(a - (Math.PI/2)));
  }


}
