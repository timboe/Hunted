package timboe.hunted.manager;


/**
 * Created by Tim on 15/01/2017.
 */
public class Sounds {

  private static Sounds ourInstance = new Sounds();

  public static Sounds getInstance() {
    return ourInstance;
  }

  private Sounds() {
  }
}
