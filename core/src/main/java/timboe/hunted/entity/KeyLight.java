package timboe.hunted.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.manager.GameState;

/**
 * Created by Tim on 16/01/2017.
 */
public class KeyLight extends Torch {

  public int activationID = -1; // Which switch causes me to animate when true? -1 is invalid
  private Color lightColour;
  private String colourStr;
  private float lightMax = 1f;

  public KeyLight(int x, int y, int aID, String tex, int frames) {
    super(x, y);
    activationID = aID;
    switch (aID) {
      case 1: lightColour = Param.LIGHT_RED; colourStr = "Red"; break;
      case 2: lightColour = Param.LIGHT_GREEN; colourStr = "Green"; break;
      case 3: lightColour = Param.LIGHT_BLUE; colourStr = "Blue"; break;
      default:
        Gdx.app.error("Sprites::addKeyShrine","FATAL n = " + aID); Gdx.app.exit();
    }
    if (tex == "lampS") lightMax = .6f;
    else if (tex == "lamp" || tex == "blob") lightMax = .75f;
    lightColour.a = 0f;
    boolean repeat = (frames == 3);
    setTexture(tex + colourStr, frames, repeat);
    Vector2 pos = new Vector2(x,y);
    pos.x += getWidth()/(2*Param.TILE_SIZE);
    pos.y += getHeight()/(2*Param.TILE_SIZE);
    addTorchToEntity(Param.RAYS_SMALL, 180f, Param.SMALL_TORCH_STRENGTH, lightColour, true, pos);
  }

  @Override
  public void act (float delta) {
    if (activationID >= 0 && GameState.getInstance().progress[activationID] == Param.SWITCH_TIME) {
      deltaTot += delta;
      if (deltaTot > Param.ANIM_TIME) {
        deltaTot -= Param.ANIM_TIME;
        ++currentFrame;
      }
      if (nFrames == 2) {
        if (currentFrame % nFrames == 0) lightColour.a = 0f;
        else lightColour.a = 1f * lightMax;
      } else if (nFrames == 4) {
        switch (currentFrame % nFrames) {
          case 0: lightColour.a = 0f; break;
          case 1: case 3: lightColour.a = .5f * lightMax; break;
          case 2: lightColour.a = 1f * lightMax; break;
        }
      }
      torchLight[0].setColor(lightColour);
    }
  }
}
