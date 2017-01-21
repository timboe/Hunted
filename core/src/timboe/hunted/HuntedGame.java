package timboe.hunted;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import timboe.hunted.manager.Sounds;
import timboe.hunted.manager.Sprites;
import timboe.hunted.manager.Textures;
import timboe.hunted.screen.GameScreen;
import timboe.hunted.manager.Physics;

public class HuntedGame extends Game {

  private GameScreen theGameScreen;
  public static boolean debug = false;

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
    Sounds.getInstance().dispose();
  }
}
