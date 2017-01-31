package timboe.hunted.screen;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import timboe.hunted.Param;
import timboe.hunted.manager.Sounds;
import timboe.hunted.manager.Textures;

/**
 * Created by Tim on 31/01/2017.
 */
public class WinScreen extends EntryScreen {

  public WinScreen() {
    super();
  }

  void loadBack() {
    splash = Textures.getInstance().getWin();

    torchLight[0] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_CAST, 600, torchX0, torchY);
    torchLight[0].setXray(true);

    torchLight[1] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_SPOT, 200, torchX0, torchY);
    torchLight[1].setXray(true);

    torchLight[2] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_CAST, 600, torchX1, torchY);
    torchLight[2].setXray(true);

    torchLight[3] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_SPOT, 200, torchX1, torchY);
    torchLight[3].setXray(true);

    particleEffect[0] = new ParticleEffect();
    particleEffect[0].load(Gdx.files.internal("flame.p"), Textures.getInstance().getAtlas());
    particleEffect[0].scaleEffect(1.5f);
    particleEffect[0].setPosition(torchX0, torchY);
    particleEffect[0].start();

    particleEffect[1] = new ParticleEffect();
    particleEffect[1].load(Gdx.files.internal("flame.p"), Textures.getInstance().getAtlas());
    particleEffect[1].scaleEffect(1.5f);
    particleEffect[1].setPosition(torchX1, torchY);
    particleEffect[1].start();
  }


  @Override
  protected void startGame() {
    Sounds.getInstance().startAmbiance();
    super.startGame();
  }

}
