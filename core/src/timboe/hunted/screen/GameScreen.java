package timboe.hunted.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import timboe.hunted.entity.Player;
import timboe.hunted.entity.Tile;
import timboe.hunted.render.HuntedRender;

/**
 * Created by Tim on 28/12/2016.
 */
public class GameScreen extends HuntedRender {

  private Group tileSet;
  private Actor player;

  private final int TILE_W = 100;
  private final int TILE_H = 100;

  @Override
  public void init() {
    tileSet = new Group();
    for (int x = 0; x < TILE_W; ++x) {
      for (int y = 0; y < TILE_H; ++y) {
        tileSet.addActor(new Tile(x,y));
      }
    }
    stage.addActor(tileSet);
    player = new Player();
    stage.addActor(player);
  }

  @Override
  protected void renderBackground() {
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }

  @Override
  public void dispose() {
    tileSet.clearChildren();
    super.dispose();
  }


}
