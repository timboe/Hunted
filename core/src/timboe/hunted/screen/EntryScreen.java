package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import timboe.hunted.Param;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sounds;
import timboe.hunted.manager.Textures;

/**
 * Created by Tim on 25/01/2017.
 */
public class EntryScreen implements Screen, InputProcessor {

  protected Texture splash;
  private TextureRegion escape0 = Textures.getInstance().getTexture("escape0");
  private TextureRegion escape1 = Textures.getInstance().getTexture("escape1");
  private TextureRegion escape;

  protected OrthographicCamera camera;
  private FitViewport viewPort;
  private Batch batch = new SpriteBatch();
  protected Rectangle buttonRec;
  private Vector2 convert = new Vector2();

  public EntryScreen() {
    buttonRec = new Rectangle(Param.TILE_SIZE * 10, Param.TILE_SIZE * 5,
      escape0.getRegionWidth(), escape0.getRegionHeight());
    escape = escape0;
    Sounds.getInstance().startAmbiance();

    camera = new OrthographicCamera();
    viewPort = new FitViewport(Param.DISPLAY_X, Param.DISPLAY_Y, camera);
    loadBack();
  }

  void loadBack() {
    splash = Textures.getInstance().getSplash();
  }

  @Override
  public void render(float delta) {
    renderClear();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(splash,0,0);
//    camera.position.set(0 - viewPort.getWorldWidth()/2, 0 - viewPort.getWorldHeight()/2, 0);
//    camera.update();
    batch.draw(escape, buttonRec.x, buttonRec.y);
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
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
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

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    convert.set(screenX, screenY);
    convert = viewPort.unproject(convert);
    if (buttonRec.contains(convert)) {
      GameState.getInstance().game.setToGame();
    }
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return mouseMoved(screenX, screenY);
  }
}
