package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.HuntedRender;
import timboe.hunted.render.Sprites;
import timboe.hunted.world.WorldGen;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen extends HuntedRender {

  private boolean keyN = false, keyE = false, keyS = false, keyW = false;
  private Rectangle cullBox;




  @Override
  public void init() {
    cullBox = new Rectangle(0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2); //TODO remove /2
    stage.addActor(Sprites.getInstance().getTileSet());
    stage.addActor(Sprites.getInstance().getPlayer());
    WorldGen.getInstance().generateWorld();
  }

  @Override
  protected void updatePhysics() {
    Sprites.getInstance().getPlayer().updatePhysics();

    float cameraX = Math.max( Sprites.getInstance().getPlayer().getX(), Gdx.graphics.getWidth()/2 );
    cameraX = Math.min( cameraX, (HuntedGame.TILE_X * HuntedGame.TILE_SIZE) - (Gdx.graphics.getWidth()/2) );
    float cameraY = Math.max( Sprites.getInstance().getPlayer().getY(), Gdx.graphics.getHeight()/2 );
    cameraY = Math.min( cameraY, (HuntedGame.TILE_Y * HuntedGame.TILE_SIZE) - (Gdx.graphics.getHeight()/2) );

    stage.getCamera().position.set(cameraX, cameraY, 0);
    cullBox.setCenter(cameraX, cameraY);
  }

  @Override
  protected void renderBackground() {
    stage.act(Gdx.graphics.getDeltaTime());
    //stage.getRoot().setCullingArea( cullBox );
    stage.draw();
//    if (HuntedGame.debug) { // Draw chunk boundaries
//      shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
//      shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//      shapeRenderer.setColor(1, 0, 0, 1);
//      for (int cX = 0; cX < HuntedGame.CHUNKS_X; ++cX) {
//        for (int cY = 0; cY < HuntedGame.CHUNKS_Y; ++cY) {
//          final int size = HuntedGame.CHUNK_SIZE * HuntedGame.TILE_SIZE;
//          shapeRenderer.rect(cX * size,cY * size, size, size);
//        }
//      }
//      shapeRenderer.end();
//    }
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
