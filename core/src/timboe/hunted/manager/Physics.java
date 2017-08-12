package timboe.hunted.manager;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.PerformanceCounter;
import timboe.hunted.HuntedGame;
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
  private Color ambientLightMod = Param.AMBIENT_LIGHT.cpy();
  private float currentReductionPercent = 1f;
  private boolean updateLights;

  private PerformanceCounter physicsProbeBox = new PerformanceCounter("Phys-box");
  private PerformanceCounter physicsProbeLight = new PerformanceCounter("Phys-light");
  private PerformanceCounter physicsProbeSprite = new PerformanceCounter("Phys-sprites");
  private PerformanceCounter physicsProbeStage = new PerformanceCounter("Phys-stage");
  private PerformanceCounter physicsProbeGameState = new PerformanceCounter("Phys-gamestate");
  private PerformanceCounter physicsProbeCamera = new PerformanceCounter("Phys-camera");


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
    return addTorch(x,y,x,y,x,y,false,0f,Param.WALL_FLAME_CAST);
  }

  public void updatePhysics(float delta) {
    physicsProbeBox.start();
    world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    physicsProbeBox.stop();
    //
    physicsProbeLight.start();
    if (HuntedGame.lights) {
      rayHandler.update();
      torchPhysics();
    }
    physicsProbeLight.stop();
    //
    physicsProbeSprite.start();
    Sprites.getInstance().updatePhysics(delta);
    physicsProbeSprite.stop();
    //
    physicsProbeStage.start();
    GameState.getInstance().theGameScreen.stage.act(delta);
    physicsProbeStage.stop();
    //
    physicsProbeGameState.start();
    WorldGen.getInstance().updatePhysics(delta);
    GameState.getInstance().updatePhysics(delta);
    Sprites.getInstance().compass.act(delta);
    physicsProbeGameState.stop();
    //
    physicsProbeCamera.start();
    GameState.getInstance().theGameScreen.gameCamera.updatePhysics(delta);
    physicsProbeCamera.stop();
    //
    physicsProbeBox.tick(delta);
    physicsProbeLight.tick(delta);
    physicsProbeSprite.tick(delta);
    physicsProbeStage.tick(delta);
    physicsProbeGameState.tick(delta);
    physicsProbeCamera.tick(delta);
  }

  public void torchPhysics() {

    for (Torch t : litTorches) {
      t.flicker();
    }

    // Check if torches need dimming
    float distance = Sprites.getInstance().getBigBad().distanceFromPlayer;
    boolean canSeePlayer = Sprites.getInstance().getBigBad().canSeePlayer;
    updateLights = false;
    if (canSeePlayer && distance <= Param.PLAYER_TORCH_STRENGTH) {
      float desiredReductionPercent = distance / Param.PLAYER_TORCH_STRENGTH;
      currentReductionPercent = currentReductionPercent + (0.05f * (desiredReductionPercent - currentReductionPercent));
      updateLights = true;
      resetLights = true;
    } else if (resetLights) {
      float desiredReductionPercent = 1f;
      currentReductionPercent = currentReductionPercent + (0.05f * (desiredReductionPercent - currentReductionPercent));
      updateLights = true;
      if (Math.abs(currentReductionPercent - 1f) < 1e-4) resetLights = false;
    }
    if (updateLights) {
      Sprites.getInstance().getPlayer().modTorch( currentReductionPercent );
      Sprites.getInstance().getBigBad().modTorch( Math.min(currentReductionPercent + 0.5f, 1f) );
      ambientLightMod.a = Param.AMBIENT_LIGHT.a * Math.min(1f, distance / Param.PLAYER_TORCH_STRENGTH);
      rayHandler.setAmbientLight(ambientLightMod);
      for (Torch t : litTorches) {
        t.modTorch( currentReductionPercent );
      }
    }

  }

  public void reset() {
    if (rayHandler != null) rayHandler.dispose();
    if (world != null) world.dispose();

    collisionHandle = new CollisionHandle();
    world = new World(new Vector2(0f, 0f), true);
    world.setContactListener(collisionHandle);
    rayHandler = new RayHandler(world);
    torches = new HashSet<Torch>();
    litTorches = new HashSet<Torch>();

    ambientLightMod = HuntedGame.floodlight ? Param.AMBIENT_FLOODLIGHT.cpy() : Param.AMBIENT_LIGHT.cpy();
    currentReductionPercent = 1f;
    updateLights = true;

    RayHandler.setGammaCorrection(false);     // enable or disable gamma correction
    RayHandler.useDiffuseLight(false);       // enable or disable diffused lighting
    rayHandler.setBlur(true);           // enabled or disable blur
    rayHandler.setBlurNum(1);           // set number of gaussian blur passes
    rayHandler.setShadows(true);        // enable or disable shadow
    rayHandler.setCulling(true);        // enable or disable culling
    rayHandler.setAmbientLight(ambientLightMod);   // set default ambient light
  }

  public void dispose() {
    if (rayHandler != null) rayHandler.dispose();
    if (world != null) world.dispose();
    Gdx.app.log("Perf", physicsProbeBox.toString());
    Gdx.app.log("Perf", physicsProbeLight.toString());
    Gdx.app.log("Perf", physicsProbeSprite.toString());
    Gdx.app.log("Perf", physicsProbeStage.toString());
    Gdx.app.log("Perf", physicsProbeGameState.toString());
    Gdx.app.log("Perf", physicsProbeCamera.toString());
  }
}
