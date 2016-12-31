package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import timboe.hunted.Param;
import timboe.hunted.render.HuntedRender;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;
import timboe.hunted.world.WorldGen;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen extends HuntedRender {

  private boolean keyN = false, keyE = false, keyS = false, keyW = false;
  private Rectangle cullBox;

  Box2DDebugRenderer debugRenderer;
  Matrix4 debugMatrix;

  @Override
  public void init() {
    cullBox = new Rectangle(0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2); //TODO remove /2

    WorldGen.getInstance().generateWorld();
    stage.addActor(Sprites.getInstance().getTileSet());
    stage.addActor(Sprites.getInstance().getPlayer());
    stage.addActor(Sprites.getInstance().getBigBad());

    debugRenderer = new Box2DDebugRenderer();
  }

  @Override
  protected void updatePhysics() {

    stage.act(Gdx.graphics.getDeltaTime());
    Sprites.getInstance().getPlayer().updatePhysics();
    Sprites.getInstance().getBigBad().updatePhysics();
    Physics.getInstance().worldBox2D.step(Gdx.graphics.getDeltaTime(), 6, 2);
    Physics.getInstance().rayHandler.update();
    Sprites.getInstance().getPlayer().updatePosition();
    Sprites.getInstance().getBigBad().updatePosition();

//    float cameraX = Math.max( Sprites.getInstance().getPlayer().getX(), Gdx.graphics.getWidth()/2 );
//    cameraX = Math.min( cameraX, (HuntedGame.TILE_X * HuntedGame.TILE_SIZE) - (Gdx.graphics.getWidth()/2) );
//    float cameraY = Math.max( Sprites.getInstance().getPlayer().getY(), Gdx.graphics.getHeight()/2 );
//    cameraY = Math.min( cameraY, (HuntedGame.TILE_Y * HuntedGame.TILE_SIZE) - (Gdx.graphics.getHeight()/2) );

    float cameraX = Sprites.getInstance().getPlayer().getX();
    float cameraY = Sprites.getInstance().getPlayer().getY();

    stage.getCamera().position.set(cameraX, cameraY, 0);
    stage.getCamera().update();
    cullBox.setCenter(cameraX, cameraY);
  }

  @Override
  protected void renderBackground() {
    //stage.getRoot().setCullingArea( cullBox );
    stage.draw();

    debugMatrix = stage.getCamera().combined.cpy().scale(Param.TILE_SIZE, Param.TILE_SIZE, 0);

    Physics.getInstance().rayHandler.setCombinedMatrix(debugMatrix);
    Physics.getInstance().rayHandler.render();

    debugRenderer.render(Physics.getInstance().worldBox2D, debugMatrix);
  }

  @Override
  public boolean keyDown(int keycode) {
    if(keycode == Input.Keys.LEFT)
      keyW = true;
    if(keycode == Input.Keys.RIGHT)
      keyE = true;
    if(keycode == Input.Keys.UP)
      keyN = true;
    if(keycode == Input.Keys.DOWN)
      keyS = true;
    Sprites.getInstance().getPlayer().updateDirection(keyN, keyE, keyS, keyW);
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if(keycode == Input.Keys.LEFT)
      keyW = false;
    if(keycode == Input.Keys.RIGHT)
      keyE = false;
    if(keycode == Input.Keys.UP)
      keyN = false;
    if(keycode == Input.Keys.DOWN)
      keyS = false;
    Sprites.getInstance().getPlayer().updateDirection(keyN, keyE, keyS, keyW);
    return false;
  }

}
