package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Textures;

/**
 * Created by Tim on 28/12/2016.
 */
public class Tile extends Actor {

  Texture texture = Textures.getInstance().dummyDirt;
  boolean isFloor;

  public Tile(int x, int y){
    setX(x * HuntedGame.TILE_SIZE);
    setY(y * HuntedGame.TILE_SIZE);
    setBounds(getX(),getY(),HuntedGame.TILE_SIZE,HuntedGame.TILE_SIZE);
    isFloor = false;
  }

  public void setIsFloor() {
    isFloor = true;
    texture =  Textures.getInstance().dummyFloor;

  }

  @Override
  public void draw(Batch batch, float alpha){
    Gdx.app.log("dgb", "drawing ["+this+"] ("+getX()/32+","+getY()/32+")");
    batch.draw(texture,this.getX(),this.getY());
  }
}
