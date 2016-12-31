package timboe.hunted;

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
}
