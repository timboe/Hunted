package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.hunted.HuntedGame;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.render.Textures;
import timboe.hunted.world.Room;

import java.util.Random;

/**
 * Created by Tim on 28/12/2016.
 */
public class Tile extends EntityBase {

  private boolean isFloor = false;
  private boolean hasPhysics = false;
  private boolean isWeb = false;
  private Room myRoom = null;

  public Tile(int x, int y) {
    super(x, y);
    setTexture("pitC");
  }


  public void setIsFloor(Room room) {
    isFloor = true;
    myRoom = room;
    int floor = Utility.r.nextInt(100);
    if (floor <= 22)  setTexture("floor" + Integer.toString(floor));
    else setTexture("floor");
  }

  public void setIsWeb() {
    if (isWeb) return;
    setTexture("webA");
    isWeb = true;
  }

  public Room getTilesRoom() {
    return myRoom;
  }

  public boolean getIsFloor() {
    return isFloor;
  }

  public boolean getHasPhysics() {
    return hasPhysics;
  }

  public void setHasPhysics(boolean p) {
    hasPhysics = p;
  }

//  public void setIsCorridor() {
//    texture =  Textures.getInstance().dummyCorridor;
//  }
}
