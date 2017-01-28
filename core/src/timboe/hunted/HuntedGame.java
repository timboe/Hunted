package timboe.hunted;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import timboe.hunted.manager.*;
import timboe.hunted.screen.EntryScreen;
import timboe.hunted.screen.GameScreen;
import timboe.hunted.screen.LooseScreen;

public class HuntedGame extends Game {

  private GameScreen theGameScreen;
  private LooseScreen theLooseScreen;
  private EntryScreen theEntryScreen;

  public static boolean debug = false;
  public static boolean lights = true;
  public static boolean particles = true;
  public static boolean world = true;
  public static boolean physicsChests = false;
  public static boolean sounds = true;
  public static boolean zoomOut = true;

  @Override
	public void create () {
    GameState.getInstance().game = this;
    theGameScreen = new GameScreen();
    theLooseScreen = new LooseScreen();
    theEntryScreen = new EntryScreen();
    setToEntry();
  }

  public void setToGame() {
    setScreen(theGameScreen);
  }

  public void setToEntry() {
    setScreen(theEntryScreen);
  }

  public void setToLoose() {
    setScreen(theLooseScreen);
  }

	@Override
	public void dispose () {
    theGameScreen.dispose();
    theEntryScreen.dispose();
    theLooseScreen.dispose();
    Textures.getInstance().dispose();
    Sprites.getInstance().dispose();
    Physics.getInstance().dispose();
    Sounds.getInstance().dispose();
  }
}
