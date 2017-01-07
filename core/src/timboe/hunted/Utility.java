package timboe.hunted;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.Random;

/**
 * Created by Tim on 31/12/2016.
 */
public class Utility {

  public static Random r = new Random();

  public static Integer xyToID(int x, int y) {
    assert (y < 1024);
    return (1024 * x) + y;
  }

  static public boolean prob(int chanceOfPass) {
    return (r.nextInt(100) + 1) <= chanceOfPass;
  }

  static public boolean prob(float chanceOfPass) {
    return (r.nextFloat() <= chanceOfPass);
  }

  static public float getTargetAngle(Vector2 target, Body from) {
    return getTargetAngle(target, from.getPosition());
  }

  static public float getTargetAngle(Vector2 target, Vector2 from) {
    float targetAngle = (float) Math.atan2(target.y - from.y, target.x - from.x);
    if (targetAngle < 0) targetAngle += (float)2*Math.PI;
    return targetAngle;
  }
}
