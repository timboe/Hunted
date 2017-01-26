package timboe.hunted.screen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.Textures;

/**
 * Created by Tim on 25/01/2017.
 */
public class LooseScreen extends EntryScreen implements Screen, InputProcessor {

  public LooseScreen() {
    super();
    buttonRec.y -= 3 * Param.TILE_SIZE;
  }

  @Override
  public void render(float delta) {
//    int shakeAmount = 3;
//    int shakePosX = 0 - shakeAmount + Utility.r.nextInt(2*shakeAmount);
//    int shakePosY = 0 - shakeAmount + Utility.r.nextInt(2*shakeAmount);
//    camera.position.set(shakePosX - camera.viewportWidth/2, shakePosY - camera.viewportHeight/2, 0);
//    camera.update();
    super.render(delta);
  }

  @Override
  void loadBack() {
    splash = Textures.getInstance().getLoose();
  }

}

