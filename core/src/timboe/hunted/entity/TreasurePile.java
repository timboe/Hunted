package timboe.hunted.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import timboe.hunted.Param;
import timboe.hunted.Utility;
import timboe.hunted.manager.Textures;

import java.util.HashMap;

/**
 * Created by Tim on 21/01/2017.
 */
public class TreasurePile extends EntityBase {
  protected TextureRegion treasure[] = new TextureRegion[Param.N_TREASURE];
  HashMap<Vector2, Integer> myTreasure = new HashMap<Vector2, Integer>();
  final float xOff = 1.5f;
  final float yOff = 0;

  public TreasurePile(int x, int y) {
    super(x, y);
    for (int i = 0; i < Param.N_TREASURE; ++i) {
      treasure[i] = Textures.getInstance().getTexture("treasure" + Integer.toString(i));
    }
  }

  public void addToPile(int ID) {
    float xPos = xOff + (float)MathUtils.clamp(Utility.r.nextGaussian()/2f, -1.5f, 1.5f);
    float yPos = yOff + (float)Math.min(1.5f, Math.abs(Utility.r.nextGaussian()/2f));
    Vector2 v = new Vector2(xPos * Param.TILE_SIZE,yPos * Param.TILE_SIZE);
    myTreasure.put(v, ID);
  }

  @Override
  public void draw(Batch batch, float alpha) {
    for (HashMap.Entry<Vector2, Integer> entry : myTreasure.entrySet()) {
      batch.draw(treasure[entry.getValue()], this.getX() + entry.getKey().x, this.getY() + entry.getKey().y);
    }
  }



}
