package timboe.hunted.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import timboe.hunted.HuntedGame;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.entity.Switch;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sprites;
import timboe.hunted.manager.Physics;
import timboe.hunted.world.GameCamera;
import timboe.hunted.world.Room;
import timboe.hunted.world.WorldGen;

import java.awt.*;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen implements Screen, InputProcessor {


  private float deltaTot;
  public Stage stage;
  public GameCamera gameCamera = new GameCamera();

  private PerformanceCounter renderStage = new PerformanceCounter("Render-Stage");
  private PerformanceCounter renderLights = new PerformanceCounter("Render-Lights");
  private PerformanceCounter renderUI = new PerformanceCounter("Render-UI");
  private PerformanceCounter allProbe = new PerformanceCounter("ALL");
  private FPSLogger fpsLogger = new FPSLogger();

  private ShapeRenderer shapeRenderer = new ShapeRenderer();
  private boolean fullscreen = false;

  private boolean keyN = false, keyE = false, keyS = false, keyW = false, keyAlt = false;

  private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
  private Matrix4 scaledLightingMatrix;
//  private BitmapFont debugFont = new BitmapFont(); // debug only
  private SpriteBatch debugSpriteBatch = new SpriteBatch(); // debug only
  private SpriteBatch uiBatch = new SpriteBatch();


  private Vector2 screenCentre = new Vector2(Param.DISPLAY_X/2, Param.DISPLAY_Y/2);


  public GameScreen() {
    GameState.getInstance().theGameScreen = this;
  }


  public void init() {
    GameState.getInstance().reset();
  }

  public void reset() {
    if (stage != null) {
      stage.dispose();
    }
    stage = new Stage(new FitViewport(Param.DISPLAY_X, Param.DISPLAY_Y, gameCamera.camera));
    Sprites.getInstance().stage = stage;
    if (HuntedGame.debug) stage.setDebugAll(true);
  }



  @Override
  public void show() {
    Gdx.input.setInputProcessor( this );
    init();
  }

  @Override
  public void hide() {
    Gdx.input.setInputProcessor(null);
//		unHookStage();
  }

  @Override
  public void pause() {
//		unHookStage();
//		Starmap.unHookListners();
  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub
    //ADD RESTORE LINE
//		hookStage();
  }

  protected void renderClear() {
    Gdx.gl.glClearColor(.184f, .157f, .227f, 1);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
  }

  public void resize (int width, int height) {
    Gdx.app.log("Resize", "ReSize in Render ["+this+"] ("+width+","+height+")");
    stage.getViewport().update(width, height, true);
    screenCentre.set(width/2, height/2);
//    gameCamera.cullBox.setWidth(width);
//    gameCamera.cullBox.setHeight(height);
  }

  @Override
  public void render(float delta) {
    allProbe.start();
    deltaTot += delta;

    renderClear();
    renderMain();

    Physics.getInstance().updatePhysics(delta);
    fpsLogger.log();

    allProbe.stop();

    renderStage.tick(delta);
    renderLights.tick(delta);
    renderUI.tick(delta);
    allProbe.tick(delta);
    ++(GameState.getInstance().frame);
  }


  protected void renderMain() {
    renderStage.start();
//        stage.getRoot().setCullingArea( gameCamera.cullBox );
    stage.draw();
    if (HuntedGame.debug) {
      debugSpriteBatch.setProjectionMatrix(stage.getCamera().combined);
      debugSpriteBatch.begin();
//      for (Room room : WorldGen.getInstance().getAllRooms()) {
//        debugFont.draw(debugSpriteBatch, Float.toString(room.getScent() * 100f), room.getX() * Param.TILE_SIZE, room.getY() * Param.TILE_SIZE);
//      }
      debugSpriteBatch.end();
    }
    renderStage.stop();

    renderLights.start();
    if (HuntedGame.lights) {
      scaledLightingMatrix = gameCamera.camera.combined.cpy().scale(Param.TILE_SIZE, Param.TILE_SIZE, 0);
      Physics.getInstance().rayHandler.setCombinedMatrix(scaledLightingMatrix);
      Physics.getInstance().rayHandler.render();
      if (HuntedGame.debug) debugRenderer.render(Physics.getInstance().world, scaledLightingMatrix);
    }
    renderLights.stop();

    renderUI.start();
    renderShapesAndUI();
    renderUI.stop();
  }

  private void renderShapesAndUI() {
    shapeRenderer.setColor(Color.WHITE);
    Gdx.gl.glLineWidth(4);
    shapeRenderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
    shapeRenderer.setTransformMatrix(stage.getBatch().getTransformMatrix());
    // World space TIMERS
    for (int i = 0; i < Param.KEY_ROOMS + 1; ++i) {
      if (GameState.getInstance().progress[i] > 0 && GameState.getInstance().progress[i] < Param.SWITCH_TIME) {
        final float prog = GameState.getInstance().progress[i] / (float) Param.SWITCH_TIME;
        final float xOff = (i == 0) ? -.5f * Param.TILE_SIZE : 0f;
        final float yOff = (i == 0) ? 2f * Param.TILE_SIZE : 1f * Param.TILE_SIZE;
        drawProgressTimer(Sprites.getInstance().keySwitch[i].getX() + xOff,
          Sprites.getInstance().keySwitch[i].getY() + yOff,
          prog);
      }
    }
    // World space Xs
    if (GameState.getInstance().switchStatus[0] && GameState.getInstance().frame % (8*Param.ANIM_SPEED) < 4*Param.ANIM_SPEED) {
      Switch s = Sprites.getInstance().keySwitch[0];
      for (int i = 0; i < Param.KEY_ROOMS; ++i) {
        if (GameState.getInstance().progress[i + 1] < Param.SWITCH_TIME) {
          drawX(s.getX() - Param.TILE_SIZE + (i * Param.TILE_SIZE), s.getY() + Param.TILE_SIZE);
        }
      }
    }
    Gdx.gl.glLineWidth(1);


    uiBatch.setProjectionMatrix(gameCamera.getUISpace());
    uiBatch.begin();
    Sprites.getInstance().treasurePile.draw(uiBatch, 1f);
    uiBatch.end();
  }

  public void drawX(float x, float y) {
    final float off = Param.TILE_SIZE * .4f;
    x += off/2f;
    y += off/2f;
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.rectLine(x, y, x + Param.TILE_SIZE - off, y + Param.TILE_SIZE - off, 8);
    shapeRenderer.rectLine(x + Param.TILE_SIZE - off, y, x, y + Param.TILE_SIZE - off, 8);
    shapeRenderer.end();
  }

  public void drawProgressTimer(float x, float y, float progress) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.circle(x + Param.TILE_SIZE, y + Param.TILE_SIZE, Param.TILE_SIZE);
    progress *= 2*Math.PI;
    shapeRenderer.line(x + Param.TILE_SIZE,
      y + Param.TILE_SIZE,
      x + Param.TILE_SIZE + (float)(Math.sin(progress) * Param.TILE_SIZE),
      y + Param.TILE_SIZE + (float)(Math.cos(progress) * Param.TILE_SIZE));
    shapeRenderer.end();
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.circle(x + Param.TILE_SIZE, y + Param.TILE_SIZE, 4f);
    shapeRenderer.end();
  }

  public void dispose () {
    stage.dispose();
    Gdx.app.log("Perf",allProbe.toString());
    Gdx.app.log("Perf",renderStage.toString());
    Gdx.app.log("Perf",renderLights.toString());
    Gdx.app.log("Perf",renderUI.toString());
  }

  @Override
  public boolean keyDown(int keycode) {
    if (!GameState.getInstance().userControl) {
      keyN = false;
      keyE = false;
      keyS = false;
      keyW = false;
    }
    if (keycode == Input.Keys.LEFT) keyW = true;
    else if (keycode == Input.Keys.RIGHT) keyE = true;
    else if (keycode == Input.Keys.UP) keyN = true;
    else if (keycode == Input.Keys.DOWN) keyS = true;
    else if (keycode == Input.Keys.ALT_LEFT || keycode == Input.Keys.ALT_RIGHT) keyAlt = true;
    if (keycode == Input.Keys.ENTER && keyAlt) {
      fullscreen = !fullscreen;
      if (fullscreen) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
      else Gdx.graphics.setWindowedMode(Param.DISPLAY_X, Param.DISPLAY_Y);
    }
    Sprites.getInstance().getPlayer().updateDirection(keyN, keyE, keyS, keyW);
    GameState.getInstance().movementOn = (keyN || keyE || keyS || keyW);
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if (keycode == Input.Keys.LEFT) keyW = false;
    else if (keycode == Input.Keys.RIGHT) keyE = false;
    else if (keycode == Input.Keys.UP) keyN = false;
    else if (keycode == Input.Keys.DOWN) keyS = false;
    else if (keycode == Input.Keys.ALT_LEFT || keycode == Input.Keys.ALT_RIGHT) keyAlt = false;
    Sprites.getInstance().getPlayer().updateDirection(keyN, keyE, keyS, keyW);
    GameState.getInstance().movementOn = (keyN || keyE || keyS || keyW);
    return false;
  }

  @Override
  public boolean keyTyped(char character) { return false; }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    float angle = Utility.getTargetAngle(screenX, Gdx.graphics.getHeight() - screenY, screenCentre);
    Sprites.getInstance().getPlayer().updateDirection(true, angle);
    GameState.getInstance().movementOn = true;
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    float angle = Utility.getTargetAngle(screenX, Gdx.graphics.getHeight() - screenY, screenCentre);
    Sprites.getInstance().getPlayer().updateDirection(false, angle);
    GameState.getInstance().movementOn = false;
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    float angle = Utility.getTargetAngle(screenX, Gdx.graphics.getHeight() - screenY, screenCentre);
    Sprites.getInstance().getPlayer().updateDirection(true, angle);
    GameState.getInstance().movementOn = true;
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }

}
