package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import timboe.hunted.HuntedGame;
import timboe.hunted.entity.Player;
import timboe.hunted.entity.Tile;
import timboe.hunted.render.HuntedRender;
import timboe.hunted.render.Sprites;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen extends HuntedRender {

  private boolean keyN = false, keyE = false, keyS = false, keyW = false;

  @Override
  public void init() {
    stage.addActor(Sprites.getInstance().getTileSet());
    stage.addActor(Sprites.getInstance().getPlayer());
  }

  @Override
  protected void updatePhysics() {
    Sprites.getInstance().getPlayer().updatePhysics();

    float cameraX = Math.max( Sprites.getInstance().getPlayer().getX(), Gdx.graphics.getWidth()/2 );
    cameraX = Math.min( cameraX, (HuntedGame.TILE_W * HuntedGame.TILE_SIZE) - (Gdx.graphics.getWidth()/2) );
    float cameraY = Math.max( Sprites.getInstance().getPlayer().getY(), Gdx.graphics.getHeight()/2 );
    cameraY = Math.min( cameraY, (HuntedGame.TILE_H * HuntedGame.TILE_SIZE) - (Gdx.graphics.getHeight()/2) );

    stage.getCamera().position.set(cameraX, cameraY, 0);

  }

  @Override
  protected void renderBackground() {
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
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
