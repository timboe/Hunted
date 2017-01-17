package timboe.hunted.manager;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import timboe.hunted.Param;
import timboe.hunted.entity.Torch;
import timboe.hunted.world.CollisionHandle;
import timboe.hunted.world.WorldGen;

import java.util.HashSet;

/**
 * Created by Tim on 30/12/2016.
 */
public class Physics {
  private static Physics ourInstance = new Physics();

  public static Physics getInstance() {
    return ourInstance;
  }

  public World world = null;
  public RayHandler rayHandler = null;
  public HashSet<Torch> torches = null;
  public HashSet<Torch> litTorches = null;
  Color ambientLightMod = Param.AMBIENT_LIGHT.cpy();
  float currentReductionPercent = 1f;

  private CollisionHandle collisionHandle = null;
  private boolean resetLights = false;

  private Physics() {
  }

  public Torch addTorch(float x, float y, float lX, float lY, float sX, float sY, boolean partial, float angle, Color c) {
    Torch t = new Torch(x,y,lX,lY,sX,sY,partial,angle,c);
    torches.add(t);
    return t;
  }

  public Torch addTorch(float x, float y) {
    return addTorch(x,y,x,y,x,y,false,0f,Param.WALL_FLAME_CAST_C);
  }

  public void updatePhysics() {
    GameState.getInstance().updatePhysics();
    Sprites.getInstance().updatePhysics();
    world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    rayHandler.update();
    Sprites.getInstance().updatePosition();
    WorldGen.getInstance().updatePhysics();
    GameState.getInstance().theGameScreen.updatePhysics();
    torchPhysics();

  }

  public void torchPhysics() {
    // Check if torches need dimming
    float distance = Sprites.getInstance().getBigBad().distanceFromPlayer;
    boolean canSeePlayer = Sprites.getInstance().getBigBad().canSeePlayer;
    boolean update = false;
    if (canSeePlayer && distance <= Param.PLAYER_TORCH_STRENGTH) {
      float desiredReductionPercent = distance / (float)Param.PLAYER_TORCH_STRENGTH;
      currentReductionPercent = currentReductionPercent + (0.05f * (desiredReductionPercent - currentReductionPercent));
      update = true;
      resetLights = true;
    } else if (resetLights) {
      float desiredReductionPercent = 1f;
      currentReductionPercent = currentReductionPercent + (0.05f * (desiredReductionPercent - currentReductionPercent));
      update = true;
      if (Math.abs(currentReductionPercent - 1f) < 1e-4) resetLights = false;
    }
    if (update) {
      Sprites.getInstance().getPlayer().modTorch( currentReductionPercent );
      Sprites.getInstance().getBigBad().modTorch( Math.min(currentReductionPercent + 0.5f, 1f) );
      ambientLightMod.a = Param.AMBIENT_LIGHT.a * (distance / (float)Param.PLAYER_TORCH_STRENGTH);
      rayHandler.setAmbientLight(ambientLightMod);
      for (Torch t : litTorches) {
        t.modTorch( currentReductionPercent );
      }
    }

    for (Torch t : litTorches) {
      t.flicker();
    }
  }

  public void reset() {
    dispose();
    collisionHandle = new CollisionHandle();
    world = new World(new Vector2(0f, 0f), true);
    world.setContactListener(collisionHandle);
    rayHandler = new RayHandler(world);
    torches = new HashSet<Torch>();
    litTorches = new HashSet<Torch>();

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
    if (world != null) world.dispose();
  }
}
