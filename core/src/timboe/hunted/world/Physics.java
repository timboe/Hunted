package timboe.hunted.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Tim on 30/12/2016.
 */
public class Physics {
  private static Physics ourInstance = new Physics();

  public static Physics getInstance() {
    return ourInstance;
  }

  public World worldBox2D = null;

  private Physics() {
    reset();
  }

  public void reset() {
    dispose();
    worldBox2D = new World(new Vector2(0f, 0f), true);
  }

  public void dispose() {
    if (worldBox2D != null) worldBox2D.dispose();
  }
}
