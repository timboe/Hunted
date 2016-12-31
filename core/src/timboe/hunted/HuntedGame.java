package timboe.hunted;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import timboe.hunted.render.Sprites;
import timboe.hunted.render.Textures;
import timboe.hunted.screen.GameScreen;
import timboe.hunted.world.Physics;

public class HuntedGame extends Game {

  public static final int TILE_SIZE = 32;
  public static final int TILE_X = 128;
  public static final int TILE_Y = 128;

  public static final float PLAYER_SPEED = 10f;
  public static final float BIGBAD_SPEED = .8f * PLAYER_SPEED;
  public static final float BIGBAD_RUSH = 3f * PLAYER_SPEED;

  public static final int MIN_ROOM_SIZE = 5;
  public static final int CORRIDOR_SIZE = MIN_ROOM_SIZE;

  public GameScreen theGameScreen;



  public static boolean debug = true;

  private static HuntedGame self;
  public static HuntedGame get() {
    return self;
  }

  public static Integer xyToID(int x, int y) {
    assert (y < 1024);
    return (1024 * x) + y;
  }

  @Override
	public void create () {
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
    Sprites.getInstance().dispose();
    Physics.getInstance().dispose();
  }
}
