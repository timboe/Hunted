package timboe.hunted.world;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import timboe.hunted.Param;
import timboe.hunted.entity.Torch;
import java.util.HashSet;

/**
 * Created by Tim on 30/12/2016.
 */
public class Physics {
  private static Physics ourInstance = new Physics();

  public static Physics getInstance() {
    return ourInstance;
  }

  public World worldBox2D = null;
  public RayHandler rayHandler = null;
  public HashSet<Torch> torches = null;

  private CollisionHandle collisionHandle = null;

  private Physics() {
  }

  public void addTorch(float x, float y, float r) {
    Torch t = new Torch(x,y,r);
    torches.add(t);
  }

  public void reset() {
    dispose();
    collisionHandle = new CollisionHandle();
    worldBox2D = new World(new Vector2(0f, 0f), true);
    worldBox2D.setContactListener(collisionHandle);
    rayHandler = new RayHandler(worldBox2D);
    torches = new HashSet<Torch>();

    RayHandler.setGammaCorrection(false);     // enable or disable gamma correction
    RayHandler.useDiffuseLight(false);       // enable or disable diffused lighting
    rayHandler.setBlur(true);           // enabled or disable blur
    rayHandler.setBlurNum(1);           // set number of gaussian blur passes
    rayHandler.setShadows(true);        // enable or disable shadow
    rayHandler.setCulling(true);        // enable or disable culling
    rayHandler.setAmbientLight(Param.AMBIENT_LIGHT);   // set default ambient light
  }

  public void dispose() {
    if (rayHandler != null) rayHandler.dispose();
    if (worldBox2D != null) worldBox2D.dispose();
  }
}
