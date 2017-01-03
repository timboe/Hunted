package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import timboe.hunted.Param;
import timboe.hunted.render.HuntedRender;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.Physics;
import timboe.hunted.world.Room;
import timboe.hunted.world.WorldGen;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen extends HuntedRender {

  private boolean keyN = false, keyE = false, keyS = false, keyW = false;
  private Rectangle cullBox;

  Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
  Matrix4 debugMatrix;
  BitmapFont debugFont = new BitmapFont();
  SpriteBatch debugSpriteBatch = new SpriteBatch();

  @Override
  public void init() {
    cullBox = new Rectangle(0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2); //TODO remove /2

    WorldGen.getInstance().generateWorld();
    stage.addActor(Sprites.getInstance().getTileSet());
    stage.addActor(Sprites.getInstance().getPlayer());
    stage.addActor(Sprites.getInstance().getBigBad());

  }

  @Override
  protected void updatePhysics() {

    stage.act(Gdx.graphics.getDeltaTime());

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

    debugSpriteBatch.setProjectionMatrix(stage.getCamera().combined);
    debugSpriteBatch.begin();
    for (Room room : WorldGen.getInstance().getAllRooms()) {
      debugFont.draw(debugSpriteBatch, Float.toString(room.getScent()*100f), room.getX()*Param.TILE_SIZE, room.getY()*Param.TILE_SIZE);
    }
    debugSpriteBatch.end();

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
