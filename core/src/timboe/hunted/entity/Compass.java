package timboe.hunted.entity;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.Sprites;

import java.util.Map;

/**
 * Created by Tim on 23/01/2017.
 */
public class Compass extends EntityBase {

  public Compass(int x, int y) {
    super(x,y);
    setTexture("compass");
  }

  @Override
  public void act (float delta) {
    float targetAngle = Utility.clampSignedAngle(Sprites.getInstance().getPlayer().angle - (float)Math.PI/2f);
    float diff = Utility.clampSignedAngle(targetAngle - angle);
    angle += delta * diff * Param.COMPASS_SPEED;
  }

  @Override
  public void draw(Batch batch, float alpha) {
    batch.draw(textureRegion[0],
      getX(), getY(),
      getWidth()/2, getHeight()/2,
      getWidth(), getHeight(),
      1f,1f, (float)Math.toDegrees(angle));
  }


}
