package timboe.hunted;

import com.badlogic.gdx.Game;
import timboe.hunted.render.Sprites;
import timboe.hunted.render.Textures;
import timboe.hunted.screen.GameScreen;
import timboe.hunted.world.Physics;

public class HuntedGame extends Game {

  private GameScreen theGameScreen;
  public static boolean debug = true;

  @Override
	public void create () {
    theGameScreen = new GameScreen();
    setScreen(theGameScreen);
  }

	@Override
	public void dispose () {
    theGameScreen.dispose();
    Textures.getInstance().dispose();
    Sprites.getInstance().dispose();
    Physics.getInstance().dispose();
  }
}
