package timboe.hunted;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

public class DesktopLauncher {
  public static void main (String[] arg) {
    System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("The Grue");
    config.setWindowSizeLimits(1280,720,1920,1080);
    config.setHdpiMode(HdpiMode.Logical); //useHDPI = true;
    config.useVsync(true);;
    config.setForegroundFPS(60);
    config.setWindowIcon( "ic_launcher_128.png");
    config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
    config.setResizable(false);
    new Lwjgl3Application(new HuntedGame(), config);
  }
}
