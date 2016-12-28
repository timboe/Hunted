package timboe.hunted;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import timboe.hunted.render.Textures;
import timboe.hunted.screen.GameScreen;

public class HuntedGame extends Game {

  public static float CAMERA_WIDTH;
  public static float CAMERA_HEIGHT;

  public static final float TILE_SIZE = 32;

  public GameScreen theGameScreen;

  public static boolean debug = true;

  private static HuntedGame self;
  public static HuntedGame get() {
    return self;
  }

  @Override
	public void create () {
    CAMERA_WIDTH = Gdx.graphics.getWidth();
    CAMERA_HEIGHT = Gdx.graphics.getHeight();
    self = this;
    theGameScreen = new GameScreen();
    setScreen(theGameScreen);
  }

//	@Override
//	public void render () {
//    Gdx.gl.glClearColor(1, 0, 0, 1);
//    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//    batch.begin();
//    batch.draw(img, 0, 0);
//    batch.end();
//	}
//
	@Override
	public void dispose () {
    theGameScreen.dispose();
    Textures.getInstance().dispose();
	}
}
