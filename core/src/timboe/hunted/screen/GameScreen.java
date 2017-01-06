package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.HuntedRender;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;
import timboe.hunted.world.Room;
import timboe.hunted.world.WorldGen;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen extends HuntedRender {

  private boolean keyN = false, keyE = false, keyS = false, keyW = false;
  private Rectangle cullBox;

  Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
  Matrix4 debugMatrix;
  BitmapFont debugFont = new BitmapFont();
  SpriteBatch debugSpriteBatch = new SpriteBatch();

  Vector2 shakePos = new Vector2();
  Vector2 currentPos = new Vector2();
  Vector2 desiredPos = new Vector2();
  float currentZoom = 1f;
  float desiredZoom = 1f;

  @Override
  public void init() {
    cullBox = new Rectangle(0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2); //TODO remove /2

    WorldGen.getInstance().generateWorld();
    stage.addActor(Sprites.getInstance().getTileSet());
    stage.addActor(Sprites.getInstance().getPlayer());
    stage.addActor(Sprites.getInstance().getBigBad());

  }

  @Override
  protected void updatePhysics() {

    stage.act(Gdx.graphics.getDeltaTime());

    desiredPos.set( Sprites.getInstance().getPlayer().getX(), Sprites.getInstance().getPlayer().getY() );
    float angle =  Sprites.getInstance().getPlayer().getBody().getAngle();

    desiredPos.x += Math.cos(angle) * Param.CAMERA_LEAD;
    desiredPos.y += Math.sin(angle) * Param.CAMERA_LEAD;

    currentPos.x = currentPos.x + (0.07f * (desiredPos.x - currentPos.x));
    currentPos.y = currentPos.y + (0.07f * (desiredPos.y - currentPos.y));

    final boolean canSeePlayer = Sprites.getInstance().getBigBad().canSeePlayer;
    final float distance = Sprites.getInstance().getBigBad().distanceFromPlayer;

    shakePos.set(currentPos);
    if (canSeePlayer && distance < Param.PLAYER_TORCH_STRENGTH) {
      int shakeAmount = (int)Math.ceil((Param.PLAYER_TORCH_STRENGTH - distance)/2f);
      shakePos.x = shakePos.x - shakeAmount + Utility.r.nextInt(2*shakeAmount);
      shakePos.y = shakePos.y - shakeAmount + Utility.r.nextInt(2*shakeAmount);
    }


      if (keyN || keyE || keyS || keyW) desiredZoom = .6f;
    else desiredZoom = .4f;

//    if (desiredZoom > currentZoom)
      currentZoom = currentZoom + (0.05f * (desiredZoom - currentZoom));
//    else currentZoom = currentZoom - (0.07f * (currentZoom - desiredZoom));

    camera.position.set(shakePos, 0);
    camera.zoom = currentZoom;
//    cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth);
//    stage.getCamera().
//    float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
//    float effectiveViewportHeight = cam.viewportHeight * cam.zoom;
//
//    cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
//    cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
//    stage.getCamera().
    camera.update();
    cullBox.setCenter(currentPos);
  }

  @Override
  protected void renderBackground() {
    //stage.getRoot().setCullingArea( cullBox );
    stage.draw();

    debugSpriteBatch.setProjectionMatrix(stage.getCamera().combined);
    debugSpriteBatch.begin();
    for (Room room : WorldGen.getInstance().getAllRooms()) {
      debugFont.draw(debugSpriteBatch, Float.toString(room.getScent()*100f), room.getX()*Param.TILE_SIZE, room.getY()*Param.TILE_SIZE);
    }
    debugSpriteBatch.end();

    debugMatrix = camera.combined.cpy().scale(Param.TILE_SIZE, Param.TILE_SIZE, 0);

    Physics.getInstance().rayHandler.setCombinedMatrix(debugMatrix);
    Physics.getInstance().rayHandler.render();

    debugRenderer.render(Physics.getInstance().worldBox2D, debugMatrix);
  }

  @Override
  public boolean keyDown(int keycode) {
    if(keycode == Input.Keys.LEFT)
      keyW = true;
    if(keycode == Input.Keys.RIGHT)
      keyE = true;
    if(keycode == Input.Keys.UP)
      keyN = true;
    if(keycode == Input.Keys.DOWN)
      keyS = true;
    Sprites.getInstance().getPlayer().updateDirection(keyN, keyE, keyS, keyW);
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if(keycode == Input.Keys.LEFT)
      keyW = false;
    if(keycode == Input.Keys.RIGHT)
      keyE = false;
    if(keycode == Input.Keys.UP)
      keyN = false;
    if(keycode == Input.Keys.DOWN)
      keyS = false;
    Sprites.getInstance().getPlayer().updateDirection(keyN, keyE, keyS, keyW);
    return false;
  }

}
