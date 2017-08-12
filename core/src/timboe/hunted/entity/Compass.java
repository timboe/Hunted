package timboe.hunted.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.Sprites;

/**
 * Created by Tim on 23/01/2017.
 */
public class Compass extends EntityBase {

  private float angleArrow = 0f;
  private float desiredArrow = 0f;
  private float arrowJitterAmount = .1f;
  private float arrowJitterSpeed = 2f;
  private float arrowOffset = 0f;
  private int arrowN = 0;

  public Compass(int x, int y) {
    super(x,y);
    setTexture("compass",5);
  }

  public void setDesiredArrow(float angle, int N, float jitterArmount, float jitterSpeed) {
    arrowN = N;
    arrowJitterAmount = jitterArmount;
    arrowJitterSpeed = jitterSpeed;
    desiredArrow = Utility.clampSignedAngle(angle);
  }

  @Override
  public void act (float delta) {
    float targetAngle = Utility.clampSignedAngle(Sprites.getInstance().getPlayer().angle);
    float diff = Utility.clampSignedAngle(targetAngle - angle);
    angle = Utility.clampSignedAngle(angle + (delta * diff * Param.COMPASS_SPEED));
    angleArrow = Utility.clampSignedAngle(angleArrow + (delta * diff * Param.COMPASS_SPEED));

    deltaTot += delta;
    if (deltaTot > Param.ANIM_TIME * arrowJitterSpeed) {
      deltaTot -= Param.ANIM_TIME * arrowJitterSpeed;
      arrowOffset = (float)Utility.r.nextGaussian() * arrowJitterAmount;
    }

    float targetArrow = Utility.clampSignedAngle(desiredArrow + arrowOffset);
    float diffArrow = Utility.clampSignedAngle(targetArrow - angleArrow);
    angleArrow = Utility.clampSignedAngle(angleArrow + (delta * diffArrow * Param.ARROW_SPEED));
  }

  @Override
  public void draw(Batch batch, float alpha) {
    batch.draw(textureRegion[0],
      getX(), getY(),
      getWidth()/2, getHeight()/2,
      getWidth(), getHeight(),
      1f,1f, (float)Math.toDegrees(angle));
    batch.draw(textureRegion[arrowN + 1],
      getX(), getY(),
      getWidth()/2, getHeight()/2,
      getWidth(), getHeight(),
      1f,1f, (float)Math.toDegrees(angleArrow));
  }


}
