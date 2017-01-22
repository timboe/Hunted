package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sprites;

/**
 * Created by Tim on 22/01/2017.
 */
public class GameCamera {

  private float currentZoom = 1f;
  private float desiredZoom = 1f;

  private Vector2 shakePos = new Vector2();
  private Vector2 currentPos = new Vector2();
  private Vector2 desiredPos = new Vector2();

//  public Rectangle cullBox = new Rectangle(0, 0, Param.DISPLAY_X, Param.DISPLAY_Y);

  public OrthographicCamera camera = new OrthographicCamera();

  public GameCamera() {
  }

  public Matrix4 getUISpace() {
    camera.position.set(0f, 0f, 0f);
    camera.zoom = .5f;
    camera.update();
    return camera.combined;
  }

  public void centreOnPlayer() {
    currentPos.set( Sprites.getInstance().getPlayer().getX() + Param.TILE_SIZE/2,
      Sprites.getInstance().getPlayer().getY() + Param.TILE_SIZE/2);
    currentZoom = 0.25f;
    Gdx.app.log("GameScreen","Centred on " + currentPos);
  }

  public void updatePhysics(float delta) {

    float frames = delta / Param.FRAME_TIME;

    final boolean canSeePlayer = Sprites.getInstance().getBigBad().canSeePlayer;
    final float distance = Sprites.getInstance().getBigBad().distanceFromPlayer;
    final boolean endZoom = Sprites.getInstance().getBigBad().isEnd();

    desiredPos.set( Sprites.getInstance().getPlayer().getX() + Param.TILE_SIZE/2,
      Sprites.getInstance().getPlayer().getY() + Param.TILE_SIZE/2);
    float angle =  Sprites.getInstance().getPlayer().getBody().getAngle();

    if (!endZoom) {
      desiredPos.x += Math.cos(angle) * Param.CAMERA_LEAD;
      desiredPos.y += Math.sin(angle) * Param.CAMERA_LEAD;
    }

    float moveSpeed = frames * (endZoom ? .5f : 0.035f);
    currentPos.x = currentPos.x + (moveSpeed * (desiredPos.x - currentPos.x));
    currentPos.y = currentPos.y + (moveSpeed * (desiredPos.y - currentPos.y));

    shakePos.set(currentPos);
    if (canSeePlayer && distance < Param.PLAYER_TORCH_STRENGTH) {
      int shakeAmount = (int)Math.ceil((Param.PLAYER_TORCH_STRENGTH - distance)/2f);
      shakePos.x = shakePos.x - shakeAmount + Utility.r.nextInt(2*shakeAmount);
      shakePos.y = shakePos.y - shakeAmount + Utility.r.nextInt(2*shakeAmount);
    }

    if (GameState.getInstance().movementOn) desiredZoom = .6f;
    else desiredZoom = .4f;

    float aMod = 0;
    if (endZoom) {
      final float mod = (distance - 1f) / Param.BIGBAD_POUNCE_DISTANCE; // Modification due to object size
      aMod = (float)Math.PI * mod * 10f;
      desiredZoom *= mod;
    }

    float zoomSpeed = frames * (endZoom ? .5f : 0.025f);
    currentZoom = currentZoom + (zoomSpeed * (desiredZoom - currentZoom));

    camera.position.set(shakePos, 0);
    camera.zoom = currentZoom;
    camera.up.set(0, 1, 0);
    camera.direction.set(0, 0, -1);
//    if (endZoom) camera.rotate(aMod);

    camera.update();
//    cullBox.setCenter(currentPos);
  }

}
