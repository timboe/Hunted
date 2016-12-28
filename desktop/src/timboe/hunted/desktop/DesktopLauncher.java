package timboe.hunted.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import timboe.hunted.HuntedGame;

public class DesktopLauncher {
  public static void main (String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "Hunted";
    config.width = 1280;
    config.height = 800;
    config.vSyncEnabled = true;

    new LwjglApplication(new HuntedGame(), config);
  }
}
