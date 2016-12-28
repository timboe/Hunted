package timboe.hunted.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import timboe.hunted.HuntedGame;

/**
 * Created by Tim on 28/12/2016.
 */
public class HuntedRender  implements Screen {

//  protected float deltaTot;
//  protected Table table; // UI
  protected Stage stage;

  protected GestureDetector gestureDetector = null;


  public HuntedRender() {
    stage = new Stage(new ScreenViewport()); //TODO choose a better renderer here
    if (HuntedGame.debug) stage.setDebugAll(true);
  }

  public void init() {
  }

  @Override
  public void dispose () {
  }

  @Override
  public void render(float delta) {
//    if (textureBuffer == true) {
//      if(frameBuffer == null) {
//        // m_fboScaler increase or decrease the antialiasing quality
//        frameBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
//        frameTexRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
//        frameTexRegion.flip(false, true);
//      }
//      frameBuffer.begin();
//    }

    renderClear();
    renderBackground();
//    renderFX(delta);
//    renderForeground(delta);
//    renderFade(delta);
//    if (textureBuffer == true) {
//      if(frameBuffer != null) {
//        frameBuffer.end();
//        spriteBatch.begin();
//        spriteBatch.draw(frameTexRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        spriteBatch.end();
//      }
//    }
  }


  protected void renderClear() {
    Gdx.gl.glClearColor(0f, 0f, 0f, 1);
    Gdx.graphics.getGL20().glClear(GL20 .GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
  }

  protected void renderBackground() {
    // Override me
  }

  public void resize (int width, int height) {
    Gdx.app.log("Resize", "ReSize in Render ["+this+"] ("+width+","+height+")");
    //Constrain
//		stage.setViewport(SpaceTrade.CAMERA_WIDTH, SpaceTrade.CAMERA_HEIGHT, true);
//		stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
    //Dont cnstrain

    //stage.setViewport(HuntedGame.CAMERA_WIDTH, HuntedGame.CAMERA_HEIGHT, false);


//    blackSquare.setWidth(Gdx.graphics.getWidth()*10);
//    blackSquare.setHeight(Gdx.graphics.getHeight()*10);

    //screenCam = new OrthographicCamera();
//    transform_BG = screenCam.combined.cpy();
//    transform_BG.scale(2f/SpaceTrade.CAMERA_WIDTH, 2f/SpaceTrade.CAMERA_HEIGHT, 0f);
//    transform_BG.translate(-SpaceTrade.CAMERA_WIDTH/2f, -SpaceTrade.CAMERA_HEIGHT/2f, 0f);

    //planetCam = new OrthographicCamera();


//    if (secondaryStage != null) {
//      secondaryStage.setCamera(stage.getCamera());
//    }
  }

  @Override
  public void show() {
//    if (secondaryStage == null && gestureDetector == null) {
      Gdx.input.setInputProcessor( stage );
//    } else {
//      if (inputMultiplex == null) {
//        inputMultiplex = new InputMultiplexer( stage );
//        if (secondaryStage != null) inputMultiplex.addProcessor(secondaryStage);
//        if (gestureDetector != null) inputMultiplex.addProcessor(gestureDetector);
//      }
//      Gdx.input.setInputProcessor( inputMultiplex );
//    }
//		hookStage();
    init();
  }

  @Override
  public void hide() {
    Gdx.input.setInputProcessor(null);
//		unHookStage();
  }

  @Override
  public void pause() {
//		unHookStage();
//		Starmap.unHookListners();
  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub
    //ADD RESTORE LINE
//		hookStage();
  }

}
