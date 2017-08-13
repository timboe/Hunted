package timboe.hunted.screen;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.Sounds;
import timboe.hunted.manager.Textures;

/**
 * Created by Tim on 25/01/2017.
 */
public class LooseScreen extends EntryScreen implements Screen, InputProcessor {

  Vector3 shakePos = new Vector3();

  public LooseScreen() {
    super();
    buttonRec.y -= 3 * Param.TILE_SIZE;
  }

  @Override
  protected void startGame() {
    Sounds.getInstance().startAmbiance();
    super.startGame();
  }

  @Override
  public void render(float delta) {
    shakePos.set(camera.position);
    int shakeAmount = 3;
    float shakePosX = camera.position.x - shakeAmount + Utility.r.nextInt(2*shakeAmount);
    float shakePosY = camera.position.y - shakeAmount + Utility.r.nextInt(2*shakeAmount);
    camera.position.set(shakePosX, shakePosY, 0);
    camera.update();

    renderClear();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(splash,0,0);
    batch.end();

    rayHandler.setCombinedMatrix(camera);
    rayHandler.updateAndRender();

    camera.position.set(shakePos);
    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(escape, buttonRec.x, buttonRec.y);
    if (Sounds.getInstance().soundsOn) {
      batch.draw(volOn, volRec.x, volRec.y);
    } else {
      batch.draw(volOff, volRec.x, volRec.y);
    }
    batch.end();
  }

  protected void renderClear() {
    Gdx.gl.glClearColor(0.5f, .226f, .273f, 1);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
  }

  @Override
  void loadBack() {
    splash = Textures.getInstance().getLoose();

    volOff = Textures.getInstance().getTexture("volume_off");
    volOn = Textures.getInstance().getTexture("volume_on");

    torchLight[0] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_CAST, 400, 465, 560);
    torchLight[0].setXray(true);

    torchLight[1] = new PointLight(rayHandler, Param.RAYS_BIGBAD, Param.WALL_FLAME_SPOT, 400, 815, 560);
    torchLight[1].setXray(true);
  }

}

