package timboe.hunted.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Tim on 28/12/2016.
 */
public class Textures {
  private static Textures ourInstance = new Textures();

  public static Textures getInstance() {
    return ourInstance;
  }

  public final Texture dummyDirt;
  public final Texture dummyPlayer;
  public final Texture dummyFloor;

  private Textures() {
    dummyDirt = new Texture(Gdx.files.internal("dummyDirt.png"));
    dummyPlayer = new Texture(Gdx.files.internal("dummyPlayer.png"));
    dummyFloor = new Texture(Gdx.files.internal("dummyFloor.png"));
  }

  public void dispose() {
    dummyDirt.dispose();
    dummyPlayer.dispose();
    dummyFloor.dispose();
  }
}
