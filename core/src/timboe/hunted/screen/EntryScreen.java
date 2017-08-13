package timboe.hunted.screen;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import timboe.hunted.Param;
import timboe.hunted.entity.Torch;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Physics;
import timboe.hunted.manager.Sounds;
import timboe.hunted.manager.Textures;

import java.util.HashSet;

/**
 * Created by Tim on 25/01/2017.
 */
public class EntryScreen implements Screen, InputProcessor {

  protected Texture splash = null;
  protected Texture title = null;
  protected TextureRegion volOff = null;
  protected TextureRegion volOn = null;
  private TextureRegion escape0 = Textures.getInstance().getTexture("escape0");
  private TextureRegion escape1 = Textures.getInstance().getTexture("escape1");
  protected TextureRegion escape;

  protected OrthographicCamera camera;
  private FitViewport viewPort;
  protected Batch batch = new SpriteBatch();
  protected Rectangle buttonRec;
  protected Rectangle volRec;
  private Vector2 convert = new Vector2();

  public PositionalLight[] torchLight = {null,null,null,null};
  public ParticleEffect[] particleEffect = {null,null};

  protected RayHandler rayHandler;

  private boolean keyAlt = false;

  protected int torchX0 = 150;
  protected int torchX1 = 1130;
  protected int torchY = 720 - 420;

  public EntryScreen() {
    escape = escape0;

    Sounds.getInstance().startAmbiance();

    rayHandler = new RayHandler(new World(new Vector2(0,0), true));
    RayHandler.setGammaCorrection(false);     // enable or disable gamma correction
    RayHandler.useDiffuseLight(false);       // enable or disable diffused lighting
    rayHandler.setBlur(true);           // enabled or disable blur
    rayHandler.setBlurNum(1);           // set number of gaussian blur passes
    rayHandler.setShadows(true);        // enable or disable shadow
    rayHandler.setCulling(true);        // enable or disable culling
    rayHandler.setAmbientLight(Param.AMBIENT_LIGHT);   // set default ambient light

    camera = new OrthographicCamera();
    viewPort = new FitViewport(Param.DISPLAY_X, Param.DISPLAY_Y, camera);

    loadBack();

    buttonRec = new Rectangle(Param.TILE_SIZE * 10, Param.TILE_SIZE * 5,
            escape0.getRegionWidth(), escape0.getRegionHeight());

    volRec = new Rectangle(Param.DISPLAY_X - volOn.getRegionWidth(), Param.DISPLAY_Y - volOn.getRegionHeight(),
            volOn.getRegionWidth(), volOn.getRegionHeight());

  }

  void loadBack() {
    splash = Textures.getInstance().getSplash();
    title = Textures.getInstance().getTitle();
    volOff = Textures.getInstance().getTexture("volume_off");
    volOn = Textures.getInstance().getTexture("volume_on");

    torchLight[0] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_CAST, 600, torchX0, torchY);
    torchLight[0].setXray(true);

    torchLight[1] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_SPOT, 200, torchX0, torchY);
    torchLight[1].setXray(true);

    torchLight[2] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_CAST, 600, torchX1, torchY);
    torchLight[2].setXray(true);

    torchLight[3] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_SPOT, 200, torchX1, torchY);
    torchLight[3].setXray(true);

    particleEffect[0] = new ParticleEffect();
    particleEffect[0].load(Gdx.files.internal("flame.p"), Textures.getInstance().getAtlas());
    particleEffect[0].scaleEffect(1.5f);
    particleEffect[0].setPosition(torchX0, torchY);
    particleEffect[0].start();

    particleEffect[1] = new ParticleEffect();
    particleEffect[1].load(Gdx.files.internal("flame.p"), Textures.getInstance().getAtlas());
    particleEffect[1].scaleEffect(1.5f);
    particleEffect[1].setPosition(torchX1, torchY);
    particleEffect[1].start();
  }

  @Override
  public void render(float delta) {
    renderClear();
    particleEffect[0].update(delta);
    particleEffect[1].update(delta);
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(splash,0,0);
    if (particleEffect[0] != null) particleEffect[0].draw(batch);
    if (particleEffect[1] != null) particleEffect[1].draw(batch);
    batch.end();

    rayHandler.setCombinedMatrix(camera);
    rayHandler.updateAndRender();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(escape, buttonRec.x, buttonRec.y);
    if (title != null) batch.draw(title, Param.DISPLAY_X/2 - title.getWidth()/2, Param.DISPLAY_Y/2);
    if (Sounds.getInstance().getSoundsOn()) {
      batch.draw(volOn, volRec.x, volRec.y);
    } else {
      batch.draw(volOff, volRec.x, volRec.y);
    }
    batch.end();
  }

  protected void renderClear() {
    Gdx.gl.glClearColor(.184f, .157f, .227f, 1);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
  }

  @Override
  public void dispose() {
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor( this );
  }

  @Override
  public void hide() {
    Gdx.input.setInputProcessor( null );
  }

  @Override
  public void pause() {
  }

  @Override
  public void resize(int width, int height) {
    Gdx.app.log("Resize", "ReSize in ["+this+"] ("+width+","+height+")");
    viewPort.update(width, height, true);
    camera.update();
  }

  @Override
  public void resume() {
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.ALT_LEFT || keycode == Input.Keys.ALT_RIGHT) keyAlt = true;
    if (keycode == Input.Keys.ENTER && keyAlt) {
      GameState.getInstance().fullscreen = !GameState.getInstance().fullscreen;
      if (GameState.getInstance().fullscreen) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
      else Gdx.graphics.setWindowedMode(Param.DISPLAY_X, Param.DISPLAY_Y);
    }
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if (keycode == Input.Keys.ALT_LEFT || keycode == Input.Keys.ALT_RIGHT) keyAlt = false;
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    convert.set(screenX, screenY);
    convert = viewPort.unproject(convert);
    if (buttonRec.contains(convert)) {
      escape = escape1;
    } else {
      escape = escape0;
    }
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return mouseMoved(screenX, screenY);
  }

  protected void startGame() {
    GameState.getInstance().game.setToGame();
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    convert.set(screenX, screenY);
    convert = viewPort.unproject(convert);
    if (buttonRec.contains(convert)) {
      startGame();
      escape = escape0;
    } else if (volRec.contains(convert)) {
       Sounds.getInstance().toggleSounds();
    }
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return mouseMoved(screenX, screenY);
  }
}
