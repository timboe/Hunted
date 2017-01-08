package timboe.hunted.world;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import timboe.hunted.Param;
import timboe.hunted.entity.Torch;
import timboe.hunted.render.Sprites;

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
  public HashSet<Torch> litTorches = null;
  Color ambientLightMod = Param.AMBIENT_LIGHT.cpy();
  float currentReductionPercent = 1f;

  public boolean switchEntry = false;
  public boolean[] switchKeyRoom = new boolean[Param.KEY_ROOMS];

  private CollisionHandle collisionHandle = null;
  private boolean resetLights = false;

  private Physics() {
  }

  public Torch addTorch(float x, float y, float lX, float lY, float r, boolean partial, float angle) {
    Torch t = new Torch(x,y,lX,lY,r,partial,angle);
    torches.add(t);
    return t;
  }

  public Torch addTorch(float x, float y, float lX, float lY, float r) {
    return addTorch(x,y,lX,lY,r,false,0f);
  }

  public Torch addTorch(float x, float y, float r) {
    return addTorch(x,y,x,y,r,false,0f);
  }

  public void updatePhysics() {
    Sprites.getInstance().getPlayer().updatePhysics();
    Sprites.getInstance().getBigBad().updatePhysics();
    Physics.getInstance().worldBox2D.step(Gdx.graphics.getDeltaTime(), 6, 2);
    Physics.getInstance().rayHandler.update();
    Sprites.getInstance().getPlayer().updatePosition();
    Sprites.getInstance().getBigBad().updatePosition();

    for (Room room : WorldGen.getInstance().getAllRooms()) {
      room.updatePhysics(); // Smell dissipation
    }


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
      Sprites.getInstance().getPlayer().torchDistanceRef = Param.PLAYER_TORCH_STRENGTH * currentReductionPercent;
      Sprites.getInstance().getBigBad().torchDistanceRef = Math.min(Param.PLAYER_TORCH_STRENGTH * currentReductionPercent + 5f, Param.PLAYER_TORCH_STRENGTH);
      ambientLightMod.a = Param.AMBIENT_LIGHT.a * (distance / (float)Param.PLAYER_TORCH_STRENGTH);
      rayHandler.setAmbientLight(ambientLightMod);
      for (Torch t : litTorches) {
        t.torchDistanceRef = Param.WALL_TORCH_STRENGTH * currentReductionPercent;
      }
    }

    for (Torch t : litTorches) {
      t.flicker();
    }

  }

  public void reset() {
    dispose();
    collisionHandle = new CollisionHandle();
    worldBox2D = new World(new Vector2(0f, 0f), true);
    worldBox2D.setContactListener(collisionHandle);
    rayHandler = new RayHandler(worldBox2D);
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
    if (worldBox2D != null) worldBox2D.dispose();
  }
}
