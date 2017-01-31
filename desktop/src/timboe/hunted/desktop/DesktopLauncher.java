package timboe.hunted.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import timboe.hunted.HuntedGame;

public class DesktopLauncher {
  public static void main (String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "The Grue";
    config.width = 1280;
    config.height = 720;
    config.vSyncEnabled = true;
    config.foregroundFPS = 60;
    config.samples = 2;
    config.addIcon("icon.png", Files.FileType.Internal);
    new LwjglApplication(new HuntedGame(), config);
  }
}
